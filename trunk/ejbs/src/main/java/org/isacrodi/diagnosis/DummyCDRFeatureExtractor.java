package org.isacrodi.diagnosis;

import java.io.IOException;
import org.isacrodi.ejb.entity.*;
import java.lang.*;

// FIXME: this doesn't get much more clever... should rename it from "dummy" to something more appropriate (adapter?)
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
    for (Object o : cropDisorderRecord.getDescriptorSet())
    {
      if (o.getClass().isInstance(new SymptomDescriptor()))
      {
        SymptomDescriptor sd = (SymptomDescriptor)o;
        CategoricalFeature cf = new CategoricalFeature(sd.getSymptomType().getTypeName(), sd.getSymptomValue());
        featureVector.put(sd.getSymptomType().getTypeName(), cf);
      }

      if (o.getClass().isInstance(new NumericDescriptor()))
      {
        NumericDescriptor nd = (NumericDescriptor)o;
        NumericFeature nf = new NumericFeature(nd.getNumericType().getTypeName(), nd.getNumericValue());
        featureVector.put(nd.getNumericType().getTypeName(), nf);
      }
    }
    return featureVector;
  }
}

