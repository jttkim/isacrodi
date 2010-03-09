package org.isacrodi.diagnosis;

import java.io.IOException;

import org.isacrodi.ejb.entity.*;
import java.lang.*;

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
    NumericDescriptor nd = getNumericDescriptorSet(cdr);
    featureVector.put(nd.getNumericType().getTypeName(), nd.getNumericValue());

    return featureVector;
  }


  public NumericDescriptor getNumericDescriptorSet(CropDisorderRecord cropDisorderRecord)
  {
     NumericDescriptor ndes = new NumericDescriptor();

     for (Object o : cropDisorderRecord.getDescriptorSet())
     {
       if (o.getClass().isInstance(new NumericDescriptor()))
       {
         ndes = (NumericDescriptor)o;
       }
     }
     return ndes;
  }
}

