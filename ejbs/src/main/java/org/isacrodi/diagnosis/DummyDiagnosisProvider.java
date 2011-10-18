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
  public DummyDiagnosisProvider()
  {
    super();
  }


  private int countCrop(Crop crop, Collection<CropDisorder> cropDisorderSet)
  {
    int counter = 0;
    for(CropDisorder cdi : cropDisorderSet)
    {
      if(cdi.getCropSet().contains(crop))
      {
        counter++;
      }
    }
    return counter;
  }


  /**
   * Dummy diagnosis.
   *
   * <p>This class is for simple testing purposes only. Diagnoses may
   * depend on the {@code cropDisorderSet} in unexpected ways.</p>
   */
  public Diagnosis diagnose(CropDisorderRecord cropDisorderRecord, Collection<CropDisorder> cropDisorderSet)
  {
    Diagnosis d = new Diagnosis();
    d.setCropDisorderRecord(cropDisorderRecord);
    d.setDisorderScoreSet(new HashSet<DisorderScore>());
    for (CropDisorder disorder : cropDisorderSet)
    {
      DisorderScore ds = new DisorderScore();
      if(cropDisorderRecord.getCrop() == null)
      {
        ds.setScore(1.0 / cropDisorderSet.size());
      }
      else if (disorder.getCropSet().contains(cropDisorderRecord.getCrop()))
      {
        ds.setScore(1.0 / countCrop(cropDisorderRecord.getCrop(), cropDisorderSet));
      }
      else
      {
	ds.setScore(0.0);
      }
      ds.setDiagnosis(d);
      ds.setCropDisorder(disorder);
      d.linkDisorderScore(ds);
    }
    return (d);
  }
}

