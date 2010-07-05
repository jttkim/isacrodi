package org.isacrodi.diagnosis;

import org.isacrodi.ejb.entity.*;
import java.util.Set;
import java.util.Vector;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collection;

import java.io.IOException;
import java.io.*;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;

// FIXME: should be more specific about imports (!!)
import edu.berkeley.compbio.jlibsvm.*;
import edu.berkeley.compbio.jlibsvm.kernel.*;
import edu.berkeley.compbio.jlibsvm.util.*;
import edu.berkeley.compbio.jlibsvm.binary.*;
import edu.berkeley.compbio.jlibsvm.multi.*;
import edu.berkeley.compbio.jlibsvm.labelinverter.*;
import edu.berkeley.compbio.jlibsvm.scaler.*;

// FIXME: needed for retrofitting
import libsvm.svm_node;


/**
 * Diagnosis based on SVM classification by jlibsvm
 */
public class JSVMDiagnosisProvider implements DiagnosisProvider
{
  private Map<String, Integer> featureNameIndexMap;
  private Map<String, Integer> disorderScientificNameIndexMap;
  private CDRFeatureExtractor cdrFeatureExtractor;
  private SvmNodeFeatureVectorMapper featureVectorMapper;
  private MultiClassModel<String, SparseVector> model;
  private Map<String, CropDisorder> cropDisorderMap;


  public JSVMDiagnosisProvider()
  {
    super();
    this.featureNameIndexMap = new HashMap<String, Integer>();
    this.disorderScientificNameIndexMap = new HashMap<String, Integer>();
    this.cdrFeatureExtractor = new DummyCDRFeatureExtractor();
    this.featureVectorMapper = null;
    this.model = null;
    this.cropDisorderMap = null;
  }


  protected int maxFeatureIndex()
  {
    int maxIndex = -1;
    for (Integer i : this.featureNameIndexMap.values())
    {
      int index = i.intValue();
      if (index > maxIndex)
      {
	maxIndex = i;
      }
    }
    if (maxIndex == -1)
    {
      throw new RuntimeException("no feature indices");
    }
    return (maxIndex);
  }


  protected SparseVector mapToSparseVector(FeatureVector featureVector)
  {
    // FIXME: retrofitting SparseVector from svm_node ...
    // System.err.printf("mapToSparseVector: feature vector = %s\n", featureVector.toString());
    svm_node[] nodeArray = this.featureVectorMapper.map(featureVector);
    SparseVector sparseVector = new SparseVector(nodeArray.length);
    for (int i = 0; i < nodeArray.length; i++)
    {
      sparseVector.indexes[i] = nodeArray[i].index;
      sparseVector.values[i] = (float) nodeArray[i].value;
    }
    return (sparseVector);
  }


  protected Map<SparseVector, String> extractSampleLabelMap(Collection<CropDisorderRecord> cropDisorderRecordCollection)
  {
    Map<SparseVector, String> sampleLabelMap = new HashMap<SparseVector, String>();
    for (CropDisorderRecord cropDisorderRecord : cropDisorderRecordCollection)
    {
      CropDisorder diagnosedCropDisorder = cropDisorderRecord.getExpertDiagnosedCropDisorder();
      if (diagnosedCropDisorder == null)
      {
	throw new IllegalArgumentException("unlabelled CDR");
      }
      FeatureVector featureVector = this.cdrFeatureExtractor.extract(cropDisorderRecord);
      SparseVector sparseVector = this.mapToSparseVector(featureVector);
      sampleLabelMap.put(sparseVector, diagnosedCropDisorder.getScientificName());
    }
    return (sampleLabelMap);
  }


  private static Map<SparseVector, Integer> idMap(Map<SparseVector, String> examples)
  {
    Map<SparseVector, Integer> m = new HashMap<SparseVector, Integer>();
    int i = 0;
    for (SparseVector v : examples.keySet())
    {
      m.put(v, new Integer(i++));
    }
    return (m);
  }


  public static AbstractComponentMapper makeComponentMapper(AbstractFeature feature)
  {
    AbstractComponentMapper componentMapper = null;
    if (feature instanceof NumericFeature)
    {
      componentMapper = new NumericComponentMapper(feature.getName());
    }
    else if (feature instanceof CategoricalFeature)
    {
      componentMapper = new CategoricalComponentMapper(feature.getName());
    }
    else
    {
      throw new IllegalArgumentException("unsupported feature type: " + feature.getClass().getCanonicalName());
    }
    return (componentMapper);
  }


  public static void updateComponentMapper(AbstractFeature feature, AbstractComponentMapper componentMapper)
  {
    if ((feature instanceof NumericFeature) && (componentMapper instanceof NumericComponentMapper))
    {
      NumericFeature numericFeature = (NumericFeature) feature;
      NumericComponentMapper numericComponentMapper = (NumericComponentMapper) componentMapper;
      // FIXME: nothing to do really if we're going to map to sparse vectors -- could collect set of values to obtain statistics for scaling etc., though.
    }
    else if ((feature instanceof CategoricalFeature) && (componentMapper instanceof CategoricalComponentMapper))
    {
      CategoricalFeature categoricalFeature = (CategoricalFeature) feature;
      CategoricalComponentMapper categoricalComponentMapper = (CategoricalComponentMapper) componentMapper;
      if (!categoricalComponentMapper.hasState(categoricalFeature.getState()))
      {
	// FIXME: using index -1 to try and trigger exceptions if index designation is forgotten or fails -- should really set a proper NA value
	categoricalComponentMapper.addState(categoricalFeature.getState(), -1);
      }
    }
    else
    {
      throw new IllegalArgumentException(String.format("feature / component mapper mismatch or unsupported feature or mapper: feature type %s, mapper type %s", feature.getClass().getSimpleName(), componentMapper.getClass().getSimpleName()));
    }
  }


