package org.isacrodi.ejb.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.Version;
import javax.persistence.OneToMany;
import java.util.Set;
import java.io.Serializable;


@Entity
public class Crop implements Serializable
{
  private Integer id;	
  private int version;
  private String name;
  private String scientificName;
  private Set<CropDisorderRecord> cropDisorderRecordSet;

  private static final long serialVersionUID = 1;


  public Crop()
  {
    super();
  }


  public Crop(String name, String scientificName)
  {
    this.name = name;
    this.scientificName = scientificName;
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
    this.scientificName = scientificName;
  }

}