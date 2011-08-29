package org.isacrodi.ejb.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;
import javax.persistence.GeneratedValue;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;

import org.javamisc.Util;
import org.javamisc.jee.entitycrud.CrudConfig;


@Entity
@CrudConfig(propertyOrder = {"id", "cropDisorderRecord", "disorderScoreSet", "*"})
public class Diagnosis implements IsacrodiEntity
{
  private Integer id;
  private int version;
  private CropDisorderRecord cropDisorderRecord;
  private Set<DisorderScore> disorderScoreSet;

  private static final long serialVersionUID = 1;


  public String toString()
  {
    return (String.format("Diagnosis(id = %s)", Util.safeStr(this.id)));
  }


  public Diagnosis()
  {
    super();
    this.disorderScoreSet = new HashSet<DisorderScore>();
  }


  @Id
  @GeneratedValue
  public Integer getId()
  {
    return (this.id);
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


  @OneToOne(mappedBy="diagnosis", optional = false)
  public CropDisorderRecord getCropDisorderRecord()
  {
    return (this.cropDisorderRecord);
  }


  public void setCropDisorderRecord(CropDisorderRecord cropDisorderRecord)
  {
    this.cropDisorderRecord = cropDisorderRecord;
  }


  public void linkCropDisorderRecord(CropDisorderRecord cropDisorderRecord)
  {
    this.setCropDisorderRecord(cropDisorderRecord);
    cropDisorderRecord.setDiagnosis(this);
  }


  public boolean unlinkCropDisorderRecord()
  {
    if (this.cropDisorderRecord == null)
    {
      return (false);
    }
    this.cropDisorderRecord.setDiagnosis(null);
    this.cropDisorderRecord = null;
    return (true);
  }


  @OneToMany(mappedBy="diagnosis")
  public Set<DisorderScore> getDisorderScoreSet()
  {
    return this.disorderScoreSet;
  }


  public void setDisorderScoreSet(Set<DisorderScore> disorderScoreSet)
  {
    this.disorderScoreSet = disorderScoreSet;
  }


  @Deprecated
  public void addDisorderScore(DisorderScore disorderScore)
  {
    this.disorderScoreSet.add(disorderScore);
    disorderScore.setDiagnosis(this);
  }


  public void linkDisorderScore(DisorderScore disorderScore)
  {
    this.disorderScoreSet.add(disorderScore);
    disorderScore.setDiagnosis(this);
  }


  public boolean unlinkDisorderScore(DisorderScore disorderScore)
  {
    if (this.disorderScoreSet.remove(disorderScore))
    {
      disorderScore.setDiagnosis(null);
      return (true);
    }
    else
    {
      return (false);
    }
  }


  /**
   * Retrieve the highest disorder score in this diagnosis.
   *
   * @return the highest scored disorder
   */
  public DisorderScore highestDisorderScore()
  {
    if (this.disorderScoreSet.size() == 0)
    {
      return (null);
    }
    DisorderScore highscore = null;
    for (DisorderScore disorderScore : this.disorderScoreSet)
    {
      if (highscore == null)
      {
	highscore = disorderScore;
      }
      else
      {
	if (highscore.getScore() < disorderScore.getScore())
	{
	  highscore = disorderScore;
	}
      }
    }
    return (highscore);
  }


  /**
   * Compute a list of disorder scores, sorted in descending order.
   *
   * @return the list of disorder scores, in descending order
   */
  public List<DisorderScore> descendingDisorderScoreList()
  {
    ArrayList<DisorderScore> disorderScoreList = new ArrayList<DisorderScore>(this.disorderScoreSet);
    Comparator<DisorderScore> comparator = new Comparator<DisorderScore>()
    {
      public int compare(DisorderScore ds1, DisorderScore ds2)
      {
	if (ds1.getScore() > ds2.getScore())
	{
	  return (-1);
	}
	else if (ds1.getScore() < ds2.getScore())
	{
	  return (1);
	}
	else
	{
	  return (0);
	}
      }
    };
    Collections.sort(disorderScoreList, comparator);
    return (disorderScoreList);
  }


  public void unlink()
  {
    this.unlinkCropDisorderRecord();
    for (DisorderScore disorderScore : this.disorderScoreSet)
    {
      disorderScore.setDiagnosis(null);
    }
    this.disorderScoreSet.clear();
  }


  public double disorderScoreSum()
  {
    double sum = 0.0;
    for (DisorderScore disorderScore : this.disorderScoreSet)
    {
      sum += disorderScore.getScore();
    }
    return (sum);
  }


  /** Compute the Shannon information of this diagnosis.
   *
   * <p>Note: Maximal information is computed from the number of
   * disorder scores, which is presumed to reflect the number of
   * disorders in the database.</p>
   *
   * @return the Shannon information
   */
  public double shannonInformation()
  {
    double s = this.disorderScoreSum();
    double information = Math.log(this.disorderScoreSet.size()) / Math.log(2.0);
    for (DisorderScore disorderScore : this.disorderScoreSet)
    {
      double p = disorderScore.getScore() / s;
      information += p * Math.log(p) / Math.log(2.0);
    }
    return (information);
  }
}
