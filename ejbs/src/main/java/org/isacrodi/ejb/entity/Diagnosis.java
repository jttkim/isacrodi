package org.isacrodi.ejb.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.*;
import javax.persistence.ManyToMany;
import java.util.*;
import java.io.Serializable;


@Entity
public class Diagnosis implements Serializable
{
  private Integer Id;
  private int version;
  private CropDisorderRecord cropDisorderRecord;
  private Set<CropDisorder> cropDisorderSet;

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

  @ManyToMany
  @JoinColumn(insertable=false, updatable=false)
  public Set<CropDisorder> getCropDisorderSet()
  {
    return (this.cropDisorderSet);
  }


  public void setCropDisorderSet(Set<CropDisorder> cropDisorderSet)
  {
    this.cropDisorderSet = cropDisorderSet;
  }


  public void addCropDisorder(CropDisorder cropDisorder)
  {
    this.cropDisorderSet.add(cropDisorder);
  }

 
  public String toString()
  {
    return String.format("%s %s %s %s", getId(), getVersion(), getCropDisorderRecord(), getCropDisorderSet());
  }
}
