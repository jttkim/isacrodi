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


  public SVMDiagnosisProvider()
  {
    super();
    this.fe = new DummyCDRFeatureExtractor();
    this.fvm = new FeatureVectorMapper();
    this.score = new ScoreTable();
    this.svmpredict = new SVMPredict();
    this.model = null;
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
    //FIXME: Do the index mapping as now it uses consecutive double numbers

    this.knownCropDisorderSet = new HashSet<CropDisorder>();
    HashMap<Double, svm_node[]> hm = new HashMap<Double, svm_node[]>();
    svm_node[] fv = null;
    Double i = 0.0;
    int max = 0;

    for (CropDisorderRecord cropDisorderRecord : labelledCropDisorderRecordSet)
    {
      this.knownCropDisorderSet.add(cropDisorderRecord.getExpertDiagnosedCropDisorder());
      hm.put(i,this.fvm.map(this.fe.extract(cropDisorderRecord)));
      max = this.fvm.getMappedVectorDimension();
      i++;
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
