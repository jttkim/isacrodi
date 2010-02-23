package org.isacrodi.diagnosis;

import org.isacrodi.ejb.entity.*;


public class DummyDiagnosisProvider implements DiagnosisProvider
{
  private Diagnosis diagnose;

  public Diagnosis diagnose(CropDisorderRecord cropDisorderRecord)
  {
     return diagnose;   
  }

}

