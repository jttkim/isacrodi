package org.isacrodi.diagnosis;

import org.isacrodi.ejb.entity.*;


interface ImageFeatureExtractor extends FeatureExtractor<CropDisorderRecord>
{
  FeatureVector extract(CropDisorderRecord cropDisorderRecord);
}
