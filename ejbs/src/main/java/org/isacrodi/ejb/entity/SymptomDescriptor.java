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

  private SymptomType symptomType;
  private double symptomValue;

  private static final long serialVersionUID = 1;


  public SymptomDescriptor()
  {
    super();
  }


  public SymptomDescriptor(SymptomType symptomType, double symptomValue)
  {
    this();
    this.symptomType = symptomType;
    this.symptomValue = symptomValue;
  }


  @ManyToOne
  public SymptomType getSymptomType()
  {
    return symptomType;
  }


  public void setSymptomType(SymptomType symptomType)
  {
    this.symptomType = symptomType;
  }

  
  public double getSymptomValue()
  {
    return symptomValue;
  }


  public void setSymptomValue(double symptomValue)
  {
    this.symptomValue = symptomValue;
  }
}
