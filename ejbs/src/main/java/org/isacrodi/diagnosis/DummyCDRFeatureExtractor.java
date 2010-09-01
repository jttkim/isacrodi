package org.isacrodi.diagnosis;

import java.io.IOException;
import java.io.Serializable;

import org.isacrodi.ejb.entity.*;


// FIXME: this doesn't get much more clever... should rename it from "dummy" to something more appropriate (adapter?)
/**
 * Implements Feature Extractor Interface.
 */
public class DummyCDRFeatureExtractor implements CDRFeatureExtractor, Serializable
{
  private static final long serialVersionUID = 1;


  public DummyCDRFeatureExtractor()
  {
    super();
  }


  public FeatureVector extract(CropDisorderRecord cropDisorderRecord)
  {
    FeatureVector featureVector = new FeatureVector();
    for (Descriptor descriptor : cropDisorderRecord.getDescriptorSet())
    {
      if (descriptor instanceof CategoricalDescriptor)
      {
	CategoricalDescriptor categoricalDescriptor = (CategoricalDescriptor) descriptor;
	// FIXME: responsibilities blurred -- extract extracts the type name but constructor extracts value names
	CategoricalFeature categoricalFeature = new CategoricalFeature(categoricalDescriptor.getDescriptorType().getTypeName(), categoricalDescriptor.getCategoricalTypeValueSet());
        featureVector.put(categoricalFeature.getName(), categoricalFeature);
      }
      else if (descriptor instanceof NumericDescriptor)
      {
	NumericDescriptor numericDescriptor = (NumericDescriptor) descriptor;
	NumericFeature numericFeature = new NumericFeature(numericDescriptor.getDescriptorType().getTypeName(), numericDescriptor.getNumericValue());
	featureVector.put(numericFeature.getName(), numericFeature);
      }
    }
    return featureVector;
  }
}
