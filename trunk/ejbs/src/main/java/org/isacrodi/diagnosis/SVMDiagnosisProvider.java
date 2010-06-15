package org.isacrodi.diagnosis;

import org.isacrodi.ejb.entity.*;
import java.io.IOException;
import java.util.Set;
import java.util.Vector;
import java.util.HashSet;
import java.io.*;
import java.io.ByteArrayOutputStream;
import libsvm.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Implements Diagnosis Provider Interface
 */
public class SVMDiagnosisProvider implements DiagnosisProvider
{
  private Set<CropDisorder> cropDisorderSet;
  private CDRFeatureExtractor fe;
  private FeatureVectorMapper fvm;


  public SVMDiagnosisProvider()
  {
    super();
  }


  public void setKnownDisorderSet(Set<CropDisorder> cropDisorderSet)
  {
    this.cropDisorderSet = cropDisorderSet;
  }


  public Diagnosis diagnose(CropDisorderRecord cropDisorderRecord)
  {
    Collection<ScoreTable> scoreTable = new ArrayList<ScoreTable>();

    Diagnosis diagnosis = new Diagnosis();
    svm_node[] fv = null;
    this.fe = new DummyCDRFeatureExtractor();
    this.fvm = new FeatureVectorMapper();

    diagnosis.setCropDisorderRecord(cropDisorderRecord);
    diagnosis.setDisorderScoreSet(new HashSet<DisorderScore>());
    FeatureVector featureVector = fe.extract(cropDisorderRecord);

    for (String k : featureVector.keySet())
      System.out.println("feature: " + k);

    fv = this.fvm.map(featureVector);

    /*
    FeatureClassifier cl = new FeatureClassifier();
    scoreTable = cl.dummyClassifier(featureVector);
    //scoreTable = cl.dummyClassifier(numericFeature);
    for (CropDisorder disorder : this.cropDisorderSet)
    {
      DisorderScore ds1 = new DisorderScore();
      ds1.setScore(search_index(scoreTable, Integer.toString(disorder.getId())));
      ds1.setDiagnosis(d);
      ds1.setCropDisorder(disorder);
      d.addDisorderScore(ds1);
    }
    */
    return diagnosis;   
  }

}

