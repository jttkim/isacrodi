package org.isacrodi.ejb.interfaces;

import org.isacrodi.ejb.entity.*;


public interface FeatureExtractor<GenericObject>
{
  FeatureVector extract(GenericObject o);
}
