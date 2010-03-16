package org.isacrodi.ejb.entity;

import java.util.*;
import java.io.Serializable;
import javax.persistence.*;
import javax.persistence.ManyToOne;


@Embeddable
public class ProcedureScore implements Serializable
{
  private Recommendation recommendation;
  private Procedure procedure;
  private double score;


  public ProcedureScore()
  {
    super();
  }


  public ProcedureScore(double score)
  {
    this.score = score;
  }

  @ManyToOne  
  public Recommendation getRecommendation()
  {
    return this.recommendation;
  }


  public void setRecommendation(Recommendation recommendation)
  {
    this.recommendation = recommendation;
  }


  @ManyToOne  
  public Procedure getProcedure()
  {
    return this.procedure;
  }


  public void setProcedure(Procedure procedure)
  {
    this.procedure = procedure;
  }


  public double getScore()
  {
    return score;
  }


  public void setScore(double score)
  {
    this.score = score;
  }


  public int hashCode()
  {
    return 1948 * recommendation.hashCode() + procedure.hashCode();
  }


  public boolean equals(Object obj) 
  {
    if (obj == this) 
      return true;
		
    if (!(obj instanceof Procedure)) 
      return false;
		
    ProcedureScore ps = (ProcedureScore)obj;

    if (this.recommendation != ps.recommendation) 
      return false;
		
    if (this.procedure != ps.procedure) 
      return false;
		
    return true;
  }
}
