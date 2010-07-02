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
  private Set<CropDisorder> knownCropDisorderSet;
  private CDRFeatureExtractor fe;
  private FeatureVectorMapper fvm;
  private ScoreTable score;
  private SVMPredict svmpredict;
  private svm_model model;
  private Map<String, Integer> disorderIndexMap;


  public SVMDiagnosisProvider()
  {
    super();
    this.fe = new DummyCDRFeatureExtractor();
    this.fvm = new FeatureVectorMapper();
    this.score = new ScoreTable();
    this.svmpredict = new SVMPredict();
    this.model = null;
    this.disorderIndexMap = null;
  }


  public SVMDiagnosisProvider(String modelFileName, String parseFileName) throws IOException
  {
    this();
    this.model = svm.svm_load_model(modelFileName);
    this.fvm.parseFile(parseFileName);
  }


  @Deprecated
  public void setKnownCropDisorderSet(Set<CropDisorder> knownCropDisorderSet)
  {
    this.knownCropDisorderSet = knownCropDisorderSet;
  }


  public void train(Collection<CropDisorderRecord> labelledCropDisorderRecordSet)
  {
    // FIXME: consider defining this mapping as part of the feature vector mappers' responsibilities
    this.knownCropDisorderSet = new HashSet<CropDisorder>();
    HashMap<Double, svm_node[]> hm = new HashMap<Double, svm_node[]>();
    svm_node[] fv = null;
    int max = 0;

    this.disorderIndexMap = new HashMap<String, Integer>();
    int maxDisorderIndex = 0;
    for (CropDisorderRecord cropDisorderRecord : labelledCropDisorderRecordSet)
    {
      CropDisorder edd = cropDisorderRecord.getExpertDiagnosedCropDisorder();
      // FIXME: should check whether this is not null
      String eddName = edd.getScientificName();
      if (!this.disorderIndexMap.containsKey(eddName))
      {
	this.disorderIndexMap.put(eddName, new Integer(maxDisorderIndex++));
      }
      int disorderIndex = this.disorderIndexMap.get(eddName).intValue();
      this.knownCropDisorderSet.add(cropDisorderRecord.getExpertDiagnosedCropDisorder());
      // FIXME: jtk: I think this is wrong -- if there are multiple CDRs
      // with the same disorder diagnosed by an expert, only the last
      // CDR with a given expert diagnosis will end up in the map.
      hm.put(new Double((double) disorderIndex), this.fvm.map(this.fe.extract(cropDisorderRecord)));
      // FIXME: determining dimension of mapped feature vectors is a feature vector mapper responsibility
      max = this.fvm.getMappedVectorDimension();
    }
    SVMTrain train = new SVMTrain();
    this.model = train.classify(hm, max);
  }


  public Diagnosis diagnose(CropDisorderRecord cropDisorderRecord)
  {
    Diagnosis diagnosis = new Diagnosis();
    svm_node[] fv = null;
    diagnosis.setCropDisorderRecord(cropDisorderRecord);
    diagnosis.setDisorderScoreSet(new HashSet<DisorderScore>());
    FeatureVector featureVector = fe.extract(cropDisorderRecord);

    fv = this.fvm.map(featureVector);
    this.score = this.svmpredict.predict(this.model, fv);

    for (CropDisorder disorder : this.knownCropDisorderSet)
    {
      DisorderScore ds1 = new DisorderScore();
      ds1.setScore(this.score.getScore(Integer.toString(disorder.getId())));
      ds1.setDiagnosis(diagnosis);
      ds1.setCropDisorder(disorder);
      diagnosis.addDisorderScore(ds1);
    }

    return diagnosis;   
  }
}
