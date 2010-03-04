package org.isacrodi.diagnosis;

import java.io.IOException;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;

import org.isacrodi.ejb.entity.*;


/**
  * Feature classifier
 */
public class FeatureClassifier 
{

  private FeatureVector featureVector;

  public FeatureClassifier()
  {
    super();
  }

  
  public FeatureClassifier(FeatureVector featureVector)
  {
    this();
    this.featureVector = featureVector;
  }

 
  public String DummyClassifier()
  {
    String veredict = "None";
    return veredict;
  }

}

