package org.isacrodi.ejb.session;

import java.io.Serializable;
import java.io.IOException;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.EJB;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.isacrodi.ejb.entity.*;

import org.isacrodi.diagnosis.DiagnosisProvider;
import org.isacrodi.diagnosis.SVMDiagnosisProvider;
import org.isacrodi.diagnosis.RecommendationProvider;
import org.isacrodi.diagnosis.SimpleRecommendationProvider;


import static org.javamisc.Util.genericTypecast;


@Stateless
@Remote(CropDisorderRecordManager.class)
public class CropDisorderRecordManagerBean implements CropDisorderRecordManager, Serializable
{
  private RecommendationProvider recommendationProvider;

  @PersistenceContext
  private EntityManager entityManager;

  @EJB
  private Kludge kludge;

  private static final long serialVersionUID = 1;


  public CropDisorderRecordManagerBean()
  {
    super();
    this.recommendationProvider = new SimpleRecommendationProvider(1.0);
  }


  private void fetchSets(CropDisorderRecord cdr)
  {
    for (Descriptor descriptor : cdr.getDescriptorSet())
    {
      descriptor.getId();
      for (Descriptor c : (Set<? extends Descriptor>) (descriptor.getDescriptorType().getDescriptorSet()))
      {
        c.getId();
	if (c instanceof CategoricalDescriptor)
	{
	  CategoricalDescriptor categoricalDescriptor = (CategoricalDescriptor) c;
	  for (CategoricalTypeValue categoricalTypeValue : categoricalDescriptor.getCategoricalTypeValueSet())
	  {
	    categoricalTypeValue.getId();
	  }
	}
      }
    }
    Diagnosis diagnosis = cdr.getDiagnosis();
    if (diagnosis != null)
    {
      for (DisorderScore disorderScore : diagnosis.getDisorderScoreSet())
      {
        disorderScore.getId();
      }
    }
    Recommendation recommendation = cdr.getRecommendation();
    if (recommendation != null)
    {
      for (ProcedureScore procedureScore : recommendation.getProcedureScoreSet())
      {
        procedureScore.getId();
      }
    }
  }


  public List<CropDisorderRecord> findCropDisorderRecordList()
  {
    Query query = this.entityManager.createQuery("SELECT r FROM CropDisorderRecord r ORDER BY r.id");
    List<CropDisorderRecord> cdrList = genericTypecast(query.getResultList());
    for (CropDisorderRecord cdr : cdrList)
    {
      this.fetchSets(cdr);
    }
    return(cdrList);
  }


  public List<CropDisorderRecord> findExpertDiagnosedCropDisorderRecordList()
  {
    Query query = this.entityManager.createQuery("SELECT r FROM CropDisorderRecord r WHERE r.expertDiagnosedCropDisorder IS NOT NULL ORDER BY r.id");
    List<CropDisorderRecord> cdrList = genericTypecast(query.getResultList());
    for (CropDisorderRecord cdr : cdrList)
    {
      fetchSets(cdr);
    }
    return(cdrList);
  }


  public CropDisorderRecord findCropDisorderRecord(Integer id)
  {
    CropDisorderRecord cropDisorderRecord = this.entityManager.find(CropDisorderRecord.class, id);
    if (cropDisorderRecord != null)
    {
      fetchSets(cropDisorderRecord);
    }
    return (cropDisorderRecord);
  }


