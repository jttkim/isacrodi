package org.isacrodi.diagnosis;

import java.io.Serializable;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


import org.isacrodi.ejb.entity.*;


public class DiagnosisProviderBean implements DiagnosisProvider, Serializable
{

  @PersistenceContext
  private EntityManager entityManager;
  private static final long serialVersionUID = 1;
  private Diagnosis diagnosis;


  public Diagnosis diagnose(CropDisorderRecord croptDisorderRecord)
  {  
    return diagnosis;
  }

}
