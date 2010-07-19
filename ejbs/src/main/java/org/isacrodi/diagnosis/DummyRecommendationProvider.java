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

    Recommendation recommendation = new Recommendation();
    //this.cropDisorderRecord.setRecommendation(this.recommendation);
    recommendation.setId(1);

    recommendation.setProcedureScoreSet(new java.util.HashSet<ProcedureScore>());
    for(DisorderScore cd : diagnosis.getDisorderScoreSet())
    {
      for(Procedure p : cd.getCropDisorder().getProcedureSet())
      {
	ProcedureScore ps = new ProcedureScore();
	ps.setRecommendation(recommendation);
	ps.setProcedure(p);
	ps.setScore(0.2);
	recommendation.addProcedureScore(ps);
      }
    }

    return (recommendation);
  }
}
