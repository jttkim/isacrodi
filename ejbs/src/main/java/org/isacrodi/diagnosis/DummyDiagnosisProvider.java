package org.isacrodi.diagnosis;

import org.isacrodi.ejb.entity.*;


/**
  * Implements Diagnosis Provider Interface
 */
public class DummyDiagnosisProvider implements DiagnosisProvider
{
  public Diagnosis diagnose(CropDisorderRecord cropDisorderRecord)
  {
    return (new Diagnosis());   
  }
}

