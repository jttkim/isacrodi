package org.isacrodi.ejb.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.OneToMany;
import javax.persistence.Version;
import java.util.Set;

/**
 * Set symptom type
 */
@Entity
@Deprecated
public class SymptomType implements IsacrodiEntity
{
  private Integer id;
  private int version;
  private String typeName;
  private Set<SymptomDescriptor> symptomDescriptorSet;

  private static final long serialVersionUID = 1;


  public SymptomType()
  {
    super();
  }


  public SymptomType(String typeName)
  {
    super();
    this.typeName = typeName;
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
  public Set<SymptomDescriptor> getSymptomDescriptorSet()
  {
    return symptomDescriptorSet;
  }


  public void setSymptomDescriptorSet(Set<SymptomDescriptor> symptomDescriptorSet)
  {
    this.symptomDescriptorSet = symptomDescriptorSet;
  }


  public void linkSymptomDescriptor(SymptomDescriptor symptomDescriptor)
  {
    this.symptomDescriptorSet.add(symptomDescriptor);
    symptomDescriptor.setSymptomType(this);
  }


  public boolean unlinkSymptomDescriptor(SymptomDescriptor symptomDescriptor)
  {
    if (!this.symptomDescriptorSet.remove(symptomDescriptor))
    {
      return (false);
    }
    symptomDescriptor.setSymptomType(null);
    return (true);
  }


  public String getTypeName()
  {
    return typeName;
  }


  public void setTypeName(String typeName)
  {
    this.typeName = typeName;
  }


  public void unlink()
  {
    for (SymptomDescriptor symptomDescriptor : this.symptomDescriptorSet)
    {
      symptomDescriptor.setSymptomType(null);
    }
    this.symptomDescriptorSet.clear();
  }
}
