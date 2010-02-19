package org.isacrodi.ejb.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.OneToMany;
import javax.persistence.Version;
import java.util.Set;


@Entity
public class SymptomType implements Serializable
{
  private Integer id;
  private int version;
  private String typeName;
  private Set<SymptomDescriptor> symptomDescriptor;

  private static final long serialVersionUID = 1;


  SymptomType()
  {
    super();
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


  @OneToMany(mappedBy="symptomType")
  public Set<SymptomDescriptor> getSymptomDescriptor()
  {
    return symptomDescriptor;
  }


  public void setSymptomDescriptor(Set<SymptomDescriptor> symptomDescriptor)
  {
    this.symptomDescriptor = symptomDescriptor;
  }



  public String getTypeName()
  {
    return typeName;
  }


  public void setTypeName(String typeName)
  {
    this.typeName = typeName;
  }
}
