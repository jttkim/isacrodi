package org.isacrodi.diagnosis;

import java.io.IOException;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;

import org.isacrodi.ejb.entity.*;


/**
 * Simple recommendation provider.
 *
 * <p>Disorders are ranked by score and the top disorders that account
 * for a configurable top percentage of the total score are
 * determined. All procedures that address one of these high scoring
 * disorders and that are not incompatible with a procedure addressing
 * a higher ranking disorder are recommended. Procedure scores are
 * computed as the sum of all disorder scores of all disorders that
 * the procedure can address.</p>
 */
public class SimpleRecommendationProvider implements RecommendationProvider
{
  private double topPercentage;


  public SimpleRecommendationProvider(double topPercentage)
  {
    super();
    this.topPercentage = topPercentage;
  }


  private List<DisorderScore> extractTopScoreList(Diagnosis diagnosis)
  {
    ArrayList<DisorderScore> scoreList = new ArrayList<DisorderScore>(diagnosis.getDisorderScoreSet());
    Comparator<DisorderScore> comparator = new Comparator<DisorderScore>()
    {
      public int compare(DisorderScore s1, DisorderScore s2)
      {
	if (s1.getScore() > s2.getScore())
	{
	  return (-1);
	}
	else if (s1.getScore() < s2.getScore())
	{
	  return (1);
	}
	else
	{
	  return (0);
	}
      }
    };
    Collections.sort(scoreList, comparator);
    double cutoff = diagnosis.disorderScoreSum() * this.topPercentage;
    ArrayList<DisorderScore> topScoreList = new ArrayList<DisorderScore>();
    double cumulativeScore = 0.0;
    for (DisorderScore disorderScore : scoreList)
    {
      topScoreList.add(disorderScore);
      cumulativeScore += disorderScore.getScore();
      if (cumulativeScore >= cutoff)
      {
	break;
      }
    }
    return (topScoreList);
  }


  public Recommendation recommend(Diagnosis diagnosis)
  {
    List<DisorderScore> topScoreList = this.extractTopScoreList(diagnosis);
    Set<ProcedureScore> procedureScoreSet = new HashSet<ProcedureScore>();
    for (DisorderScore disorderScore : topScoreList)
    {
      // System.err.println(String.format("SimpleRecommendationProvider.recommend: processing disorder %s", disorderScore.getCropDisorder().getName()));
      CropDisorder cropDisorder = disorderScore.getCropDisorder();
      boolean compatible = true;
      for (Procedure procedure : cropDisorder.getProcedureSet())
      {
	// System.err.println(String.format("SimpleRecommendationProvider.recommend:   processing procedure %s", procedure.getName()));
	ProcedureScore procedureScore = null;
	for (ProcedureScore ps : procedureScoreSet)
	{
	  if (procedure.equals(ps.getProcedure()))
	  {
	    // System.err.println(String.format("SimpleRecommendationProvider.recommend:   found existing score for %s", procedure.getName()));
	    procedureScore = ps;
	    break;
	  }
	  else if (!procedure.compatibleWith(ps.getProcedure()))
	  {
	    // System.err.println(String.format("SimpleRecommendationProvider.recommend:   %s is incompatible with %s", procedure.getName(), ps.getProcedure().getName()));
	    compatible = false;
	    break;
	  }
	}
	if (compatible && (procedureScore == null))
	{
	  // System.err.println(String.format("SimpleRecommendationProvider.recommend:   creating new score for %s", procedure.getName()));
	  procedureScore = new ProcedureScore(0.0, procedure);
	  procedureScoreSet.add(procedureScore);
	}
	if (procedureScore != null)
	{
	  procedureScore.setScore(procedureScore.getScore() + disorderScore.getScore());
	}
	System.err.println();
      }
    }
    Recommendation recommendation = new Recommendation();
    recommendation.linkCropDisorderRecord(diagnosis.getCropDisorderRecord());
    for (ProcedureScore procedureScore : procedureScoreSet)
    {
      recommendation.addProcedureScore(procedureScore);
    }
    return (recommendation);
  }
}
