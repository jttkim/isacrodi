package org.isacrodi.diagnosis;

import java.io.IOException;
import org.isacrodi.ejb.entity.*;
import java.lang.*;

/**
 * Implements Feature Extractor Interface.
 */

public class DummyCDRFeatureExtractor implements CDRFeatureExtractor
{

  public DummyCDRFeatureExtractor()
  {
    super();
  }


  public FeatureVector extract(CropDisorderRecord cropDisorderRecord)
  {

    FeatureVector featureVector = new FeatureVector();

    SymptomDescriptor sd = new SymptomDescriptor();
    NumericDescriptor nd = new NumericDescriptor();

    for (Object o : cropDisorderRecord.getDescriptorSet())
    {
      if (o.getClass().isInstance(new SymptomDescriptor()))
      {
        sd = (SymptomDescriptor)o;
        CategoricalFeature cf = new CategoricalFeature(sd.getSymptomType().getTypeName(), sd.getSymptomValue());
        featureVector.put(sd.getSymptomType().getTypeName(), cf);
      }

      if (o.getClass().isInstance(new NumericDescriptor()))
      {
        nd = (NumericDescriptor)o;
        NumericFeature nf = new NumericFeature(nd.getNumericType().getTypeName(), nd.getNumericValue());
        featureVector.put(nd.getNumericType().getTypeName(), nf);
      }
    }

    return featureVector;
  }

}

