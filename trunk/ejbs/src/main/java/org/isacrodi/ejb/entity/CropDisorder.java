package org.isacrodi.ejb.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.Version;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;


@Entity
public class CropDisorder implements Serializable
{
  private Integer Id;
  private int version;
  private String name;
  private String scientificName;
  private Set<Diagnosis> diagnosisSet;
  private Set<Crop> cropSet;


  private static final long serialVersionUID = 1;


  public CropDisorder() 
  {
    super();
  }


  public CropDisorder(String name, String scientificName) 
  {
    this();
    this.name = name;
    this.scientificName = scientificName;
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


  public String getName() 
  {
    return (this.name);
  }


  public void setName(String name)
  {
    this.name = name;
  }


  public String getScientificName() 
  {
    return (this.scientificName);
  }


  public void setScientificName(String scientificName)
  {
    this.name = scientificName;
  }


  @ManyToMany(mappedBy="cropDisorderSet") 
  public Set<Diagnosis> getDiagnosisSet() 
  {
    return (this.diagnosisSet);
  }


  public void setDiagnosisSet(Set<Diagnosis> diagnosisSet)
  {
    this.diagnosisSet = diagnosisSet;
  }

  
  public void addDiagnosis(Diagnosis diagnosis)
  {
    this.diagnosisSet.add(diagnosis);
  }


  @ManyToMany
  public Set<Crop> getCropSet()
  {
    return (this.cropSet);
  }


  public void setCropSet(Set<Crop> cropSet)
  {
    this.cropSet = cropSet;
  }


  public void addCrop(Crop crop)
  {
    this.cropSet.add(crop);
  }

  public String toString()
  {
    return String.format("%s %s %s %s %s", getId(), getVersion(), getName(), getScientificName(), getCropSet());
  }

}
