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

import libsvm.*;


/**
 * Implements Diagnosis Provider Interface
 */
public class SVMDiagnosisProvider implements DiagnosisProvider
{
  private CDRFeatureExtractor fe;
  private SvmNodeFeatureVectorMapper svmNodeFeatureVectorMapper;
  private svm_model model;
  private Map<CropDisorder, Integer> disorderIndexMap;


  public SVMDiagnosisProvider()
  {
    super();
    this.fe = new DummyCDRFeatureExtractor();
    this.svmNodeFeatureVectorMapper = new SvmNodeFeatureVectorMapper();
    this.model = null;
    this.disorderIndexMap = null;
  }


  public SVMDiagnosisProvider(String modelFileName, String parseFileName) throws IOException
  {
    // FIXME: dangerous design -- modelFileName and parseFileName have to be consistent!!
    this();
    this.model = svm.svm_load_model(modelFileName);
    this.svmNodeFeatureVectorMapper.parseFile(parseFileName);
  }


  // jtk: feature vector mapper copied from JSVMDiagnosisProvider, expecting that JSVMDiagnosisProvider will be phased out
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
    this.svmNodeFeatureVectorMapper = new SvmNodeFeatureVectorMapper();
    for (AbstractComponentMapper componentMapper : componentMapperMap.values())
    {
      this.svmNodeFeatureVectorMapper.addComponentMapper(componentMapper);
    }
    this.svmNodeFeatureVectorMapper.designateIndexes();
  }
  // jtk: end copied stuff



  /**
   * Provide a map to look up disorder names by svm label (index).
   */
  public Map<Integer, CropDisorder> makeReverseDisorderIndexMap()
  {
    Map<Integer, CropDisorder> reverseDisorderIndexMap = new HashMap<Integer, CropDisorder>();
    for (CropDisorder cropDisorder : this.disorderIndexMap.keySet())
    { 
      reverseDisorderIndexMap.put(this.disorderIndexMap.get(cropDisorder), cropDisorder);
    }
    return (reverseDisorderIndexMap);
  }


  public void train(Collection<CropDisorderRecord> labelledCropDisorderRecordSet)
  {
    // FIXME: consider defining this mapping as part of the feature vector mappers' responsibilities
    this.disorderIndexMap = new HashMap<CropDisorder, Integer>();
    svm_node[][] sample = new svm_node[labelledCropDisorderRecordSet.size()][];
    double label[] = new double[labelledCropDisorderRecordSet.size()];
    int maxDisorderIndex = 0;
    int i = 0;
    for (CropDisorderRecord cropDisorderRecord : labelledCropDisorderRecordSet)
    {
      CropDisorder edd = cropDisorderRecord.getExpertDiagnosedCropDisorder();
      if (edd == null)
      {
	throw new RuntimeException("CDR with no expert diagnosis in labelled set");
      }
      if (!this.disorderIndexMap.containsKey(edd))
      {
	this.disorderIndexMap.put(edd, new Integer(maxDisorderIndex++));
      }
      int disorderIndex = this.disorderIndexMap.get(edd).intValue();
      label[i] = (double) disorderIndex;
      FeatureVector featureVector = this.fe.extract(cropDisorderRecord);
      sample[i] = this.svmNodeFeatureVectorMapper.map(featureVector);
    }
    svm_parameter svmparameter = new svm_parameter();
    // use svm with slack variables
    svmparameter.svm_type = svm_parameter.C_SVC;
    // use radial basis function
    svmparameter.kernel_type = svm_parameter.RBF;
    // unused?
    svmparameter.degree = 3;
    // gamma = 1 / number of features
    svmparameter.gamma = 1.0 / ((double) labelledCropDisorderRecordSet.size());
    svmparameter.coef0 = 0;
    svmparameter.nu = 0.5;
    svmparameter.cache_size = 100;
    svmparameter.C = 1;
    svmparameter.eps = 1e-3;
    svmparameter.p = 0.1;
    svmparameter.shrinking = 1;
    svmparameter.probability = 1;
    svmparameter.nr_weight = 0;
    svmparameter.weight_label = new int[0];
    svmparameter.weight = new double[0];
    svm_problem svmproblem = new svm_problem();
    svmproblem.x = sample;
    svmproblem.y = label;
    svmproblem.l = label.length;
    this.model = svm.svm_train(svmproblem, svmparameter);
  }


  public Diagnosis diagnose(CropDisorderRecord cropDisorderRecord)
  {
    if (this.model == null)
    {
      throw new RuntimeException("attempt to diagnose without training first");
    }
    Diagnosis diagnosis = new Diagnosis();
    int[] svmLabels = new int[this.disorderIndexMap.size()];
    svm.svm_get_labels(this.model, svmLabels);
    FeatureVector featureVector = fe.extract(cropDisorderRecord);
    svm_node[] fv = this.svmNodeFeatureVectorMapper.map(featureVector);
    double[] probability = new double[this.disorderIndexMap.size()];
    double predictedLabel = svm.svm_predict_probability(this.model, fv, probability);
    Map<Integer, CropDisorder> reverseDisorderIndexMap = this.makeReverseDisorderIndexMap();
    for (int i = 0; i < probability.length; i++)
    {
      int disorderIndex = svmLabels[i];
      CropDisorder cropDisorder = reverseDisorderIndexMap.get(new Integer(disorderIndex));
      // FIXME: problem: how to reattach disorders???
      DisorderScore disorderScore = new DisorderScore(probability[i], cropDisorder);
      diagnosis.linkDisorderScore(disorderScore);
    }
    diagnosis.linkCropDisorderRecord(cropDisorderRecord);
    return diagnosis;   
  }
}
