package org.isacrodi.diagnosis;

import java.io.IOException;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.util.Set;

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

  
  public void DummyClassifier(FeatureVector featureVector, Set<DisorderScore> disorderScoreSet)
  {
    System.out.println("featureVector....." + featureVector);

  }


  public void DummyOld(FeatureVector featureVector, Set<DisorderScore> disorderScoreSet)
  {
    for(DisorderScore d : disorderScoreSet)
    {
      for(Crop o : d.getCropDisorder().getCropSet()) 
      {
        if(((double)featureVector.get("crop") == o.getId()) & featureVector.get("crop") == 10) 
	{
	  d.setScore((double)1/3);
	}
        else if(((double)featureVector.get("crop") == o.getId()) & featureVector.get("crop") == 20) 
	  d.setScore((double)1/2);
	else
	  d.setScore((double)1/5);
     }
    }
  }

  


}

