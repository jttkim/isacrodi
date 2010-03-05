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

  public FeatureClassifier()
  {
    super();
  }

  
  public int DummyClassifier(FeatureVector featureVector)
  {
    
    int disease = 0;

    if(featureVector.get("crop") == 1.0) {
      if(featureVector.get("temperature") > 20) 
         disease = 1;
      else 
         disease = 2;
    }
    else 
      disease = 3;

    return disease;
  }

}

