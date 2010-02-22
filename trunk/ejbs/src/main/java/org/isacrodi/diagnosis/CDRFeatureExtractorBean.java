package org.isacrodi.diagnosis;

import java.io.Serializable;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


import org.isacrodi.ejb.entity.*;


public class CDRFeatureExtractorBean implements CDRFeatureExtractor, Serializable
{

  @PersistenceContext
  private EntityManager entityManager;
  private CropDisorderRecord cropDisorderRecord;
  private static final long serialVersionUID = 1;
  private FeatureVector featureVector;


  public FeatureVector extract(CropDisorderRecord cropDisorderRecord)
  {  
    return featureVector;
  }

}
