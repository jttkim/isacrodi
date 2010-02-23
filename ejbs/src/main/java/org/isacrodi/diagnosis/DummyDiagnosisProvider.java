package org.isacrodi.diagnosis;

import org.isacrodi.ejb.entity.*;


public class DummyDiagnosisProvider implements DiagnosisProvider
{
  public Diagnosis diagnose(CropDisorderRecord cropDisorderRecord)
  {
    return (new Diagnosis());   
  }
}

