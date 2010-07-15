package org.isacrodi.ejb.session;

import java.io.Serializable;

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


import static org.javamisc.Util.genericTypecast;


@Stateless
@Remote(CropDisorderRecordManager.class)
public class CropDisorderRecordManagerBean implements CropDisorderRecordManager, Serializable
{
  @PersistenceContext
  private EntityManager entityManager;

  @EJB
  private Access access;

  private static final long serialVersionUID = 1;


  private void fetchSets(CropDisorderRecord cdr)
  {
    for (Descriptor descriptor : cdr.getDescriptorSet())
    {
      descriptor.getId();
    }
    Diagnosis diagnosis = cdr.getDiagnosis();
    if (diagnosis != null)
    {
      for (DisorderScore disorderScore : diagnosis.getDisorderScoreSet())
      {
	disorderScore.getId();
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


  public DiagnosisProvider getDiagnosisProvider()
  {
    SVMDiagnosisProvider svmDiagnosisProvider = new SVMDiagnosisProvider();
    svmDiagnosisProvider.train(this.findExpertDiagnosedCropDisorderRecordList());
    return (svmDiagnosisProvider);
  }


  public void requestDiagnosis(int cropDisorderRecordId)
  {
    CropDisorderRecord cropDisorderRecord = this.entityManager.find(CropDisorderRecord.class, new Integer(cropDisorderRecordId));
    // FIXME: should check whether we got a cdr
    DiagnosisProvider diagnosisProvider = this.getDiagnosisProvider();
    Diagnosis diagnosis = diagnosisProvider.diagnose(cropDisorderRecord);
    this.entityManager.persist(diagnosis);
    for (DisorderScore disorderScore : diagnosis.getDisorderScoreSet())
    {
      this.entityManager.persist(disorderScore);
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
