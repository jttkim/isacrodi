package org.isacrodi.ejb.session;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.Remote;
import javax.ejb.EJB;
import javax.persistence.PersistenceContext;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.isacrodi.ejb.entity.*;


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


  public NumericType findNumericType(String typename)
  {
    Query query = this.entityManager.createQuery("SELECT n FROM NumericType n WHERE typename = :s");
    query.setParameter("s", typename);
    NumericType numericType = (NumericType) query.getSingleResult();
    return (numericType);
  }
}
