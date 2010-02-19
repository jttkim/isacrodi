package org.isacrodi.diagnosis;

import org.isacrodi.ejb.entity.*;


interface ImageFeatureExtractor extends FeatureExtractor<ImageDescriptor>
{
  FeatureVector extract(ImageDescriptor descriptor);
}
