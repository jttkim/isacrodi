package org.isacrodi.ejb.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.Version;
import javax.persistence.ManyToMany;
import java.io.Serializable;
import java.util.Set;

@Entity(name="CropDisorderId")
public class CropDisorder implements Serializable
{
  private Integer cropDisorderId;
  private int version;
  private String name;
  private String scientificName;
  private Set<Diagnosis> diagnosis;
  private static final long serialVersionUID = 1;


  public CropDisorder() 
  {
    super();
  }


  public CropDisorder(String name, String scientificName) 
  {
    this.name = name;
    this.scientificName = scientificName;
  }


  @Id
  @GeneratedValue
  public Integer getCropDisorderId()
  {
    return (this.cropDisorderId);
  }


  public void setCropDisorderId(Integer cropDisorderId)
  {
    this.cropDisorderId = cropDisorderId;
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
    return name;
  }


  public void setName(String name)
  {
    this.name = name;
  }


  public String getScientificName() 
  {
    return scientificName;
  }


  public void setScientificName(String scientificName)
  {
    this.name = scientificName;
  }


  @ManyToMany(mappedBy="cropDisorderSet") 
  public Set<Diagnosis> getDiagnosis() 
  {
    return diagnosis;
  }


  public void setDiagnosis(Set<Diagnosis> diagnosis)
  {
    this.diagnosis = diagnosis;
  }

}
