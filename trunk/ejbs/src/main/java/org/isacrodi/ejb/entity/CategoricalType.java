package org.isacrodi.ejb.entity;

import java.util.Set;
import java.util.HashSet;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.OneToMany;
import javax.persistence.Version;
import javax.persistence.Column;

import org.javamisc.jee.entitycrud.CrudConfig;


/**
 * Specification of the physical quantity (physical unit) that is
 * recorded by descriptors of this type.
 */
@Entity
@CrudConfig(propertyOrder = {"id", "typeName", "description", "*"})
public class CategoricalType extends DescriptorType
{
  private Set<CategoricalTypeValue> categoricalTypeValueSet;
  private boolean multivalue;

  private static final long serialVersionUID = 1;


  public CategoricalType()
  {
    super();
    this.categoricalTypeValueSet = new HashSet<CategoricalTypeValue>();
  }


  public CategoricalType(String typeName)
  {
    super(typeName);
    this.categoricalTypeValueSet = new HashSet<CategoricalTypeValue>();
  }


  public boolean getMultivalue()
  {
    return (this.multivalue);
  }


  public void setMultivalue(boolean multivalue)
  {
    this.multivalue = multivalue;
  }


  @OneToMany(mappedBy="categoricalType")
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
  }


  public void linkCategoricalTypeValue(CategoricalTypeValue categoricalTypeValue)
  {
    this.categoricalTypeValueSet.add(categoricalTypeValue);
    categoricalTypeValue.setCategoricalType(this);
  }


  public boolean unlinkCategoricalTypeValue(CategoricalTypeValue categoricalTypeValue)
  {
    if(this.categoricalTypeValueSet.remove(categoricalTypeValue))
    {
      categoricalTypeValue.setCategoricalType(null);
      return (true);
    }
    else
    {
      return (false);
    }
  }


  public void unlink()
  {
    super.unlink();
  }


  public CategoricalTypeValue findCategoricalTypeValue(String value)
  {
    // FIXME: linear search -- hopefully ok as sets of values should be small
    for (CategoricalTypeValue categoricalTypeValue : this.categoricalTypeValueSet)
    {
      if (value.equals(categoricalTypeValue.getValueType()))
      {
	return (categoricalTypeValue);
      }
    }
    return (null);
  }


  public String fileRepresentation()
  {
    String x;
    String s = "categoricaltype\n{\n";
    s += String.format("  typename: %s\n", this.typeName);
    x = "";
    if (this.description != null)
    {
      x = this.description;
    }
    s += String.format("  description: %s\n", x);
    s += "  valueSet:";
    String glue = "";
    for (CategoricalTypeValue categoricalTypeValue : this.categoricalTypeValueSet)
    {
      s += String.format("%s%s", glue, categoricalTypeValue.getValueType());
      glue = ", ";
    }
    s += "\n";
    if (this.multivalue)
    {
      s += "  multiple: true\n";
    }
    else
    {
      s += "  multiple: false\n";
    }
    s += "}\n";
    return (s);
  }
}
