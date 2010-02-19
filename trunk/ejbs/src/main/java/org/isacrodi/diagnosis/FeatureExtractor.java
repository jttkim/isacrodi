package org.isacrodi.diagnosis;

import org.isacrodi.ejb.entity.*;


public interface FeatureExtractor<GenericObject>
{
  FeatureVector extract(GenericObject o);
}
