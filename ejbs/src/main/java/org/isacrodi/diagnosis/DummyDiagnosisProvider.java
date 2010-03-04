package org.isacrodi.diagnosis;

import org.isacrodi.ejb.entity.*;
import java.io.IOException;

/**
  * Implements Diagnosis Provider Interface
 */
public class DummyDiagnosisProvider implements DiagnosisProvider
{

  public DummyDiagnosisProvider()
  {
    super();
  }


  public Diagnosis diagnose(CropDisorderRecord cropDisorderRecord)
  {
    CDRFeatureVectorExtractor c = new CDRFeatureVectorExtractor();
    FeatureVector descriptorFeatureVector = c.extract(cropDisorderRecord);
    Set<ImageDescriptor> imageDescriptorSet = cropDisorderRecord.getImageDescriptorSet();
    if (imageDescriptorSet.size() > 0)
    {
      ImageFeatureVectorExtractor ie = new ImageFeatureVectorExtractor();
      FeatureVector ifv = ie.extract
    }
    return (new Diagnosis());   
  }


  public String performDiagnosis(FeatureVector featureVector)
  {
    String diagnosis;
    FeatureClassifier cl = new FeatureClassifier(featureVector);
    diagnosis = cl.DummyClassifier();
    return "disease"; 
  }

}

