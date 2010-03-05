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
  private Integer id;
  private int version;
  private CropDisorderRecord cropDisorderRecord;
  private Set<CropDisorder> cropDisorderSet;

  private static final long serialVersionUID = 1;


  public Diagnosis()
  {
    super();
  }


  @Id @GeneratedValue
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


  @OneToOne
  public CropDisorderRecord getCropDisorderRecord()
  {
    return (this.cropDisorderRecord);
  }


  public void setCropDisorderRecord(CropDisorderRecord cropDisorderRecord)
  {
    this.cropDisorderRecord = cropDisorderRecord;
  }


  @ManyToMany(mappedBy="diagnosis")
  public Set<CropDisorder> getCropDisorderSet()
  {
    return (this.cropDisorderSet);
  }


  public void setCropDisorderSet(Set<CropDisorder> cropDisorder)
  {
    this.cropDisorderSet = cropDisorder;
  }


  public void addCropDisorder(CropDisorder cropDisorder)
  {
    this.cropDisorderSet.add(cropDisorder);
  }
}
