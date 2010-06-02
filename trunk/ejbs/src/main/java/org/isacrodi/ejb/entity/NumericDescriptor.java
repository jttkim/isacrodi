package org.isacrodi.ejb.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

@Entity
public class NumericDescriptor extends Descriptor implements Serializable
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


  @ManyToOne
  public NumericType getNumericType()
  {
    return numericType;
  }


  public void setNumericType(NumericType numericType)
  {
    this.numericType = numericType;
  }


  public double getNumericValue()
  {
    return numericValue;
  }


  public void setNumericValue(double numericValue)
  {
    this.numericValue = numericValue;
  }


  public String toString()
  {
    return (String.format("NumericDescriptor(%s, %g)", this.numericType.getTypeName(), this.numericValue));
  }
}
