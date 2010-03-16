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
  private Set<Crop> cropSet;
  private Set<DisorderScore> disorderScoreSet;
  private Set<Procedure> procedureSet;


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


  @ManyToMany
  public Set<Crop> getCropSet()
  {
    return this.cropSet;
  }


  public void setCropSet(Set<Crop> cropSet)
  {
    this.cropSet = cropSet;
  }


  public void addCrop(Crop crop)
  {
    this.cropSet.add(crop);
  }

  
  @OneToMany(mappedBy="cropDisorder")
  public Set<DisorderScore> getDisorderScoreSet()
  {
    return this.disorderScoreSet;
  }


  public void setDisorderScoreSet(Set<DisorderScore> disorderScoreSet)
  {
    this.disorderScoreSet = this.disorderScoreSet;
  }


  public void addDisorderScore(DisorderScore disorderScore)
  {
    this.disorderScoreSet.add(disorderScore);
  }

  @ManyToMany
  public Set<Procedure> getProcedureSet()
  {
    return this.procedureSet;
  }


  public void setProcedureSet(Set<Procedure> procedureSet)
  {
    this.procedureSet = procedureSet;
  }

  
  public void addProcedure(Procedure procedure)
  {
    this.procedureSet.add(procedure);
  }


  public String toString()
  {
    return String.format("%s %s %s %s", getId(), getVersion(), getName(), getScientificName());
  }

}
