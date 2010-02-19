package org.isacrodi.ejb.interfaces;

import org.isacrodi.ejb.entity.*;


interface CDRFeatureExtractor extends FeatureExtractor<CropDisorderRecord>
{
  FeatureVector extract(CropDisorderRecord cropDisorderRecord);
}
