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

    double [][] score = null; 
    CDRFeatureExtractor c = new DummyCDRFeatureExtractor();
    FeatureVector dfv = c.extract(cropDisorderRecord);
    ImageFeatureExtractor ie = new DummyImageFeatureExtractor();
    FeatureVector ifv = ie.extract(cropDisorderRecord);
    
    FeatureVector featureVector = new FeatureVector();
    Crop crop = cropDisorderRecord.getCrop();
    if (crop != null)
    {
      // jtk: is this really intended, to put the crop ID as a Double into the vector???
      featureVector.put("2", (double) crop.getId());
    }

    for (String k : dfv.keySet())
      featureVector.put(k, dfv.get(k));
    //for (String k : ifv.keySet())
    //  featureVector.put(k, ifv.get(k));
   
    FeatureClassifier cl = new FeatureClassifier();
    score = cl.dummyClassifier(featureVector);
    for (CropDisorder disorder : this.cropDisorderSet)
    {
      DisorderScore ds1 = new DisorderScore();
      ds1.setScore(score[search_index(score, disorder.getId())][1]);
      ds1.setDiagnosis(d);
      ds1.setCropDisorder(disorder);
      d.addDisorderScore(ds1);
    }

    return d;   
  }


  public int search_index(double [][] score, int key)
  {
    int i;  

    for(i=0; i < score.length; i++)
    {
      if (score[i][0] == key)
        break;
    }
    return i;
  }



/// Old Stuff

  public Diagnosis diagnoseOld(CropDisorderRecord cropDisorderRecord)
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


}

