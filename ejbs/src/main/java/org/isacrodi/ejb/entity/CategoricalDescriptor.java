package org.isacrodi.ejb.entity;

import java.util.Set;
import java.util.HashSet;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;

import org.javamisc.jee.entitycrud.CrudConfig;


@Entity
@CrudConfig(propertyOrder = {"id", "cropDisorderRecord", "descriptorType", "categoricalTypeValue", "*"})
public class CategoricalDescriptor extends Descriptor
{
  private Set<CategoricalTypeValue> categoricalTypeValueSet;
  private CategoricalType categoricalType;


  public CategoricalDescriptor()
  {
    super();
    this.categoricalTypeValueSet = new HashSet<CategoricalTypeValue>();
  }


  public CategoricalDescriptor(CategoricalType categoricalType)
  {
    this();
    this.linkDescriptorType(categoricalType);
  }


  public CategoricalDescriptor(CategoricalType categoricalType, Set<CategoricalTypeValue> categoricalTypeValueSet)
  {
    this(categoricalType);
    this.categoricalTypeValueSet = categoricalTypeValueSet;
  }


  @ManyToMany
  public Set<CategoricalTypeValue> getCategoricalTypeValueSet()
  {
    return(this.categoricalTypeValueSet);
  }


  public void setCategoricalTypeValueSet(Set<CategoricalTypeValue> categoricalTypeValueSet)
  {
    this.categoricalTypeValueSet = categoricalTypeValueSet;
  }


  @Deprecated
  public void addCategoricalTypeValue(CategoricalTypeValue categoricalTypeValue)
  {
    this.categoricalTypeValueSet.add(categoricalTypeValue);
    categoricalTypeValue.getCategoricalDescriptorSet().add(this);
  }


  public void linkCategoricalTypeValue(CategoricalTypeValue categoricalTypeValue)
  {
    this.categoricalTypeValueSet.add(categoricalTypeValue);
    categoricalTypeValue.getCategoricalDescriptorSet().add(this);
  }


  public boolean unlinkCategoricalTypeValue(CategoricalTypeValue categoricalTypeValue)
  {
    if (!this.categoricalTypeValueSet.remove(categoricalTypeValue))
    {
      return (false);
    }
    return (categoricalTypeValue.getCategoricalDescriptorSet().remove(this));
  }


  public void unlink()
  {
    for (CategoricalTypeValue categoricalTypeValue : this.categoricalTypeValueSet)
    {
      categoricalTypeValue.getCategoricalDescriptorSet().remove(this);
    }
    this.categoricalTypeValueSet.clear();
    super.unlink();
  }


  public String fileRepresentation()
  {
    String s = String.format("%s: ", this.descriptorType.getTypeName());
    String glue = "";
    for (CategoricalTypeValue categoricalTypeValue : this.categoricalTypeValueSet)
    {
      s += String.format("%s%s", glue, categoricalTypeValue.getValueType());
      glue = ", ";
    }
    return (s);
  }


  public String toString()
  {
    String v = "{";
    String glue = "";
    for (CategoricalTypeValue c : this.categoricalTypeValueSet)
    {
      v += String.format("%s%s", glue, c.getValueType());
    }
    v += "}";
    return (String.format("CategoricalDescriptor(%s, %s)", this.descriptorType.getTypeName(), v));
  }
}
