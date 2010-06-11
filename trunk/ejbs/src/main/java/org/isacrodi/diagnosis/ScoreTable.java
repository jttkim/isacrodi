package org.isacrodi.diagnosis;


import org.isacrodi.ejb.entity.*;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.util.Set;
import java.util.Vector;
import java.util.HashSet;
import java.io.*;
import java.io.ByteArrayOutputStream;
import libsvm.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
  * Feature classifier
  */
public class ScoreTable implements Serializable
{

  private String label;
  private Double score;

  public ScoreTable()
  {
    super();
  }

  public ScoreTable(String label, Double score)
  {
    this();
    this.label = label;
    this.score = score;
  }


  public Double getScore()
  { 
    return this.score;
  }

  
  public void setScore(Double score)
  {
    this.score = score;
  }
  
  
  public String getLabe()
  { 
    return this.label;
  }

  
  public void setLabel(String label)
  {
    this.label = label;
  }

}
