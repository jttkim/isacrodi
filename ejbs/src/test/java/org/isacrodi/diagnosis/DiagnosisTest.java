package org.isacrodi.diagnosis;

import org.junit.*;
import org.isacrodi.ejb.entity.*;


public class DiagnosisTest
{
  @Test
  public void testImageFeatureExtractor()
  {
    // set up an image descriptor
    // get an instance of a dummy class that implements ImageFeatureExtractor
    // run some test ...
  }


  @Test
  public void testDiagnosisProvider()
  {
    // FIXME: use better variable names
    Crop tomato = new Crop("Tomato", "Lycopersicon esculentum");
    CropDisorderRecord cropDiseaseRecord = new CropDiseaseRecord();
    cropDisorderRecord.setCrop(tomato);
    NumericType nt = new NumericType("temperature");
    NumericDescriptor nd = new NumericDescriptor();
    nd.setId(11);
    nd.setNumericType(nt);
    nd.setValue(27.0);
    // addDescriptor method is currently missing from CropDisorderRecord
    cropDisorderRecord.addDescriptor(nd);
    ImageDescriptor id = new ImageDescriptor();
    // fill id ...
    cropDisorderRecord.addDescriptor(id);
    DiagnosisProvider dp = new DummyDiagnosisProvider();
    Diagnosis diagnosis = dp.diagnose(cropDiseaseRecord);
    Assert.assertTrue(diagnosis != null);
  }
}
