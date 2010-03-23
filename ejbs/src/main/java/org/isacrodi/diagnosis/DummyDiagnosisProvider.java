package org.isacrodi.diagnosis;

import org.isacrodi.ejb.entity.*;
import java.io.IOException;
import java.util.Set;
import java.util.HashSet;


/**
 * Implements Diagnosis Provider Interface
 */
public class DummyDiagnosisProvider implements DiagnosisProvider
{
  private Set<CropDisorder> cropDisorderSet;

  
  public DummyDiagnosisProvider()
  {
    super();
  }


  public void setKnownDisorderSet(Set<CropDisorder> cropDisorderSet)
  {
    this.cropDisorderSet = cropDisorderSet;
  }


  public Diagnosis diagnose(CropDisorderRecord cropDisorderRecord)
  {
    Diagnosis d = new Diagnosis();
    d.setCropDisorderRecord(cropDisorderRecord);
    d.setDisorderScoreSet(new HashSet<DisorderScore>());

    for (CropDisorder disorder : this.cropDisorderSet)
    {
      DisorderScore ds = new DisorderScore();
      if(cropDisorderRecord.getCrop() == null)
        ds.setScore(1.0 / this.cropDisorderSet.size());
      else if (disorder.getCropSet().contains(cropDisorderRecord.getCrop()))
        ds.setScore(1.0 / CountCrop(cropDisorderRecord.getCrop()));
      else
	ds.setScore(0.0); 
      ds.setDiagnosis(d);
      ds.setCropDisorder(disorder);
      d.addDisorderScore(ds);
    }
    return (d);
  }


  public int CountCrop(Crop crop)
  {

    int counter = 0;

    for(CropDisorder cdi : this.cropDisorderSet)
    {
      if(cdi.getCropSet().contains(crop))
        counter++;
    }

    return counter;
  }


  public Diagnosis diagnoseOld(CropDisorderRecord cropDisorderRecord)
  {
   
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
    cl.DummyClassifier(featureVector, cropDisorderRecord.getDiagnosis().getDisorderScoreSet());

    return (cropDisorderRecord.getDiagnosis());   
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

