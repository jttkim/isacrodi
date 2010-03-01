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
  private String value;

  private static final long serialVersionUID = 1;


  public SymptomDescriptor()
  {
    super();
  }


  public SymptomDescriptor(SymptomType symptomType, String value)
  {
    super();
    this.symptomType = symptomType;
    this.value = value;
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

  
  public String getValue()
  {
    return value;
  }


  public void setValue(String value)
  {
    this.value = value;
  }
}
