package org.isacrodi.diagnosis;

import org.isacrodi.ejb.entity.*;
import java.io.IOException;
import java.util.Set;
import java.util.Vector;
import java.util.HashSet;
import java.io.*;
import java.io.ByteArrayOutputStream;
import libsvm.*;

/**
 * Implements Diagnosis Provider Interface
 */
public class DummyDiagnosisProvider implements DiagnosisProvider
{
  private Set<CropDisorder> cropDisorderSet;

  
  public DummyDiagnosisProvider()
  {
    super();
  }


  public void setKnownDisorderSet(Set<CropDisorder> cropDisorderSet)
  {
    this.cropDisorderSet = cropDisorderSet;
  }


  public Diagnosis diagnose(CropDisorderRecord cropDisorderRecord)
  {
    Diagnosis d = new Diagnosis();
    d.setCropDisorderRecord(cropDisorderRecord);
    d.setDisorderScoreSet(new HashSet<DisorderScore>());


    diagnoseOld(cropDisorderRecord);

    for (CropDisorder disorder : this.cropDisorderSet)
    {
      DisorderScore ds = new DisorderScore();
      if(cropDisorderRecord.getCrop() == null)
        ds.setScore(1.0 / this.cropDisorderSet.size());
      else if (disorder.getCropSet().contains(cropDisorderRecord.getCrop()))
        ds.setScore(1.0 / CountCrop(cropDisorderRecord.getCrop()));
      else
	ds.setScore(0.0); 
      ds.setDiagnosis(d);
      ds.setCropDisorder(disorder);
      d.addDisorderScore(ds);
    }
    
    return (d);
  }


  public int CountCrop(Crop crop)
  {

    int counter = 0;

    for(CropDisorder cdi : this.cropDisorderSet)
    {
      if(cdi.getCropSet().contains(crop))
        counter++;
    }

    return counter;
  }


  public Diagnosis diagnoseOld(CropDisorderRecord cropDisorderRecord)
  //public void diagnoseOld(CropDisorderRecord cropDisorderRecord)
  {
    double a = 0.0; 
    CDRFeatureExtractor c = new DummyCDRFeatureExtractor();
    FeatureVector dfv = c.extract(cropDisorderRecord);

    ImageFeatureExtractor ie = new DummyImageFeatureExtractor();
    FeatureVector ifv = ie.extract(cropDisorderRecord);
    
    FeatureVector featureVector = new FeatureVector();
    featureVector.put("2", (double)cropDisorderRecord.getCrop().getId());

    for (String k : dfv.keySet())
      featureVector.put(k, dfv.get(k));
    //for (String k : ifv.keySet())
    //  featureVector.put(k, ifv.get(k));
   
    FeatureClassifier cl = new FeatureClassifier();
    DisorderScore ds = new DisorderScore();
    //ds.setScore(cl.DummyClassifier(featureVector, cropDisorderRecord.getDiagnosis().getDisorderScoreSet()));
 
    a = cl.DummyClassifier(featureVector, cropDisorderRecord.getDiagnosis().getDisorderScoreSet());
    ds.setScore(a);

    return (cropDisorderRecord.getDiagnosis());   
  }

}

