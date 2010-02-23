package org.isacrodi.ejb.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;


@Entity
public class SymptomDescriptor extends Descriptor implements Serializable
{

  private SymptomType symptomType;

  private static final long serialVersionUID = 1;


  public SymptomDescriptor()
  {
    super();
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



}
