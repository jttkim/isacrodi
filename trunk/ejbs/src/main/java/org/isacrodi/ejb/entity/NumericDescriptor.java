package org.isacrodi.ejb.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

@Entity
public class NumericDescriptor extends Descriptor implements Serializable
{
  private NumericType numericType;

  private static final long serialVersionUID = 1;

   
  NumericDescriptor()
  {
    super();
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
}
