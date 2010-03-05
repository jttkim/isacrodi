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
    NumericDescriptor nd = cdr.getNumericDescriptorSet();
    featureVector.put(nd.getNumericType().getTypeName(), nd.getValue());

    return featureVector;
  }
}

