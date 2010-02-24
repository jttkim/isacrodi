package org.isacrodi.diagnosis;

import org.isacrodi.ejb.entity.*;


/**
  * Implements Image Feature Extractor Interface
 */
public class DummyImageFeatureExtractor implements ImageFeatureExtractor
{
  public FeatureVector extract(ImageDescriptor imageDescriptor)
  {
    return (new FeatureVector());   
  }
}

