package org.isacrodi.diagnosis;


import org.isacrodi.ejb.entity.*;
import java.io.*;
import java.io.Serializable;


/**
  * Feature mapper
  */
public class FeatureMapper implements Serializable
{
  private int featureLabel;
  private String featureName;


  public FeatureMapper()
  {
    super();
  }


  public FeatureMapper(int featureLabel, String featureName)
  {
    this();
    this.featureLabel = featureLabel;
    this.featureName = featureName;
  }


  public int getFeatureLabel()
  {
    return this.featureLabel;
  }

 
  public void setFeatureLabel(int featureLabel)
  {
    this.featureLabel = featureLabel;
  }


  public String getFeatureName()
  {
    return this.featureName;
  }

 
  public void setFeatureLabel(String featureName)
  {
    this.featureName = featureName;
  }


  public String toString()
  {
    return String.format("%s %s", getFeatureLabel(), getFeatureName());
  }

}
