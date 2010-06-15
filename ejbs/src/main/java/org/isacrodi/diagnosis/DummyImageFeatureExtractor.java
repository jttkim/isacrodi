package org.isacrodi.diagnosis;

import java.io.IOException;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;

import java.util.Set;

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


  /**
   * Extracts a feature vector from a CDR.
   *
   * Bug note: CDRs can have multiple image descriptors, each of which
   * can yield a feature vector. This dummy implementation just takes
   * the "first" image descriptor to construct a feature vector. I'm
   * not clear about the aggregation of feature vectors at this point.
   */
  public FeatureVector extract(CropDisorderRecord cropDisorderRecord)
  {

    FeatureVector featureVector = new FeatureVector();
    Set<ImageDescriptor> idSet = cropDisorderRecord.findImageDescriptorSet();
    if (idSet.size() == 0)
    {
      return (null);
    }
    // hack to just get the "first" element of the set.
    ImageDescriptor ides = idSet.iterator().next();
    try
    {
      ImageProcessing ip = new ImageProcessing(ides.bufferedImage());

    // JTK: exception handling mandated by interface...

    // root question: how should we deal with failures to compute
    // features?  Missing values? How should these be reported, and
    // tho whom?
      NumericFeature nf = new NumericFeature("pixelMean", 12.5);
      featureVector.put("pixelMean", nf);
      //featureVector.put("pixelMean", ip.calculatePixelMean());
    }
    catch (IOException e)
    {
      // FIXME: misusing RuntimeException to bypass exception checking
      throw new RuntimeException(String.format("got IOException extracting buffered image: %s", e.toString()));
    }
    return(featureVector);
  }
}

