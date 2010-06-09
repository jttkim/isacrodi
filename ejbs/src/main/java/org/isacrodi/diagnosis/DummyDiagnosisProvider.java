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

