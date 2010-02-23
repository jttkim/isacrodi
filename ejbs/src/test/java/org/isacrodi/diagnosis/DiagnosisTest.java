package org.isacrodi.diagnosis;

import org.junit.*;
import java.awt.image.*;
import java.io.*;
import java.sql.*;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.*;


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
  public void testDiagnosisProvider() throws IOException
  {
    // FIXME: use better variable names
    Crop tomato = new Crop("Tomato", "Lycopersicon esculentum");
    CropDisorderRecord cropDisorderRecord = new CropDisorderRecord();
    cropDisorderRecord.setCrop(tomato);
    NumericType nt = new NumericType("temperature");
    NumericDescriptor nd = new NumericDescriptor();
    nd.setId(11);
    nd.setNumericType(nt);
    nd.setValue(27.0);
    cropDisorderRecord.setDescriptorSet(new java.util.HashSet<Descriptor>());
    cropDisorderRecord.addDescriptor(nd);
    FileInputStream f = new FileInputStream("src/test/java/org/isacrodi/diagnosis/uchuva.jpg");
    byte b[] = new byte [f.available()];
    ImageType it = new ImageType("leaf");
    ImageDescriptor id = new ImageDescriptor();
    id.setId(12);
    id.setImageType(it);
    id.setMimeType("image");
    id.setImageData(b);   
    DiagnosisProvider dp = new DummyDiagnosisProvider();
    Diagnosis diagnosis = dp.diagnose(cropDisorderRecord);
    Assert.assertTrue(diagnosis != null);
  }
}
