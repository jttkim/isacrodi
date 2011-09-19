package org.isacrodi.diagnosis;

import java.util.Collection;

import org.isacrodi.ejb.entity.*;


public interface DiagnosisProvider 
{
  public Diagnosis diagnose(CropDisorderRecord cropDisorderRecord, Collection<CropDisorder> cropDisorderSet);
}
