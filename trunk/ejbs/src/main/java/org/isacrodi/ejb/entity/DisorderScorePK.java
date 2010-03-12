package org.isacrodi.ejb.entity;

import java.util.*;
import java.io.Serializable;
import javax.persistence.*;
import javax.persistence.ManyToOne;


@Embeddable
public class DisorderScorePK implements Serializable
{
  private Diagnosis diagnosis;
  private CropDisorder cropDisorder;


  public DisorderScorePK()
  {
    super();
  }


  //public DisorderScorePK(Diagnosis diagnosis, CropDisorder cropDisorder)
  //{
  //  this.diagnosis = diagnosis;
  //  this.cropDisorder = cropDisorder;
  //}

  
  public Diagnosis getDiagnosis()
  {
    return this.diagnosis;
  }

  public void setDiagnosis(Diagnosis diagnosis)
  {
    this.diagnosis = diagnosis;
  }


  public CropDisorder getCropDisorder()
  {
    return this.cropDisorder;
  }


  public void setCropDisorder(CropDisorder cropDisorder)
  {
    this.cropDisorder = cropDisorder;
  }


  public int hashCode()
  {
    System.out.println(diagnosis);
    return 1948 * diagnosis.hashCode() + cropDisorder.hashCode();
  }


  public boolean equals(Object obj) 
  {
    if (obj == this) 
      return true;
		
    if (!(obj instanceof CropDisorder)) 
      return false;
		
    DisorderScorePK pk = (DisorderScorePK)obj;

    if (this.diagnosis != pk.diagnosis) 
      return false;
		
    if (this.cropDisorder != pk.cropDisorder) 
      return false;
		
    return true;
    }
	
}
