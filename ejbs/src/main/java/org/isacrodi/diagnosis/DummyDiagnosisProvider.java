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
    DisorderScore ds;
    Diagnosis diagnosis;

    diagnosis = new Diagnosis();
    diagnosis.setId(1);
    ds = new DisorderScore();
   
    //for(Diagnosis d : cropDisorderRecord.getDiagnosis())
    System.out.println("Diagnosis Int:  "+ cropDisorderRecord.getDiagnosis()+"\n");
   
    //Set<DisorderScorePK> dsPKa = new Set<DisorderScorePK>();  
    //DisorderScorePK dsPK = new DisorderScorePK();
    //dsPK.setDiagnosis(diagnosis);
    
    CDRFeatureExtractor c = new DummyCDRFeatureExtractor();
    FeatureVector dfv = c.extract(cropDisorderRecord);

    ImageFeatureExtractor ie = new DummyImageFeatureExtractor();
    ImageDescriptor imageDescriptorSet = getImageDescriptorSet(cropDisorderRecord);
    FeatureVector ifv = ie.extract(imageDescriptorSet);
    
    FeatureVector featureVector = new FeatureVector();

    for (String k : dfv.keySet())
      featureVector.put(k, dfv.get(k));
    for (String k : ifv.keySet())
      featureVector.put(k, ifv.get(k));
   
    featureVector.put("crop", (double)cropDisorderRecord.getCrop().getId());

    FeatureClassifier cl = new FeatureClassifier();
    ds = cl.DummyClassifier(featureVector, diagnosis);

    return (diagnosis);   
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


  public NumericDescriptor getNumericDescriptorSet(CropDisorderRecord cropDisorderRecord)
  {
     NumericDescriptor ndes = new NumericDescriptor();

     for (Object o : cropDisorderRecord.getDescriptorSet())
     {
       if (o.getClass().isInstance(new NumericDescriptor()))
       {
         ndes = (NumericDescriptor)o;
       }
     }
     return ndes;
  }

}

