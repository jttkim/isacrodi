package org.isacrodi.ejb.entity;

import javax.persistence.Entity;
import javax.persistence.*;
import java.util.*;
import java.io.Serializable;



public class DisorderScore implements Serializable
{

  private Integer diagnosisId;
  private Integer cropDisorderId;


  public DisorderScore()
  {
    super();
  }


  public DisorderScore(Integer diagnosisId, Integer cropDisorderId)
  {
    this.diagnosisId = diagnosisId;
    this.cropDisorderId = cropDisorderId;
  }


  public int setDiagnosisId()
  {
    return diagnosisId;
  }


  public void getDiagnosisId(Integer diagnosisId)
  {
    this.diagnosisId = this.diagnosisId;
  }


  public int setCropDisorderId()
  {
    return cropDisorderId;
  }


  public void getCropDisorderId(Integer cropDisorderId)
  {
    this.cropDisorderId = this.cropDisorderId;
  }


  public int hashCode()
  {
    int hash = 7;
    hash = 31 * hash + diagnosisId;
    hash = 31 * hash + cropDisorderId;
    return hash;
  }

  public boolean equals(Object obj) 
  {
    if (obj == this) 
    {	
      return true;
    }
		
    if (!(obj instanceof CropDisorder)) 
    {	
      return false;
    }
		
    DisorderScore pk = (DisorderScore)obj;

    if (this.diagnosisId != pk.diagnosisId) 
    {	
      return false;
    }
		
    if (this.cropDisorderId != pk.cropDisorderId) 
    {	
      return false;
    }
		
    return true;
    }
	
}
