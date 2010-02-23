package org.isacrodi.ejb.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

@Entity
public class NumericDescriptor extends Descriptor implements Serializable
{
  private NumericType numericType;
  private double value;

  private static final long serialVersionUID = 1;

   
  public NumericDescriptor()
  {
    super();
  }


  public NumericDescriptor(NumericType numericType, double value)
  {
    this();
    this.numericType = numericType;
    this.value = value;
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


  public double getValue()
  {
    return value;
  }


  public void setValue(double value)
  {
    this.value = value;
  }

}
