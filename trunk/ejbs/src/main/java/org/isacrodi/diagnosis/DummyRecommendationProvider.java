package org.isacrodi.diagnosis;

import org.isacrodi.ejb.entity.*;
import java.io.IOException;
import java.util.Set;
/**
  * Implements Recommendation Provider Interface
 */
public class DummyRecommendationProvider implements RecommendationProvider
{
  
  public DummyRecommendationProvider()
  {
    super();
  }


  public Recommendation recommend(Diagnosis diagnosis)
  {
   
    return (new Recommendation());   
  }

}

