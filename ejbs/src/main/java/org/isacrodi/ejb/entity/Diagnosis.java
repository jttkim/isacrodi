package org.isacrodi.ejb.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.*;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import java.util.*;
import java.io.Serializable;


@Entity
public class Diagnosis implements Serializable
{
  private Integer Id;
  private int version;
  private CropDisorderRecord cropDisorderRecord;
  private Set<DisorderScore> disorderScoreSet;


  private static final long serialVersionUID = 1;


  public Diagnosis()
  {
    super();
  }


  @Id 
  @GeneratedValue
  public Integer getId()
  {
    return (this.Id);
  }


  public void setId(Integer Id)
  {
    this.Id = Id;
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


  @OneToOne(mappedBy="diagnosis")
  public CropDisorderRecord getCropDisorderRecord()
  {
    return (this.cropDisorderRecord);
  }


  public void setCropDisorderRecord(CropDisorderRecord cropDisorderRecord)
  {
    this.cropDisorderRecord = cropDisorderRecord;
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


  public void addDisorderScore(DisorderScore disorderScore)
  {
    this.disorderScoreSet.add(disorderScore);
  }


  //public String toString()
  //{
  //  return String.format("%s %s %s %s", getId(), getVersion(), getCropDisorderRecord(), getDisorderScoreSet());
  //}
}
