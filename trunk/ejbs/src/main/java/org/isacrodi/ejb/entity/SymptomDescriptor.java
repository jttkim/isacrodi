package org.isacrodi.ejb.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;



/**
  * Set symptom descriptor
  */
@Entity
public class SymptomDescriptor extends Descriptor implements Serializable
{
  //FIXME: Change double for String to test classifier

  private SymptomType symptomType;
  //private double symptomValue;
  private String symptomValue;

  private static final long serialVersionUID = 1;


  public SymptomDescriptor()
  {
    super();
  }


  public SymptomDescriptor(SymptomType symptomType, String symptomValue)
  {
    this();
    this.symptomType = symptomType;
    this.symptomValue = symptomValue;
  }


  @ManyToOne
  public SymptomType getSymptomType()
  {
    return this.symptomType;
  }


  public void setSymptomType(SymptomType symptomType)
  {
    this.symptomType = symptomType;
  }

  
  public String getSymptomValue()
  {
    return symptomValue;
  }


  public void setSymptomValue(String symptomValue)
  {
    this.symptomValue = symptomValue;
  }


  public String toString()
  {
    return String.format("%s %s", this.symptomType, this.symptomValue);
  }

}
