package org.isacrodi.ejb.session;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.Remote;
import javax.ejb.EJB;
import javax.persistence.PersistenceContext;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.isacrodi.ejb.entity.*;
import static org.isacrodi.util.Util.genericTypecast;


@Stateless
@Remote({Access.class})
public class AccessBean implements Access
{
  @PersistenceContext
  private EntityManager entityManager;

  @EJB
  private UserHandler userHandler;


  public AccessBean()
  {
    super();
  }


  public void insert(Crop crop)
  {
    // FIXME: should check for duplicates
    this.entityManager.persist(crop);
  }


  public void insert(CropDisorder cropDisorder, String[] cropScientificNameSet)
  {
    for (String cropScientificName : cropScientificNameSet)
    {
      Query query = this.entityManager.createQuery("SELECT c FROM Crop c WHERE scientificName = :s");
      query.setParameter("s", cropScientificName);
      // will throw a NoResultException if crop not found, so we can plough ahead
      Crop crop = (Crop) query.getSingleResult();
      cropDisorder.addCrop(crop);
    }
    // FIXME: no check for duplication
    this.entityManager.persist(cropDisorder);
  }


  public void insert(NumericType numericType)
  {
    // FIXME: should check for duplicates
    this.entityManager.persist(numericType);
  }


  public void insert(CropDisorderRecord cropDisorderRecord, String username, String cropScientificName)
  {
    // FIXME: need to fix up related entities...
    IsacrodiUser isacrodiUser = this.userHandler.findUser(username);
    Query query = this.entityManager.createQuery("SELECT c FROM Crop c WHERE scientificName = :s");
    query.setParameter("s", cropScientificName);
    Crop crop = (Crop) query.getSingleResult();
    cropDisorderRecord.setIsacrodiUser(isacrodiUser);
    cropDisorderRecord.setCrop(crop);
    this.entityManager.persist(cropDisorderRecord);
    for (Descriptor d : cropDisorderRecord.getDescriptorSet())
    {
      this.entityManager.persist(d);
    }
  }


  public Crop findCrop(String scientificName)
  {
    Query query = this.entityManager.createQuery("SELECT c FROM Crop c WHERE scientificName = :s");
    query.setParameter("s", scientificName);
    List<Crop> cropList = genericTypecast(query.getResultList());
    if (cropList.size() == 1)
    {
      return (cropList.get(0));
    }
    else
    {
      return (null);
    }
  }


  public CropDisorder findCropDisorder(String scientificName)
  {
    Query query = this.entityManager.createQuery("SELECT c FROM CropDisorder c WHERE scientificName = :s");
    query.setParameter("s", scientificName);
    List<CropDisorder> cropDisorderList = genericTypecast(query.getResultList());
    if (cropDisorderList.size() == 1)
    {
      return (cropDisorderList.get(0));
    }
    else
    {
      return (null);
    }
  }


  public NumericType findNumericType(String typename)
  {
    Query query = this.entityManager.createQuery("SELECT n FROM NumericType n WHERE typename = :s");
    query.setParameter("s", typename);
    List<NumericType> numericTypeList = genericTypecast(query.getResultList());
    if (numericTypeList.size() == 1)
    {
      return (numericTypeList.get(0));
    }
    else
    {
      return (null);
    }
  }
}
