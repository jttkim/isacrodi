package org.isacrodi.diagnosis;

import org.isacrodi.ejb.entity.*;


interface CDRFeatureExtractor extends FeatureExtractor<CropDisorderRecord>
{
  FeatureVector extract(CropDisorderRecord cropDisorderRecord);
}
