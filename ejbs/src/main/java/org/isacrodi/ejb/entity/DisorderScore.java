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
@CrudConfig(propertyOrder = {"id", "diagnosis", "cropDisorder", "score", "*"})
public class DisorderScore implements IsacrodiEntity
{
  private Integer id;
  private int version;
  private Diagnosis diagnosis;
  private CropDisorder cropDisorder;
  private double score;


  public DisorderScore()
  {
    super();
  }


  public DisorderScore(double score)
  {
    this();
    this.score = score;
  }


  public DisorderScore(double score, CropDisorder cropDisorder)
  {
    this(score);
    this.cropDisorder = cropDisorder;
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
  public Diagnosis getDiagnosis()
  {
    return this.diagnosis;
  }


  public void setDiagnosis(Diagnosis diagnosis)
  {
    this.diagnosis = diagnosis;
  }


  public void linkDiagnosis(Diagnosis diagnosis)
  {
    this.diagnosis = diagnosis;
    diagnosis.getDisorderScoreSet().add(this);
  }


  public boolean unlinkDiagnosis()
  {
    if (this.diagnosis == null)
    {
      return (false);
    }
    if (!this.diagnosis.getDisorderScoreSet().remove(this))
    {
      return (false);
    }
    this.diagnosis = null;
    return (true);
  }


  @ManyToOne(optional = false)
  public CropDisorder getCropDisorder()
  {
    return this.cropDisorder;
  }


  public void setCropDisorder(CropDisorder cropDisorder)
  {
    this.cropDisorder = cropDisorder;
  }


  public void linkCropDisorder(CropDisorder cropDisorder)
  {
    this.cropDisorder = cropDisorder;
    cropDisorder.getDisorderScoreSet().add(this);
  }


  public boolean unlinkCropDisorder()
  {
    if (this.cropDisorder == null)
    {
      return (false);
    }
    if (!this.cropDisorder.getDisorderScoreSet().remove(this))
    {
      return (false);
    }
    this.cropDisorder = null;
    return (true);
  }


  public double getScore()
  {
    return this.score;
  }


  public void setScore(double score)
  {
    this.score = score;
  }


  public void unlink()
  {
    this.unlinkDiagnosis();
    this.unlinkCropDisorder();
  }


  /* jtk: hash code breaks if diagnosis or cropDisorder are null
  public int hashCode()
  {
    // jtk: what do we need this for? is this a good hash code?
    return 1948 * diagnosis.hashCode() + cropDisorder.hashCode();
  }
  */


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
      return (true);
    }
    if (!(obj instanceof DisorderScore))
    {
      return (false);
    }
    DisorderScore otherDisorderScore = (DisorderScore) obj;
    if ((this.id == null) || (otherDisorderScore.id == null))
    {
      return (false);
    }
    return (this.id.equals(otherDisorderScore.id));
  }
  */

  public String toString()
  {
    return (String.format("DisorderScore(id = %s, score = %s)", Util.safeStr(id), this.score));
  }
}
