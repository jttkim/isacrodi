package org.isacrodi.ejb.entity;

import java.util.Set;
import java.util.HashSet;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.ManyToOne;
import javax.persistence.ManyToMany;
import javax.persistence.Version;
import javax.persistence.Column;


@Entity
public class CategoricalTypeValue implements IsacrodiEntity
{
  private Integer id;
  private static final long serialVersionUID = 1;
  private CategoricalType categoricalType;
  private Set<CategoricalDescriptor> categoricalDescriptorSet;
  private String valueType;


  public CategoricalTypeValue()
  {
    super();
    this.categoricalDescriptorSet = new HashSet<CategoricalDescriptor>();
  }


  public CategoricalTypeValue(String valueType)
  {
    this();
    this.valueType = valueType;
  }


  public CategoricalTypeValue(Integer id, CategoricalType categoricalType, String valueType)
  {
    this(valueType);
    this.id = id;
    this.categoricalType = categoricalType;
  }


  @Id
  @GeneratedValue
  public Integer getId()
  {
    return(this.id);
  }

  public void setId(Integer id)
  {
    this.id = id;
  }


  @ManyToOne
  public CategoricalType getCategoricalType()
  {
    return (this.categoricalType);
  }


  public void setCategoricalType(CategoricalType categoricalType)
  {
    this.categoricalType = categoricalType;
  }


  public void linkCategoricalType(CategoricalType categoricalType)
  {
    this.categoricalType = categoricalType;
    categoricalType.getCategoricalTypeValueSet().add(this);
  }


  public boolean unlinkCategoricalType()
  {
    if (this.categoricalType == null)
    {
      return (false);
    }
    this.categoricalType = null;
    if (!categoricalType.getCategoricalTypeValueSet().remove(this))
    {
      return (false);
    }
    return (true);
  }


  // FIXME: uniqueness should be enforced for one type
  // @Column(unique = true, nullable = false)
  public String getValueType()
  {
    return(this.valueType);
  }


  public void setValueType(String valueType)
  {
    this.valueType = valueType;
  }


  @ManyToMany(mappedBy = "categoricalTypeValueSet")
  public Set<CategoricalDescriptor> getCategoricalDescriptorSet()
  {
    return(this.categoricalDescriptorSet);
  }


  public void setCategoricalDescriptorSet(Set<CategoricalDescriptor> categoricalDescriptorSet)
  {
    this.categoricalDescriptorSet = categoricalDescriptorSet;
  }


  @Deprecated
  public void addCategoricalDescriptor(CategoricalDescriptor categoricalDescriptor)
  {
    this.categoricalDescriptorSet.add(categoricalDescriptor);
    categoricalDescriptor.getCategoricalTypeValueSet().add(this);
  }


  public void linkCategoricalDescriptor(CategoricalDescriptor categoricalDescriptor)
  {
    this.categoricalDescriptorSet.add(categoricalDescriptor);
    categoricalDescriptor.getCategoricalTypeValueSet().add(this);
  }


  public boolean unlinkCategoricalDescriptor(CategoricalDescriptor categoricalDescriptor)
  {
    if (!this.categoricalDescriptorSet.remove(categoricalDescriptor))
    {
      return (false);
    }
    return (categoricalDescriptor.getCategoricalTypeValueSet().remove(this));
  }


  public void unlink()
  {
    if (this.categoricalType != null)
    {
      this.unlinkCategoricalType();
    }
    for (CategoricalDescriptor categoricalDescriptor : this.categoricalDescriptorSet)
    {
      categoricalDescriptor.getCategoricalTypeValueSet().remove(this);
    }
    this.categoricalDescriptorSet.clear();
  }
}
