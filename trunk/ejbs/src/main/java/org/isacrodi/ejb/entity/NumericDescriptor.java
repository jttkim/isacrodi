package org.isacrodi.ejb.entity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

import org.javamisc.jee.entitycrud.CrudConfig;


@Entity
@CrudConfig(propertyOrder = {"id", "cropDisorderRecord", "descriptorType", "numericValue", "*"})
public class NumericDescriptor extends Descriptor
{
  private double numericValue;

  private static final long serialVersionUID = 1;


  public NumericDescriptor()
  {
    super();
  }


  public NumericDescriptor(NumericType numericType, double numericValue)
  {
    this();
    this.numericValue = numericValue;
    this.linkDescriptorType(numericType);
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
    super.unlink();
  }


  public String toString()
  {
    return (String.format("NumericDescriptor(%s, %g)", this.descriptorType.getTypeName(), this.numericValue));
  }


  public String fileRepresentation()
  {
    return(String.format("%s: %f", this.descriptorType.getTypeName(), this.numericValue));
  }
}
