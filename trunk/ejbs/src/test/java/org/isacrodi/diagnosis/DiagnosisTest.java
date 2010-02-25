package org.isacrodi.diagnosis;

import org.junit.*;
import java.awt.image.*;
import java.awt.Image;
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

    try
    {
      String filename = "src/test/java/org/isacrodi/diagnosis/uchuva.jpg";
      FileInputStream f = new FileInputStream(filename);
      BufferedImage bufferImage = ImageIO.read(f);
      byte b[] = new byte [f.available()];

      ImageType it = new ImageType("leaf");
      ImageDescriptor id = new ImageDescriptor();
      id.setId(12);
      id.setImageType(it);
      id.setMimeType("image");
      id.setImageData(b);   
      id.setBufferedImage(bufferImage);   
      ImageFeatureExtractor ife = new DummyImageFeatureExtractor();
      FeatureVector featureVector = ife.extract(id);
      System.out.println("MEAN........... "+featureVector.mean);
      Assert.assertTrue(featureVector != null);
    }
    catch(IOException e)
     {
      System.err.println(e);
    }
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
    try {
      FileInputStream f = new FileInputStream("src/test/java/org/isacrodi/diagnosis/uchuva.jpg");
      byte b[] = new byte [f.available()];
      ImageType it = new ImageType("leaf");
      ImageDescriptor id = new ImageDescriptor();
      id.setId(12);
      id.setImageType(it);
      id.setMimeType("image");
      id.setImageData(b);   
    }
    catch (IOException e)
    {
      System.err.println(e);
    }
    DiagnosisProvider dp = new DummyDiagnosisProvider();
    Diagnosis diagnosis = dp.diagnose(cropDisorderRecord);
    cropDisorderRecord.setDiagnosis(diagnosis);
    Assert.assertTrue(diagnosis != null);
  }
}
