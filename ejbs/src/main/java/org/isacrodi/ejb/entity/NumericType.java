package org.isacrodi.ejb.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.OneToMany;
import javax.persistence.Version;
import javax.persistence.Column;
import java.util.Set;
import java.util.HashSet;

/**
 * Specification of the physical quantity (physical unit) that is
 * recorded by descriptors of this type.
 */
@Entity
public class NumericType implements Serializable
{
  private Integer id;
  private int version;
  private String typeName;
  private Set<NumericDescriptor> numericDescriptorSet;

  private static final long serialVersionUID = 1;


  public NumericType()
  {
    super();
    this.numericDescriptorSet = new HashSet<NumericDescriptor>();
  }


  public NumericType(String typeName)
  {
    this();
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


  @OneToMany(mappedBy="numericType")
  public Set<NumericDescriptor> getNumericDescriptorSet()
  {
    return this.numericDescriptorSet;
  }


  public void setNumericDescriptorSet(Set<NumericDescriptor> numericDescriptorSet)
  {
    this.numericDescriptorSet = numericDescriptorSet;
  }


  @Column(unique = true, nullable = false)
  public String getTypeName()
  {
    return typeName;
  }


  public void setTypeName(String typeName)
  {
    this.typeName = typeName;
  }
}