  protected void extractFeatureVectorMapper(Collection<FeatureVector> featureVectorCollection)
  {
    Map<String, AbstractComponentMapper> componentMapperMap = new HashMap<String, AbstractComponentMapper>();
    for (FeatureVector featureVector : featureVectorCollection)
    {
      for (String featureName : featureVector.keySet())
      {
	AbstractFeature feature = featureVector.get(featureName);
	AbstractComponentMapper componentMapper = componentMapperMap.get(featureName);
	if (componentMapper == null)
	{
	  componentMapper = makeComponentMapper(feature);
	  componentMapperMap.put(featureName, componentMapper);
	}
	updateComponentMapper(feature, componentMapper);
      }
    }
    this.featureVectorMapper = new SvmNodeFeatureVectorMapper();
    for (AbstractComponentMapper componentMapper : componentMapperMap.values())
    {
      this.featureVectorMapper.addComponentMapper(componentMapper);
    }
    this.featureVectorMapper.designateIndexes();
  }


  public void train(Collection<CropDisorderRecord> cropDisorderRecordCollection)
  {
    this.cropDisorderMap = new HashMap<String, CropDisorder>();
    for (CropDisorderRecord cropDisorderRecord : cropDisorderRecordCollection)
    {
      CropDisorder cropDisorder = cropDisorderRecord.getExpertDiagnosedCropDisorder();
      if (cropDisorder == null)
      {
	throw new IllegalArgumentException("unlabelled CDR");
      }
      this.cropDisorderMap.put(cropDisorder.getScientificName(), cropDisorder);
    }
    Set<FeatureVector> featureVectorSet = new HashSet<FeatureVector>();
    for (CropDisorderRecord cropDisorderRecord : cropDisorderRecordCollection)
    {
      // FIXME: duplicate feature vector extraction
      featureVectorSet.add(this.cdrFeatureExtractor.extract(cropDisorderRecord));
    }
    this.extractFeatureVectorMapper(featureVectorSet);
    KernelFunction<SparseVector> kf = new GaussianRBFKernel(1.0f);
    ImmutableSvmParameterPoint.Builder<String, SparseVector> b = new ImmutableSvmParameterPoint.Builder<String, SparseVector>();
    b.kernel = kf;
    // leaving default results in eps < 0 error
    b.eps = 0.001f;
    b.nu = 0.001f;
    b.probability = true;
    // b.oneVsAllMode = MultiClassModel.OneVsAllMode.Best;
    b.crossValidationFolds = 10;
    // trying out p -- no documentation, no idea...
    // b.p = 0.1f;
    ImmutableSvmParameter<String, SparseVector> param = new ImmutableSvmParameterPoint<String, SparseVector>(b);
    LabelInverter<String> labelInverter = new StringLabelInverter();
    // FIXME: duplicate feature vector extraction
    Map<SparseVector, String> sampleLabelMap = this.extractSampleLabelMap(cropDisorderRecordCollection);
    System.err.printf("%d sparse vectors\n", sampleLabelMap.size());
    /*
    for (SparseVector sv : sampleLabelMap.keySet())
    {
      System.err.printf("%s -> %s\n", sv.toString(), sampleLabelMap.get(sv));
    }
    */
    // I have no clue what the IDs are good for
    Map<SparseVector, Integer> sampleIds = idMap(sampleLabelMap);
    ScalingModel<SparseVector> scalingModel = new NoopScalingModel<SparseVector>();
    MultiClassProblem<String, SparseVector> problem = new MultiClassProblemImpl<String, SparseVector>(String.class, labelInverter, sampleLabelMap, sampleIds, scalingModel);
    BinaryClassificationSVM<String, SparseVector> binaryClassificationSVM = new C_SVC<String, SparseVector>();
    MultiClassificationSVM<String, SparseVector> multiClassificationSVM = new MultiClassificationSVM<String, SparseVector>(binaryClassificationSVM);
    this.model = multiClassificationSVM.train(problem, param);
  }


  public Diagnosis diagnose(CropDisorderRecord cropDisorderRecord)
  {
    Diagnosis diagnosis = new Diagnosis();
    FeatureVector featureVector = this.cdrFeatureExtractor.extract(cropDisorderRecord);
    SparseVector sparseVector = this.mapToSparseVector(featureVector);
    String label = this.model.predictLabel(sparseVector);
    for (String cropDisorderScientificName : this.cropDisorderMap.keySet())
    {
      double p = 0.0;
      if (label.equals(cropDisorderScientificName))
      {	
	p = 1.0;
      }
      DisorderScore disorderScore = new DisorderScore(p, this.cropDisorderMap.get(cropDisorderScientificName));
      diagnosis.linkDisorderScore(disorderScore);
    }
    return (diagnosis);
  }
}
