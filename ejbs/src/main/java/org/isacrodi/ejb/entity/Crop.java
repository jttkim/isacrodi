package org.isacrodi.ejb.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Version;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import java.util.Set;
import java.util.HashSet;

import java.io.Serializable;


@Entity
public class Crop implements Serializable
{
  private Integer id;
  private int version;
  private String name;
  private String scientificName;
  private Set<CropDisorder> cropDisorderSet;
  private Set<CropDisorderRecord> cropDisorderRecordSet;

  private static final long serialVersionUID = 1;


  public Crop()
  {
    super();
    this.cropDisorderSet = new HashSet<CropDisorder>();
    this.cropDisorderRecordSet = new HashSet<CropDisorderRecord>();
  }


  public Crop(String name, String scientificName)
  {
    this();
    this.name = name;
    this.scientificName = scientificName;
  }


  @Id
  @GeneratedValue
  public Integer getId()
  {
    return this.id;
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


  @OneToMany(mappedBy="crop")
  public Set<CropDisorderRecord> getCropDisorderRecordSet()
  {
    return (this.cropDisorderRecordSet);
  }


  public void setCropDisorderRecordSet(Set<CropDisorderRecord> cropDisorderRecordSet)
  {
    this.cropDisorderRecordSet = cropDisorderRecordSet;
  }


  public String getName()
  {
    return this.name;
  }


  public void setName(String name)
  {
    this.name = name;
  }

  @Column(unique = true, nullable = false)
  public String getScientificName()
  {
    return scientificName;
  }


  public void setScientificName(String scientificName)
  {
    this.scientificName = scientificName;
  }

  @ManyToMany
  public Set<CropDisorder> getCropDisorderSet()
  {
    return this.cropDisorderSet;
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
    return String.format("%s %s %s", getId(), getName(), getScientificName());
  }
}
