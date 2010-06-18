package org.isacrodi.ejb.entity;

import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.Version;
import javax.persistence.ManyToOne;


@Entity
public class ProcedureScore implements IsacrodiEntity
{
  private Integer id;
  private int version;
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


  @Id
  @GeneratedValue
  public Integer getId()
  {
    return id;
  }


  public void setId(Integer id)
  {
    this.id = id;
  }


  @Version
  public int getVersion()
  {
    return (this.version);
  }


  public void setVersion(int version)
  {
    this.version = version;
  }


  @ManyToOne(optional = false)
  public Recommendation getRecommendation()
  {
    return this.recommendation;
  }


  public void setRecommendation(Recommendation recommendation)
  {
    this.recommendation = recommendation;
  }


  public void linkRecommendation(Recommendation recommendation)
  {
    this.recommendation = recommendation;
    recommendation.getProcedureScoreSet().add(this);
  }


  public boolean unlinkRecommendation()
  {
    if (!this.recommendation.getProcedureScoreSet().remove(this))
    {
      return (false);
    }
    this.recommendation = null;
    return (true);
  }


  @ManyToOne(optional = false)
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


  public void unlink()
  {
    if (this.recommendation != null)
    {
      this.recommendation.getProcedureScoreSet().remove(this);
      this.recommendation = null;
    }
    if (this.procedure != null)
    {
      this.procedure.getProcedureScoreSet().remove(this);
      this.procedure = null;
    }
  }
}
