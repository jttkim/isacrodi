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
import java.util.Set;


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
  private Crop aubergine;
  private CropDisorderRecord cropDisorderRecord;
  private CropDisorder cropDisorder;
  private Set<Crop> cropList;
  private Set<CropDisorder> cropDisorderList;
  private Set<Procedure> procedureList;
  private Recommendation recommendation;
  private Diagnosis diagnosis;


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
   
    //Create procedures
    Procedure proc1 = new Procedure("Water the plant");
    proc1.setId(1);
    Procedure proc2 = new Procedure("Prune the plant");
    proc2.setId(2);
    Procedure proc3 = new Procedure("Apply soapy water to leaves");
    proc3.setId(3);
    Procedure proc4 = new Procedure("Burn crop down");
    proc4.setId(4);
    Procedure proc5 = new Procedure("Apply eco-duff");
    proc5.setId(5);

    // Add first crop 
    this.tomato = new Crop("Tomato", "Lycopersicon esculentum");
    this.tomato.setId(10);

    CropDisorder cdTom1 = new CropDisorder("anthracnose", "Colletotrichum coccodes");
    cdTom1.setId(1);
    cdTom1.setCropSet(new java.util.HashSet<Crop>());
    cdTom1.addCrop(this.tomato);
    cdTom1.setProcedureSet(new java.util.HashSet<Procedure>());
    cdTom1.addProcedure(proc1);
    cdTom1.addProcedure(proc2);

    CropDisorder cdTom2 = new CropDisorder("anthracmouth", "Colletotrichum mouth");
    cdTom2.setId(2);
    cdTom2.setCropSet(new java.util.HashSet<Crop>());
    cdTom2.addCrop(this.tomato);
    cdTom2.setProcedureSet(new java.util.HashSet<Procedure>());
    cdTom2.addProcedure(proc1);

    CropDisorder cdTom3 = new CropDisorder("anthraclips", "Colletotrichum lips");
    cdTom3.setId(3);
    cdTom3.setCropSet(new java.util.HashSet<Crop>());
    cdTom3.addCrop(this.tomato);
    cdTom3.setProcedureSet(new java.util.HashSet<Procedure>());
    cdTom3.addProcedure(proc1);
    cdTom3.addProcedure(proc2);
    cdTom3.addProcedure(proc5);

    this.tomato.setCropDisorderSet(new java.util.HashSet<CropDisorder>());
    this.tomato.addCropDisorder(cdTom1);
    this.tomato.addCropDisorder(cdTom2);
    this.tomato.addCropDisorder(cdTom3);

    // Add second crop 
    this.aubergine = new Crop("Aubergine", "Solanum melongena");
    this.aubergine.setId(20);
    CropDisorder cdAub1 = new CropDisorder("Bacterial wilt", "Ralstonia (Pseudomonas) solanacearum");
    cdAub1.setId(11);
    cdAub1.setCropSet(new java.util.HashSet<Crop>());
    cdAub1.addCrop(this.aubergine);
    cdAub1.setProcedureSet(new java.util.HashSet<Procedure>());
    cdAub1.addProcedure(proc4);

    CropDisorder cdAub2 = new CropDisorder("Verticillium Wilt", "Verticillium sp");
    cdAub2.setId(22);
    cdAub2.setCropSet(new java.util.HashSet<Crop>());
    cdAub2.addCrop(this.aubergine);
    cdAub2.setProcedureSet(new java.util.HashSet<Procedure>());
    cdAub2.addProcedure(proc1);
    cdAub2.addProcedure(proc5);

    this.aubergine.setCropDisorderSet(new java.util.HashSet<CropDisorder>());
    this.aubergine.addCropDisorder(cdAub1);
    this.aubergine.addCropDisorder(cdAub2);

    // Create crop set
    this.cropList = new HashSet<Crop>();
    this.cropList.add(this.tomato);
    this.cropList.add(this.aubergine);
    this.cropDisorderList = new HashSet<CropDisorder>();
    cropDisorderList.add(cdTom1);
    cropDisorderList.add(cdTom2);
    cropDisorderList.add(cdTom3);
    cropDisorderList.add(cdAub1);
    cropDisorderList.add(cdAub2);
  
    // Create CDR
    this.cropDisorderRecord = new CropDisorderRecord();
    this.cropDisorderRecord.setCrop(this.aubergine);
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
    this.diagnosis = new Diagnosis();
    this.cropDisorderRecord.setDiagnosis(diagnosis);
    this.diagnosis.setId(1);
    this.diagnosis.setCropDisorderRecord(this.cropDisorderRecord);
   
    this.diagnosis.setDisorderScoreSet(new java.util.HashSet<DisorderScore>());
    for(Crop c : this.cropList)
    {
      for(CropDisorder cd : c.getCropDisorderSet()) 
      {
        DisorderScore ds = new DisorderScore();
        ds.setDiagnosis(diagnosis);
	ds.setCropDisorder(cd);
        this.diagnosis.addDisorderScore(ds); 
      }
    }

    this.diagnosis = dp.diagnose(this.cropDisorderRecord);
    for (DisorderScore o : this.cropDisorderRecord.getDiagnosis().getDisorderScoreSet())
    {
      for (Crop c : o.getCropDisorder().getCropSet())
        System.out.print("CROP:  " + c.getName() + " ");
        System.out.print("Disease: " + o.getCropDisorder().getName() + " ");
        System.out.print("Procedure: " + o.getCropDisorder().getProcedureSet() + " ");
        System.out.println("Score: " + o.getScore());
    }
    
    
    // Test recommendation

    RecommendationProvider rp = new DummyRecommendationProvider();
    this.recommendation = new Recommendation(); 
    this.cropDisorderRecord.setRecommendation(this.recommendation);
    this.recommendation.setId(1);

    for(DisorderScore cd : this.diagnosis.getDisorderScoreSet())
    {
      for(Procedure pr : cd.getCropDisorder().getProcedureSet())
        System.out.println("DISORDER SCORE: " + pr);
    }

    Assert.assertTrue(this.diagnosis != null);
  }
}
