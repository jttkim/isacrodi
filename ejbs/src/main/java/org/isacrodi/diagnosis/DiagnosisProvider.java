package org.isacrodi.diagnosis;

import org.isacrodi.ejb.entity.*;


public interface DiagnosisProvider 
{

  public Diagnosis diagnose(CropDisorderRecord cropDisorderRecord);

}

