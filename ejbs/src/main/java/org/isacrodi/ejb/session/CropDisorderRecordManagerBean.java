package org.isacrodi.ejb.session;

import java.io.Serializable;

import java.util.List;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.isacrodi.ejb.entity.*;

import org.isacrodi.diagnosis.SVMDiagnosisProvider;


import static org.javamisc.Util.genericTypecast;


@Stateless
@Remote(CropDisorderRecordManager.class)
public class CropDisorderRecordManagerBean implements CropDisorderRecordManager, Serializable
{
  @PersistenceContext
  private EntityManager entityManager;

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


  public void requestDiagnosis(int cropDisorderRecordId)
  {
    CropDisorderRecord cropDisorderRecord = this.entityManager.find(CropDisorderRecord.class, new Integer(cropDisorderRecordId));
    // FIXME: should check whether we got a cdr
    SVMDiagnosisProvider svmDiagnosisProvider = new SVMDiagnosisProvider();
    svmDiagnosisProvider.train(this.findExpertDiagnosedCropDisorderRecordList());
    Diagnosis diagnosis = svmDiagnosisProvider.diagnose(cropDisorderRecord);
    this.entityManager.persist(diagnosis);
    for (DisorderScore disorderScore : diagnosis.getDisorderScoreSet())
    {
      this.entityManager.persist(disorderScore);
    }
  }
}
