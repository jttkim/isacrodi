package org.isacrodi.diagnosis;

import java.io.IOException;

import org.isacrodi.ejb.entity.*;


/**
 * Implements Image Feature Extractor Interface.
 */
public class DummyCDRFeatureExtractor implements CDRFeatureExtractor
{

  public DummyCDRFeatureExtractor()
  {
    super();
  }


  public FeatureVector extract(CropDisorderRecord cdr)
  {

    FeatureVector featureVector = new FeatureVector();
    
    for (Object o : cdr.getDescriptorSet())
    {
      if (o.getClass().getName() == "org.isacrodi.ejb.entity.NumericDescriptor")
      {
        NumericDescriptor nd = (NumericDescriptor)o;
        featureVector.put(nd.getNumericType().getTypeName(), nd.getValue());
      }
    } 
    return featureVector;
  }
}

