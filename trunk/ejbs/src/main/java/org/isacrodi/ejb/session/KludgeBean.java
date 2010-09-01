package org.isacrodi.ejb.session;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;

import javax.ejb.Stateless;
import javax.ejb.Remote;
import javax.ejb.EJB;
import javax.persistence.PersistenceContext;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.isacrodi.ejb.entity.*;

import static org.javamisc.Util.genericTypecast;


/**
 * An ArrayList that supports drawing of random samples.
 */
class SamplableList<T> extends ArrayList<T>
{
  public SamplableList()
  {
    super();
  }


  public SamplableList(Collection<? extends T> c)
  {
    this();
    for (T e : c)
    {
      this.add(e);
    }
  }


  public T randomSample(Random rng)
  {
    if (this.size() == 0)
    {
      return (null);
    }
    else
    {
      return (this.get(rng.nextInt(this.size())));
    }
  }


  public List<T> randomSampleList(Random rng, int numSamples)
  {
    if (this.size() < numSamples)
    {
      throw new RuntimeException(String.format("request of %d samples from a list of %d elements", numSamples, this.size()));
    }
    int[] index = new int[this.size()];
    for (int i = 0; i < this.size(); i++)
    {
      index[i] = i;
    }
    for (int i = 0; i < this.size(); i++)
    {
      int j = rng.nextInt(this.size());
      int tmp = index[i];
      index[i] = index[j];
      index[j] = tmp;
    }
    ArrayList<T> l = new ArrayList<T>();
    for (int i = 0; i < numSamples; i++)
    {
      l.add(this.get(index[i]));
    }
    return (l);
  }
}


@Stateless
@Remote({Kludge.class})
public class KludgeBean implements Kludge
{
  @PersistenceContext
  private EntityManager entityManager;


  public void concoctDiagnosis(Integer cdrId)
  {
    CropDisorderRecord cdr = this.entityManager.find(CropDisorderRecord.class, cdrId);
    if (cdr == null)
    {
      return;
    }
    Diagnosis diagnosis = new Diagnosis();
    diagnosis.linkCropDisorderRecord(cdr);
    this.entityManager.persist(diagnosis);
  }


  public void makeRandomExpertDiagnosedCDRs(int rndseed, int numNumericTypes, int numCrops, int numCropDisorders, int numCDRs, int numDisorderAssociations, double numericDescriptorPercentage, double stddevBetween, double stddevWithin)
  {
    Random rng = new Random(rndseed);
    IsacrodiUser rndUser = new IsacrodiUser("Dummy", "Random", "rnduser", IsacrodiUser.hash("rnd"), "random@generator.org");
    this.entityManager.persist(rndUser);
    SamplableList<NumericType> numericTypeList = new SamplableList<NumericType>();
    for (int i = 0; i < numNumericTypes; i++)
    {
      NumericType numericType = new NumericType(String.format("rndNT_%03d", i));
      this.entityManager.persist(numericType);
      numericTypeList.add(numericType);
    }
    SamplableList<Crop> cropList = new SamplableList<Crop>();
    for (int i = 0; i < numCrops; i++)
    {
      Crop crop = new Crop(String.format("rndCrop_%03d", i), String.format("Yerba randomica_%03d", i));
      this.entityManager.persist(crop);
      cropList.add(crop);
    }
    SamplableList<CropDisorder> cropDisorderList = new SamplableList<CropDisorder>();
    HashMap<String, HashMap<String, Double>> disorderCharacteristicsMap = new HashMap<String, HashMap<String, Double>>();
    for (int i = 0; i < numCropDisorders; i++)
    {
      CropDisorder cropDisorder = new CropDisorder(String.format("rndDisorder_%03d", i), String.format("Fungus randomicus_%03d", i));
      Procedure procedure = new Procedure(String.format("rndCure_%03d", i));
      cropDisorder.linkProcedure(procedure);
      this.entityManager.persist(procedure);
      this.entityManager.persist(cropDisorder);
      cropDisorderList.add(cropDisorder);
      HashMap<String, Double> disorderCharacteristics = new HashMap<String, Double>();
      String cropDisorderDescription = String.format("dummy disorder #%d\n", i);
      for (NumericType nt : numericTypeList)
      {
        double c = new Double(rng.nextGaussian() * stddevBetween);
        disorderCharacteristics.put(nt.getTypeName(), c);
        cropDisorderDescription += String.format("%s: %s\n", nt.getTypeName(), c);
      }
      disorderCharacteristicsMap.put(cropDisorder.getScientificName(), disorderCharacteristics);
      cropDisorder.setDescription(cropDisorderDescription);
    }
    for (int i = 0; i < numDisorderAssociations; i++)
    {
      Crop crop = cropList.randomSample(rng);
      CropDisorder cropDisorder = cropDisorderList.randomSample(rng);
      if (!crop.getCropDisorderSet().contains(cropDisorder))
      {
        crop.linkCropDisorder(cropDisorder);
      }
    }
    SamplableList<CropDisorderRecord> cropDisorderRecordList = new SamplableList<CropDisorderRecord>();
    for (int i = 0; i < numCDRs; i++)
    {
      CropDisorderRecord cdr = new CropDisorderRecord();
      cdr.linkIsacrodiUser(rndUser);
      this.entityManager.persist(cdr);
      cdr.linkCrop(cropList.randomSample(rng));
      Crop crop = cdr.getCrop();
      SamplableList<CropDisorder> dList = new SamplableList<CropDisorder>(crop.getCropDisorderSet());
      String cdrDescription = String.format("dummy cdr #%d\n", i);
      if (dList.size() > 0)
      {
        CropDisorder edd = dList.randomSample(rng);
        cdr.linkExpertDiagnosedCropDisorder(edd);
        cdrDescription += String.format("expert diagnosed disorder is #%d: %s\n", edd.getId().intValue(), edd.getScientificName());
        CropDisorder cropDisorder = cdr.getExpertDiagnosedCropDisorder();
        HashMap<String, Double> disorderCharacteristics = disorderCharacteristicsMap.get(cropDisorder.getScientificName());
        for (NumericType nt : numericTypeList)
        {
          if (rng.nextDouble() < numericDescriptorPercentage)
          {
            double m = disorderCharacteristics.get(nt.getTypeName()).doubleValue(); // mean of diagnosed disorder
            double jitter = rng.nextGaussian() * stddevWithin;
            NumericDescriptor nd = new NumericDescriptor(nt, m + jitter);
            cdrDescription += String.format("%s: %f + %f\n", nt.getTypeName(), m, jitter);
            cdr.linkDescriptor(nd);
            this.entityManager.persist(nd);
          }
        }
      }
      cdr.setDescription(cdrDescription);
      cropDisorderRecordList.add(cdr);
    }
  }


