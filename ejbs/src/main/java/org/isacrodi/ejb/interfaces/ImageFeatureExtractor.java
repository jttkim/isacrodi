package org.isacrodi.ejb.interfaces;

import org.isacrodi.ejb.entity.*;


interface ImageFeatureExtractor extends FeatureExtractor<ImageDescriptor>
{
  FeatureVector extract(ImageDescriptor descriptor);
}
