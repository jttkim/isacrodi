package org.isacrodi.ejb.entity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;



/**
  * Set symptom descriptor
  */
@Entity
public class SymptomDescriptor extends Descriptor
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


  public void linkSymptomType(SymptomType symptomType)
  {
    this.symptomType = symptomType;
    symptomType.getSymptomDescriptorSet().add(this);
  }


  public boolean unlinkSymptomType()
  {
    if (!symptomType.getSymptomDescriptorSet().remove(symptomType))
    {
      return (false);
    }
    this.symptomType = null;
    return (true);
  }


  public void setSymptomValue(String symptomValue)
  {
    this.symptomValue = symptomValue;
  }


  public String toString()
  {
    return String.format("%s %s", this.symptomType, this.symptomValue);
  }


  public void unlink()
  {
    this.symptomType.getSymptomDescriptorSet().remove(this);
    this.symptomType = null;
  }
}
