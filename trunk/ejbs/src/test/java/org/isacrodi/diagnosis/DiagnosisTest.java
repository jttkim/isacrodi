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
import libsvm.*;


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
  private CropDisorderRecord emptyCDR;
  private CropDisorderRecord cropDisorderRecord;
  private Set<Crop> cropSet;
  private Set<CropDisorder> cropDisorderSet;
  private Set<Procedure> procedureSet;
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

    this.symptomType = new SymptomType("traces");
    this.symptomDescriptor = new SymptomDescriptor();
    this.symptomDescriptor.setId(8);
    this.symptomDescriptor.setSymptomType(this.symptomType);
    //FIXME: Changed double for String to test classifier, I think it should be left like that -- avc.
    this.symptomDescriptor.setSymptomValue("circular");

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

    // Add crop disorder
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

    // Add crop disorder
    CropDisorder cdAub1 = new CropDisorder("Bacterial wilt", "Ralstonia (Pseudomonas) solanacearum");
    cdAub1.setId(4);
    cdAub1.setCropSet(new java.util.HashSet<Crop>());
    cdAub1.addCrop(this.aubergine);
    cdAub1.setProcedureSet(new java.util.HashSet<Procedure>());
    cdAub1.addProcedure(proc4);

    CropDisorder cdAub2 = new CropDisorder("Verticillium Wilt", "Verticillium sp");
    cdAub2.setId(5);
    cdAub2.setCropSet(new java.util.HashSet<Crop>());
    cdAub2.addCrop(this.aubergine);
    cdAub2.setProcedureSet(new java.util.HashSet<Procedure>());
    cdAub2.addProcedure(proc1);
    cdAub2.addProcedure(proc5);

    this.aubergine.setCropDisorderSet(new java.util.HashSet<CropDisorder>());
    this.aubergine.addCropDisorder(cdAub1);
    this.aubergine.addCropDisorder(cdAub2);

    // Create crop set
    this.cropSet = new HashSet<Crop>();
    this.cropSet.add(this.tomato);
    this.cropSet.add(this.aubergine);
    this.cropDisorderSet = new HashSet<CropDisorder>();
    cropDisorderSet.add(cdTom1);
    cropDisorderSet.add(cdTom2);
    cropDisorderSet.add(cdTom3);
    cropDisorderSet.add(cdAub1);
    cropDisorderSet.add(cdAub2);

    // create empty CDR
    this.emptyCDR = new CropDisorderRecord();
    this.emptyCDR.setId(new Integer(1));

    // Create CDR
    this.cropDisorderRecord = new CropDisorderRecord();
    this.emptyCDR.setId(new Integer(2));
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
    FeatureVector featureVector = ife.extract(this.cropDisorderRecord);
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
  public void testDiagnosisStuff() throws IOException
  {
    DummyDiagnosisProvider dp = new DummyDiagnosisProvider();
    dp.setKnownDisorderSet(this.cropDisorderSet);
    this.diagnosis = new Diagnosis();
    this.diagnosis.setId(1);
    this.diagnosis.setCropDisorderRecord(this.cropDisorderRecord);
    this.diagnosis.setDisorderScoreSet(new java.util.HashSet<DisorderScore>());
    this.cropDisorderRecord.setDiagnosis(this.diagnosis);

    for(Crop c : this.cropSet)
    {
      for(CropDisorder cd : c.getCropDisorderSet())
      {
        DisorderScore ds = new DisorderScore();
        ds.setDiagnosis(this.diagnosis);
	ds.setCropDisorder(cd);
        this.diagnosis.addDisorderScore(ds);
      }
    }

    // instance variable diagnosis obsolescent...
    Diagnosis diagnosis = dp.diagnose(this.cropDisorderRecord);
    // Test recommendation

    RecommendationProvider rp = new DummyRecommendationProvider();
    this.recommendation = new Recommendation();
    this.recommendation = rp.recommend(this.diagnosis);

    Assert.assertTrue(this.diagnosis != null);
  }

  /**
   * Test that all disorders get the same score when diagnosing an
   * empty CDR.
   *
   * @author jtk
   */
  @Test
  public void testEmptyDiagnosisProvider() throws IOException
  {
    Assert.assertTrue("no disorders", this.cropDisorderSet.size() > 0);
    DummyDiagnosisProvider dp = new DummyDiagnosisProvider();
    dp.setKnownDisorderSet(this.cropDisorderSet);
    /* FIXME: test partially disabled pending refactoring of classification*/
    
    Diagnosis d = dp.diagnose(this.emptyCDR);
    Assert.assertEquals(this.cropDisorderSet.size(), d.getDisorderScoreSet().size());
    double s = d.getDisorderScoreSet().iterator().next().getScore();
    for (DisorderScore ds : d.getDisorderScoreSet())
    {
      Assert.assertEquals(s, ds.getScore());
    }
    
  }


  /**
   * Test that only disorders that can affect the crop specified in
   * the CDR get a score greater than 0.
   *
   * @author jtk
   */
  @Test
  public void testDiagnosisProviderByCrop()
  {
    Assert.assertTrue("no disorders", this.cropDisorderSet.size() > 0);
    /* FIXME: partially disabled pending refactoring of classification*/
    
    DummyDiagnosisProvider dp = new DummyDiagnosisProvider();
    dp.setKnownDisorderSet(this.cropDisorderSet);
    Diagnosis d = dp.diagnose(this.cropDisorderRecord);
    for (DisorderScore ds : d.getDisorderScoreSet())
    {
      if (ds.getCropDisorder().getCropSet().contains(this.cropDisorderRecord.getCrop()))
      {
	Assert.assertTrue(ds.getScore() > 0.0);
      }
      else
      {
	Assert.assertEquals(0.0, ds.getScore());
      }
    }
    
  }


  /**
   * Test Feature Mapper
   *
   * @author jtk
   */
  @Test
  public void testSVMDiagnosis() throws IOException
  {
    SVMDiagnosisProvider dp = new SVMDiagnosisProvider();
    dp.setKnownDisorderSet(this.cropDisorderSet);
    this.diagnosis = new Diagnosis();
    this.diagnosis.setId(1);
    this.diagnosis.setCropDisorderRecord(this.cropDisorderRecord);
    this.diagnosis.setDisorderScoreSet(new java.util.HashSet<DisorderScore>());
    this.cropDisorderRecord.setDiagnosis(this.diagnosis);

    for(Crop c : this.cropSet)
    {
      for(CropDisorder cd : c.getCropDisorderSet())
      {
        DisorderScore ds = new DisorderScore();
        ds.setDiagnosis(this.diagnosis);
	ds.setCropDisorder(cd);
        this.diagnosis.addDisorderScore(ds);
      }
    }

    // instance variable diagnosis obsolescent...
    Diagnosis diagnosis = dp.diagnose(this.cropDisorderRecord);
    // Test recommendation

    //RecommendationProvider rp = new DummyRecommendationProvider();
    //this.recommendation = new Recommendation();
    //this.recommendation = rp.recommend(this.diagnosis);

    Assert.assertTrue(this.diagnosis != null);
  }


  /**
   * Test Feature Mapper
   *
   * @author avc
   */
  @Test
  public void testFeatureMapper()
  {
    FeatureVector featureVector = new FeatureVector();
    FeatureVectorMapper fvm = new FeatureVectorMapper();

    svm_node[] fv = null;
    fv = fvm.map(featureVector);
  }

}
