package org.isacrodi.ejb.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.OneToMany;
import javax.persistence.Version;
import javax.persistence.Column;
import java.util.Set;
import java.util.HashSet;

import org.javamisc.Util;
import org.javamisc.jee.entitycrud.CrudConfig;


/**
 * Specification of the physical quantity (physical unit) that is
 * recorded by descriptors of this type.
 */
@Entity
@CrudConfig(propertyOrder = {"id", "typeName", "description", "unit", "*"})
public class NumericType extends DescriptorType
{
  private String unit;

  private static final long serialVersionUID = 1;

  public NumericType()
  {
    super();
  }


  public NumericType(String typeName)
  {
    super(typeName);
  }


  public NumericType(String typeName, String unit)
  {
    this(typeName);
    this.unit = unit;
  }


  /**
   * Unit of this numeric type.
   */
  public String getUnit()
  {
    return (this.unit);
  }


  public void setUnit(String unit)
  {
    this.unit = unit;
  }


  public void unlink()
  {
    super.unlink();
  }


  public String toString()
  {
    return (String.format("NumericType(id = %s, name = %s)", Util.safeStr(this.id), Util.safeStr(this.typeName)));
  }


  public String fileRepresentation()
  {
    String x;
    String s = "numerictype\n{\n";
    s += String.format("  typename: %s\n", this.typeName);
    s += String.format("  unit: %s\n", this.unit);
    x = "";
    if (this.description != null)
    {
      x = this.description;
    }
    s += String.format("  description: %s\n", x);
    s += "}\n";
    return (s);
  }
}
