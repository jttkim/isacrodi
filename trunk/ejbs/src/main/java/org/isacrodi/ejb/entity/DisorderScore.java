package org.isacrodi.ejb.entity;

import java.util.*;
import java.io.Serializable;
import javax.persistence.*;
import javax.persistence.ManyToOne;


@Embeddable
public class DisorderScore implements Serializable
{
  private Diagnosis diagnosis;
  private CropDisorder cropDisorder;
  private double score;


  public DisorderScore()
  {
    super();
  }


  public DisorderScore(double score)
  {
    this.score = score;
  }

  @ManyToOne  
  public Diagnosis getDiagnosis()
  {
    return this.diagnosis;
  }


  public void setDiagnosis(Diagnosis diagnosis)
  {
    this.diagnosis = diagnosis;
  }


  @ManyToOne 
  public CropDisorder getCropDisorder()
  {
    return this.cropDisorder;
  }


  public void setCropDisorder(CropDisorder cropDisorder)
  {
    this.cropDisorder = cropDisorder;
  }


  public double getScore()
  {
    return this.score;
  }


  public void setScore(double score)
  {
    this.score = score;
  }


  public int hashCode()
  {
    return 1948 * diagnosis.hashCode() + cropDisorder.hashCode();
  }


  public boolean equals(Object obj) 
  {
    if (obj == this) 
      return true;
		
    if (!(obj instanceof CropDisorder)) 
      return false;
		
    DisorderScore ds = (DisorderScore)obj;

    if (this.diagnosis != ds.diagnosis) 
      return false;
		
    if (this.cropDisorder != ds.cropDisorder) 
      return false;
		
    return true;
  }
}
