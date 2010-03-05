package org.isacrodi.diagnosis;

import org.isacrodi.ejb.entity.*;
import java.io.IOException;
import java.util.Set;
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
    int diagId = 0;

    CropDisorder cropDisorder;
    Diagnosis diagnosis = new Diagnosis();

    cropDisorder = new CropDisorder("anthracnose", "Colletotrichum coccodes");
    cropDisorder.setId(1);

    

    CDRFeatureExtractor c = new DummyCDRFeatureExtractor();
    FeatureVector dfv = c.extract(cropDisorderRecord);

    ImageFeatureExtractor ie = new DummyImageFeatureExtractor();
    ImageDescriptor imageDescriptorSet = cropDisorderRecord.getImageDescriptorSet();
    FeatureVector ifv = ie.extract(imageDescriptorSet);
    
    FeatureVector featureVector = new FeatureVector();

    for (String k : dfv.keySet())
      featureVector.put(k, dfv.get(k));
    for (String k : ifv.keySet())
      featureVector.put(k, ifv.get(k));
   
    featureVector.put("crop", (double)cropDisorderRecord.getCrop().getId());

    FeatureClassifier cl = new FeatureClassifier();
    diagId= cl.DummyClassifier(featureVector);
    diagnosis.setCropDisorderSet(new java.util.HashSet<CropDisorder>());

    if (cropDisorder.getId() == diagId)
      diagnosis.addCropDisorder(cropDisorder);

    return (new Diagnosis());   
  }

}

