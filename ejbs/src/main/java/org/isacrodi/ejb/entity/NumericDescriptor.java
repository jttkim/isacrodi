package org.isacrodi.ejb.entity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

@Entity
public class NumericDescriptor extends Descriptor
{
  private NumericType numericType;
  private double numericValue;

  private static final long serialVersionUID = 1;


  public NumericDescriptor()
  {
    super();
  }


  public NumericDescriptor(NumericType numericType, double numericValue)
  {
    this();
    this.numericType = numericType;
    this.numericValue = numericValue;
  }


  @ManyToOne(optional = false)
  public NumericType getNumericType()
  {
    return numericType;
  }


  public void setNumericType(NumericType numericType)
  {
    this.numericType = numericType;
  }


  public void linkNumericType(NumericType numericType)
  {
    this.numericType = numericType;
    numericType.getNumericDescriptorSet().add(this);
  }


  public boolean unlinkNumericType()
  {
    if (!this.numericType.getNumericDescriptorSet().remove(this))
    {
      return (false);
    }
    this.numericType = null;
    return (true);
  }


  public double getNumericValue()
  {
    return numericValue;
  }


  public void setNumericValue(double numericValue)
  {
    this.numericValue = numericValue;
  }


  public void unlink()
  {
    this.unlinkNumericType();
  }


  public String toString()
  {
    return (String.format("NumericDescriptor(%s, %g)", this.numericType.getTypeName(), this.numericValue));
  }
}
