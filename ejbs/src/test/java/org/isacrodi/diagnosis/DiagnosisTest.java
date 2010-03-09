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
  private NumericType numericType;
  private NumericDescriptor numericDescriptor;
  private SymptomType symptomType;
  private SymptomDescriptor symptomDescriptor;
  private ImageType imageType;
  private ImageDescriptor imageDescriptor;
  private Crop tomato;
  private CropDisorderRecord cropDisorderRecord;
  private CropDisorder cropDisorder;


  @Before
  public void setUp() throws IOException
  {


    this.numericType = new NumericType("temperature");
    this.numericDescriptor = new NumericDescriptor();
    this.numericDescriptor.setId(11);
    this.numericDescriptor.setNumericType(this.numericType);
    this.numericDescriptor.setNumericValue(27.0);

    this.symptomType = new SymptomType("leaf_symptom");
    this.symptomDescriptor = new SymptomDescriptor();
    this.symptomDescriptor.setId(8);
    this.symptomDescriptor.setSymptomType(this.symptomType);
    this.symptomDescriptor.setSymptomValue(1);

    this.imageType = new ImageType("leaf");
    this.imageDescriptor = new ImageDescriptor();
    this.imageDescriptor.setId(12);
    this.imageDescriptor.setImageType(this.imageType);
    this.imageDescriptor.setMimeType("image/jpeg");
    String jpegFileName = "src/test/java/org/isacrodi/diagnosis/uchuva.jpg";
    File jpegFile = new File(jpegFileName);
    long jpegLength = jpegFile.length();
    byte[] jpegData = new byte[(int) jpegLength];
    FileInputStream f = new FileInputStream(jpegFile);
    if (f.read(jpegData) != (int) jpegLength)
    {
      throw new RuntimeException(String.format("failure to read image data from %s", jpegFileName));
    }
    this.imageDescriptor.setImageData(jpegData);

    this.tomato = new Crop("Tomato", "Lycopersicon esculentum");
    this.tomato.setId(1);
    this.cropDisorderRecord = new CropDisorderRecord();
    this.cropDisorderRecord.setCrop(this.tomato);
    this.cropDisorderRecord.setDescriptorSet(new java.util.HashSet<Descriptor>());
    this.cropDisorderRecord.addDescriptor(this.numericDescriptor);
    this.cropDisorderRecord.addDescriptor(this.symptomDescriptor);
    this.cropDisorderRecord.addDescriptor(this.imageDescriptor);
  }


  @Test(expected = IllegalStateException.class)
  public void testInvalidMimeType() throws IOException
  {
    ImageDescriptor idBroken = new ImageDescriptor();
    idBroken.setId(13);
    idBroken.setImageType(this.imageType);
    idBroken.setMimeType("image/blah");
    BufferedImage bi = idBroken.bufferedImage();
  }


  @Test
  public void testImageFeatureExtractor() throws IOException
  {
    Assert.assertNotNull(this.imageDescriptor);
    Assert.assertNotNull(this.imageDescriptor.bufferedImage());
    ImageFeatureExtractor ife = new DummyImageFeatureExtractor();
    FeatureVector featureVector = ife.extract(this.imageDescriptor);
    // JTK: this should be a method, not a publicly accessible member
    System.out.println(featureVector.toString());
    Assert.assertNotNull(featureVector);
  }


  @Test 
  public void testCDRFeatureExtractor() throws IOException
  {
    Assert.assertNotNull(this.symptomDescriptor);
    CDRFeatureExtractor cdrfe = new DummyCDRFeatureExtractor();
    FeatureVector featureVector = cdrfe.extract(this.cropDisorderRecord);
    Assert.assertNotNull(featureVector);
  }


  @Test
  public void testDiagnosisProvider() throws IOException
  {
    DiagnosisProvider dp = new DummyDiagnosisProvider();
    Diagnosis diagnosis = dp.diagnose(this.cropDisorderRecord);
    // this.cropDisorderRecord.setDiagnosis(diagnosis);
    Assert.assertTrue(diagnosis != null);
  }
}
