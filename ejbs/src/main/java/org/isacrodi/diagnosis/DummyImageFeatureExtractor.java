package org.isacrodi.diagnosis;

import java.io.IOException;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;

import org.isacrodi.ejb.entity.*;


/**
 * Implements Image Feature Extractor Interface.
 */
public class DummyImageFeatureExtractor implements ImageFeatureExtractor
{

  public DummyImageFeatureExtractor()
  {
    super();
  }


  public FeatureVector extract(CropDisorderRecord cropDisorderRecord)
  {

    FeatureVector featureVector = new FeatureVector();
    ImageDescriptor ides = getImageDescriptorSet(cropDisorderRecord);
    try
    {
    ImageProcessing ip = new ImageProcessing(ides.bufferedImage());

    // JTK: exception handling mandated by interface...

    // root question: how should we deal with failures to compute
    // features?  Missing values? How should these be reported, and
    // tho whom?
      featureVector.put("pixelMean", ip.calculatePixelMean());
    }
    catch (IOException e)
    {
      // FIXME: misusing RuntimeException to bypass exception checking
      throw new RuntimeException(String.format("got IOException extracting buffered image: %s", e.toString()));
    }
    return(featureVector);
  }


  public ImageDescriptor getImageDescriptorSet(CropDisorderRecord cropDisorderRecord)
  {
     ImageDescriptor ides = new ImageDescriptor();

     for (Object o : cropDisorderRecord.getDescriptorSet())
     {
       if (o.getClass().isInstance(new ImageDescriptor()))
       {
         ides = (ImageDescriptor)o;
       }
     }
     return ides;
  }
}