  public void requestDiagnosis(int cropDisorderRecordId)
  {
    // FIXME: should check for existing diagnosis and recommendation and delete these first
    CropDisorderRecord cropDisorderRecord = this.entityManager.find(CropDisorderRecord.class, new Integer(cropDisorderRecordId));
    if (cropDisorderRecord == null)
    {
      throw new RuntimeException(String.format("no crop disorder record with id %s found", cropDisorderRecordId));
    }
    DiagnosisProvider diagnosisProvider = null;
    try
    {
      diagnosisProvider = this.kludge.findDiagnosisProvider();
    }
    catch (IOException e)
    {
      System.err.println("CropDisorderRecordManagerBean.requestDiagnosis: caught IOException trying to find dignosis provider");
    }
    catch (ClassNotFoundException e)
    {
      System.err.println("CropDisorderRecordManagerBean.requestDiagnosis: caught ClassNotFoundException trying to find dignosis provider");
    }
    if (diagnosisProvider != null)
    {
      Query query = this.entityManager.createQuery("SELECT d FROM CropDisorder d");
      List<CropDisorder> cropDisorderList = genericTypecast(query.getResultList());
      Diagnosis diagnosis = diagnosisProvider.diagnose(cropDisorderRecord, cropDisorderList);
      this.entityManager.persist(diagnosis);
      for (DisorderScore disorderScore : diagnosis.getDisorderScoreSet())
      {
	for (Procedure procedure : disorderScore.getCropDisorder().getProcedureSet())
	{
	  procedure.getId();
	}
	this.entityManager.persist(disorderScore);
      }
      Recommendation recommendation = this.recommendationProvider.recommend(diagnosis);
      this.entityManager.persist(recommendation);
      for (ProcedureScore procedureScore : recommendation.getProcedureScoreSet())
      {
	this.entityManager.persist(procedureScore);
      }
    }
  }


  public void updateNumericDescriptors(Integer cropDisorderRecordId, Map<Integer, Double> numericDescriptorMap)
  {
    CropDisorderRecord cropDisorderRecord = this.entityManager.find(CropDisorderRecord.class, cropDisorderRecordId);
    if (cropDisorderRecord == null)
    {
      throw new RuntimeException(String.format("no crop disorder record with id = %d", cropDisorderRecordId.intValue()));
    }
    Set<NumericDescriptor> oldNumericDescriptorSet = cropDisorderRecord.findNumericDescriptorSet();
    for (NumericDescriptor oldNumericDescriptor : oldNumericDescriptorSet)
    {
      this.entityManager.remove(oldNumericDescriptor);
    }
    for (Integer numericTypeId : numericDescriptorMap.keySet())
    {
      NumericType numericType = (NumericType) this.entityManager.find(NumericType.class, numericTypeId);
      if (numericType == null)
      {
        throw new RuntimeException(String.format("no numeric type with id = %d", numericTypeId.intValue()));
      }
      NumericDescriptor numericDescriptor = new NumericDescriptor(numericType, numericDescriptorMap.get(numericTypeId).doubleValue());
      cropDisorderRecord.linkDescriptor(numericDescriptor);
      this.entityManager.persist(numericDescriptor);
    }
  }


  public void updateCategoricalDescriptors(Integer cropDisorderRecordId, Map<Integer, Set<String>> categoricalDescriptorMap)
  {
    CropDisorderRecord cropDisorderRecord = this.entityManager.find(CropDisorderRecord.class, cropDisorderRecordId);
    if (cropDisorderRecord == null)
    {
      throw new RuntimeException(String.format("no crop disorder record with id = %d", cropDisorderRecordId.intValue()));
    }
    Set<CategoricalDescriptor> oldCategoricalDescriptorSet = cropDisorderRecord.findCategoricalDescriptorSet();
    System.err.println(String.format("CropDisorderRecordManagerBean.updateCategoricalDescriptors: removing old categorical descriptors from cdr %d", cropDisorderRecord.getId().intValue()));
    for (CategoricalDescriptor oldCategoricalDescriptor : oldCategoricalDescriptorSet)
    {
      System.err.println(String.format("CropDisorderRecordManagerBean.updateCategoricalDescriptors: removing descriptor %s", oldCategoricalDescriptor.toString()));
      oldCategoricalDescriptor.unlink();
      this.entityManager.remove(oldCategoricalDescriptor);
      this.entityManager.flush();
    }
    System.err.println("CropDisorderRecordManagerBean.updateCategoricalDescriptors: done");
    Query categoricalTypeValueQuery = this.entityManager.createQuery("SELECT c FROM CategoricalTypeValue c WHERE valueType = :valueName AND c.categoricalType.typeName = :typeName");
    for (Integer categoricalTypeId : categoricalDescriptorMap.keySet())
    {
      CategoricalType categoricalType = (CategoricalType) this.entityManager.find(CategoricalType.class, categoricalTypeId);
      if (categoricalType == null)
      {
        throw new RuntimeException(String.format("no categorical type with id = %d", categoricalTypeId.intValue()));
      }
      // FIXME: no check for validity of value against type
      CategoricalDescriptor categoricalDescriptor = new CategoricalDescriptor(categoricalType);
      Set<String> categoricalTypeValueNameSet = categoricalDescriptorMap.get(categoricalTypeId);
      if (categoricalTypeValueNameSet.size() > 0)
      {
	if (!categoricalType.getMultivalue() && (categoricalTypeValueNameSet.size() != 1))
	{
	  throw new RuntimeException(String.format("categorical type %s does not allow multiple values", categoricalType.getTypeName()));
	}
	categoricalTypeValueQuery.setParameter("typeName", categoricalType.getTypeName());
	for (String categoricalTypeValueName : categoricalTypeValueNameSet)
	{
	  categoricalTypeValueQuery.setParameter("valueName", categoricalTypeValueName);
	  List<CategoricalTypeValue> categoricalTypeValueList = genericTypecast(categoricalTypeValueQuery.getResultList());
	  if (categoricalTypeValueList.size() == 1)
	  {
	    categoricalDescriptor.linkCategoricalTypeValue(categoricalTypeValueList.get(0));
	  }
	  else
	  {
	    throw new RuntimeException(String.format("invalid value \"%s\" specified for categorical type %s", categoricalTypeValueName, categoricalType.getTypeName()));
	    // System.err.println(String.format("CropDisorderRecordManagerBean.updateCategoricalDescriptors: \"%s\" is not a valid value for %s", categoricalTypeValueName, categoricalType.getTypeName()));
	  }
	}
	cropDisorderRecord.linkDescriptor(categoricalDescriptor);
	this.entityManager.persist(categoricalDescriptor);
      }
    }
  }


