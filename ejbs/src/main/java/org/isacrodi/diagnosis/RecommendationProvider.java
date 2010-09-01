package org.isacrodi.diagnosis;

import org.isacrodi.ejb.entity.*;


public interface RecommendationProvider
{
  public Recommendation recommend(Diagnosis diagnosis);
}
