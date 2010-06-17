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
  private ScoreTable score;
  private svm_predict svmpredict;
  private svm_model model;


  public SVMDiagnosisProvider()
  {
    super();
    this.fe = new DummyCDRFeatureExtractor();
    this.fvm = new FeatureVectorMapper();
    this.score = new ScoreTable();
    this.svmpredict = new svm_predict();
    this.model = new svm_model();
  }


  public void setKnownDisorderSet(Set<CropDisorder> cropDisorderSet)
  {
    this.cropDisorderSet = cropDisorderSet;
  }


  public Diagnosis diagnose(CropDisorderRecord cropDisorderRecord)
  {
    String model_filename = "src/test/java/org/isacrodi/diagnosis/isacrodi_model";
    Diagnosis diagnosis = new Diagnosis();
    svm_node[] fv = null;
    diagnosis.setCropDisorderRecord(cropDisorderRecord);
    diagnosis.setDisorderScoreSet(new HashSet<DisorderScore>());
    FeatureVector featureVector = fe.extract(cropDisorderRecord);

    this.fvm.parseFile("src/test/java/org/isacrodi/diagnosis/isacrodi_feature_mapper.txt");
    fv = this.fvm.map(featureVector);
    //FIXME: How can I get rid of this try
    try 
    {
      this.model = svm.svm_load_model(model_filename);
      this.score = this.svmpredict.predict(this.model, fv);

      for (CropDisorder disorder : this.cropDisorderSet)
      {
        DisorderScore ds1 = new DisorderScore();
        ds1.setScore(this.score.getScore(Integer.toString(disorder.getId())));
        ds1.setDiagnosis(diagnosis);
        ds1.setCropDisorder(disorder);
        diagnosis.addDisorderScore(ds1);
      }
    } 
    catch (IOException e)
    {
      System.err.println(e);
    }
    return diagnosis;   
  }
}
