package org.isacrodi.ejb.interfaces;

import org.isacrodi.ejb.entity.*;


public interface DiagnosisProvider 
{
  public Diagnosis diagnose(CropDisorderRecord cropDisorderRecord);
}

