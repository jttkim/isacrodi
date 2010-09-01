package org.isacrodi.ejb.entity;

import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.Version;
import javax.persistence.ManyToOne;

import org.javamisc.Util;
import org.javamisc.jee.entitycrud.CrudConfig;


@Entity
@CrudConfig(propertyOrder = {"id", "procedure", "score", "recommendation", "*"})
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
    this();
    this.score = score;
  }


  public ProcedureScore(Procedure procedure)
  {
    this();
    this.procedure = procedure;
  }


  public ProcedureScore(double score, Procedure procedure)
  {
    this(score);
    this.procedure = procedure;
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


  /*
  public int hashCode()
  {
    if (this.id == null)
    {
      return (0);
    }
    return (this.id.intValue());
  }


  public boolean equals(Object obj)
  {
    if (obj == this)
    {
      return true;
    }
    if (!(obj instanceof ProcedureScore))
    {
      return false;
    }
    ProcedureScore ps = (ProcedureScore) obj;
    if ((this.id == null) || (ps.id == null))
    {
      return (false);
    }
    return (this.id.equals(ps.id));
  }
  */


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


  public String toString()
  {
    String procedureName = "<null>";
    if (this.procedure != null)
    {
      procedureName = this.procedure.getName();
    }
    Integer recommendationId = null;
    if (this.recommendation != null)
    {
      recommendationId = this.recommendation.getId();
    }
    return (String.format("ProcedureScore(id = %s, procedure = %s, recommendationId = %s, score = %f", Util.safeStr(this.id), procedureName, Util.safeStr(recommendationId), this.score));
  }
}