  public void makeDummies()
  {
    Dummy1 d1a = new Dummy1("a");
    Dummy1 d1b = new Dummy1("b");
    Dummy2 d2 = new Dummy2("blah");
    this.entityManager.persist(d1a);
    this.entityManager.persist(d1b);
    this.entityManager.persist(d2);
    d1a.linkXyzDummy2(d2);
    d1b.linkXyzDummy2(d2);
    for (int i = 0; i < 10; i++)
    {
      Dummy0 d0 = new Dummy0(String.format("d0-%02d", i));
      this.entityManager.persist(d0);
      if ((i % 2) == 0)
      {
	d0.linkDummy1(d1a);
      }
      else
      {
	d0.linkDummy1(d1b);
      }
    }
  }


  public Dummy0 findDummy0(Integer id)
  {
    Dummy0 dummy0 = this.entityManager.find(Dummy0.class, id);
    return (dummy0);
  }


  public Dummy0 findDummy0(String name)
  {
    Query query = this.entityManager.createQuery("SELECT d0 FROM Dummy0 d0 WHERE something = :s");
    query.setParameter("s", name);
    Dummy0 dummy0 = (Dummy0) query.getSingleResult();
    return (dummy0);
  }


  public void makeScenario()
  {
    IsacrodiUser isacrodiUser = new IsacrodiUser("Kim", "Jan Dummy", "jdk", IsacrodiUser.hash("blah"), "jttkim@gmail.com");
    this.entityManager.persist(isacrodiUser);
    Crop crop = new Crop("barley", "Hordeum vulgare");
    this.entityManager.persist(crop);
    CropDisorder cropDisorder = new CropDisorder("rust", "Fungus horribilis");
    cropDisorder.linkCrop(crop);
    this.entityManager.persist(cropDisorder);
    CropDisorderRecord cdr = new CropDisorderRecord();
    cdr.linkCrop(crop);
    cdr.linkExpertDiagnosedCropDisorder(cropDisorder);
    cdr.linkIsacrodiUser(isacrodiUser);
    this.entityManager.persist(cdr);
    ImageType imageType = new ImageType("testpic");
    this.entityManager.persist(imageType);
    ImageDescriptor imageDescriptor = new ImageDescriptor();
    imageDescriptor.linkDescriptorType(imageType);
    imageDescriptor.linkCropDisorderRecord(cdr);
    this.entityManager.persist(imageDescriptor);
  }


  public ImageDescriptor findImageDescriptor(Integer id)
  {
    ImageDescriptor imageDescriptor = this.entityManager.find(ImageDescriptor.class, id);
    return (imageDescriptor);
    /*
    System.err.println("got image descriptor: " + imageDescriptor.toString());
    CropDisorderRecord cropDisorderRecord = imageDescriptor.getCropDisorderRecord();
    if (cropDisorderRecord != null)
    {
      System.err.println("cdr: " + cropDisorderRecord.toString());
      CropDisorder cropDisorder = cropDisorderRecord.getExpertDiagnosedCropDisorder();
      if (cropDisorder != null)
      {
	System.err.println("expert diagnosis: " + cropDisorder.toString());
      }
    }
    if ((imageDescriptor != null) && (imageDescriptor.getCropDisorderRecord() != null))
    {
      CropDisorder cropDisorder = imageDescriptor.getCropDisorderRecord().getExpertDiagnosedCropDisorder();
      if (cropDisorder != null)
      {
	System.err.println(String.format("expert diagnosed crop disorder version: %d", cropDisorder.getVersion()));
      }
    }
    System.err.println("sleeping a second");
    try
    {
      Thread.sleep(1000);
    }
    catch (InterruptedException e)
    {
      System.err.println("rudely woken up by " + e);
    }
    System.err.println("returning");
    return (imageDescriptor);
    */
  }
}
