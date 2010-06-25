package org.isacrodi.diagnosis;

import org.isacrodi.ejb.entity.*;
import java.util.Set;
import java.util.Vector;
import java.util.HashSet;
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
    this.model = new svm_model();
  }


  public SVMDiagnosisProvider(String modelFileName) throws IOException
  {
    this();
    this.model = svm.svm_load_model(modelFileName);
  }


  @Deprecated
  public void setKnownCropDisorderSet(Set<CropDisorder> knownCropDisorderSet)
  {
    this.knownCropDisorderSet = knownCropDisorderSet;
  }


  public void train(Collection<CropDisorderRecord> labelledCropDisorderRecordSet)
  {
    this.knownCropDisorderSet = new HashSet<CropDisorder>();
    for (CropDisorderRecord cropDisorderRecord : labelledCropDisorderRecordSet)
    {
      this.knownCropDisorderSet.add(cropDisorderRecord.getExpertDiagnosedCropDisorder());
      // get feature vectors...
    }
    this.model = null; // compute and set the model to be used for predicting...
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

      for (CropDisorder disorder : this.knownCropDisorderSet)
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
