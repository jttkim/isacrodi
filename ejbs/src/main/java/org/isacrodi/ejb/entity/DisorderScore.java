package org.isacrodi.ejb.entity;

import java.util.*;
import java.io.Serializable;
import javax.persistence.*;


@Entity
public class DisorderScore implements Serializable
{
  @Id
  private DisorderScorePK key;
  private double score;


  public DisorderScore()
  {
    super();
  }


  public DisorderScore(double score)
  {
    this();
    this.score = score;
  }


  public DisorderScorePK getDisorderScorePK()
  {
    return this.key;
  }


  public void setDisorderScorePK(DisorderScorePK key)
  {
    this.key = key;
  }
    

  public double getScore()
  {
    return this.score;
  }


  public void setScore(double score)
  {
    this.score = score;
  }
}

