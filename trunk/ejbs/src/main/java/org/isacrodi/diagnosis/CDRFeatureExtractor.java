package org.isacrodi.diagnosis;

import org.isacrodi.ejb.entity.*;


public interface CDRFeatureExtractor extends FeatureExtractor<CropDisorderRecord>
{
  FeatureVector extract(CropDisorderRecord cropDisorderRecord);
}
