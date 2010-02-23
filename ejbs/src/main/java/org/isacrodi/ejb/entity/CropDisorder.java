package org.isacrodi.ejb.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.Version;
import java.io.Serializable;

@Entity
public class CropDisorder implements Serializable
{
  private Integer id;
  private int version;
  private String name;
  private String scientificName;

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
}