  public void update(CropDisorderRecord cropDisorderRecord, String cropScientificName, String expertDiagnosedCropDisorderScientificName)
  {
    if (cropDisorderRecord.getId() == null)
    {
      this.entityManager.persist(cropDisorderRecord);
    }
    else
    {
      CropDisorderRecord oldCdr = this.entityManager.find(CropDisorderRecord.class, cropDisorderRecord.getId());
      if (oldCdr == null)
      {
        throw new RuntimeException(String.format("No CDR with id %d ", cropDisorderRecord.getId().intValue()));
      }
      oldCdr.setDescription(cropDisorderRecord.getDescription());
      cropDisorderRecord = oldCdr;
    }
    cropDisorderRecord.setDiagnosis(null);
    if (cropScientificName != null)
    {
      cropDisorderRecord.unlinkCrop();
      if (cropScientificName.length() > 0)
      {
        // FIXME: a few line repeated from AccessBean.findCrop --
        // trouble is that crop instance found by AccessBean's entity
        // manager won't work with this bean's entity manager...
        Query query = this.entityManager.createQuery("SELECT c FROM Crop c WHERE scientificName = :s");
        query.setParameter("s", cropScientificName);
        Crop crop = (Crop) query.getSingleResult();
        if (crop == null)
        {
          throw new RuntimeException(String.format("no crop with scientific name \"%s\"", cropScientificName));
        }
        cropDisorderRecord.linkCrop(crop);
      }
    }
    this.entityManager.persist(cropDisorderRecord);
    if (expertDiagnosedCropDisorderScientificName != null)
    {
      System.err.println(String.format("updating expert diagnosis of crop disorder record %d to %s", cropDisorderRecord.getId().intValue(), expertDiagnosedCropDisorderScientificName));
      cropDisorderRecord.unlinkExpertDiagnosedCropDisorder();
      if (expertDiagnosedCropDisorderScientificName.length() > 0)
      {
        Query query = this.entityManager.createQuery("SELECT d FROM CropDisorder d WHERE scientificName = :s");
        query.setParameter("s", expertDiagnosedCropDisorderScientificName);
        CropDisorder cropDisorder = (CropDisorder) query.getSingleResult();
        if (cropDisorder == null)
        {
          throw new RuntimeException(String.format("no crop disorder with scientific name \"%s\"", expertDiagnosedCropDisorderScientificName));
        }
        cropDisorderRecord.linkExpertDiagnosedCropDisorder(cropDisorder);
      }
    }
  }
}
