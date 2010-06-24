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
	cdr.linkExpertDiagnosedCropDisorder(dList.randomSample(rng));
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
}
