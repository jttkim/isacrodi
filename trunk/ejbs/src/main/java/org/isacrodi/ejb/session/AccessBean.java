package org.isacrodi.ejb.session;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.Remote;
import javax.ejb.EJB;
import javax.persistence.PersistenceContext;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.isacrodi.ejb.entity.*;

import org.isacrodi.util.Util;
import static org.isacrodi.util.Util.genericTypecast;


@Stateless
@Remote({Access.class})
public class AccessBean implements Access
{
  @PersistenceContext
  private EntityManager entityManager;

  @EJB
  private UserHandler userHandler;


  private static boolean isSetAccessor(Method method)
  {
    String methodName = method.getName();
    if (!methodName.matches("get[A-Z].*Set"))
    {
      return (false);
    }
    Class<?>[] parameterTypes = method.getParameterTypes();
    if (parameterTypes.length != 0)
    {
      return (false);
    }
    else
    {
      return (true);
    }
  }


  private void fetchSets(Object entity)
  {
    Class<?> entityClass = entity.getClass();
    for (Method method : entityClass.getMethods())
    {
      if (isSetAccessor(method))
      {
	try
	{
	  Object setObject = method.invoke(entity);
	  Set<?> set = genericTypecast(setObject);
	  for (Object o : set)
	  {
	    o.toString();
	  }
	}
	catch (IllegalAccessException e)
	{
	  System.err.println(String.format("AccessBean.fetchSets: caught %s\n", e.toString()));
	}
	catch (InvocationTargetException e)
	{
	  System.err.println(String.format("AccessBean.fetchSets: caught %s\n", e.toString()));
	}
      }
    }
  }


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
    // FIXME: no check for duplication
    this.entityManager.persist(cropDisorder);
    for (String cropScientificName : cropScientificNameSet)
    {
      Query query = this.entityManager.createQuery("SELECT c FROM Crop c WHERE scientificName = :s");
      query.setParameter("s", cropScientificName);
      // will throw a NoResultException if crop not found, so we can plough ahead
      Crop crop = (Crop) query.getSingleResult();
      cropDisorder.addCrop(crop);
    }
  }


  public void insert(NumericType numericType)
  {
    // FIXME: should check for duplicates
    this.entityManager.persist(numericType);
  }


  public void insert(ImageType imageType)
  {
    // FIXME: should check for duplicates
    this.entityManager.persist(imageType);
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
    for (Descriptor descriptor : cropDisorderRecord.getDescriptorSet())
    {
      this.entityManager.persist(descriptor);
    }
  }


  public <EntityClass> EntityClass findEntity(Class<EntityClass> entityClass, Integer id)
  {
    EntityClass entity = this.entityManager.find(entityClass, id);
    if (entity != null)
    {
      fetchSets(entity);
    }
    return (entity);
  }


  public List<?> findEntityList(Class<?> entityClass)
  {
    // FIXME: hack!!!!! string assembly of query!!!
    // using a class' simple name provides some amount of sanity hopefully, but this is not good
    Query query = this.entityManager.createQuery(String.format("SELECT e FROM %s e", entityClass.getSimpleName()));
    List<?> entityList = genericTypecast(query.getResultList());
    for (Object entity : entityList)
    {
      fetchSets(entity);
    }
    return (entityList);
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


  public ImageType findImageType(String typename)
  {
    Query query = this.entityManager.createQuery("SELECT n FROM ImageType n WHERE typename = :s");
    query.setParameter("s", typename);
    List<ImageType> imageTypeList = genericTypecast(query.getResultList());
    if (imageTypeList.size() == 1)
    {
      return (imageTypeList.get(0));
    }
    else
    {
      return (null);
    }
  }


  public void updateEntity(Class<?> entityClass, Integer entityId, Map<String, String[]> propertyMap) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException
  {
    // FIXME: propertyMap may need some sanitising, especially it must not contain an id entry (!!)
    Object entity = Util.constructDefaultInstance(entityClass);
    if (entityId != null)
    {
      entity = this.entityManager.find(entity.getClass(), entityId);
    }
    for (String propertyName : propertyMap.keySet())
    {
      System.err.println(String.format("setting property %s to value \"%s\"", propertyName, propertyMap.get(propertyName)));
      Class<?> propertyType = Util.findPropertyType(entity.getClass(), propertyName);
      if (String.class.isAssignableFrom(propertyType))
      {
	String propertyValue = propertyMap.get(propertyName)[0];
	Util.setProperty(entity, propertyName, propertyValue);
      }
      else if (Integer.class.isAssignableFrom(propertyType))
      {
	Integer propertyValue = new Integer(Integer.parseInt(propertyMap.get(propertyName)[0]));
	Util.setProperty(entity, propertyName, propertyValue);
      }
      else if (Double.class.isAssignableFrom(propertyType))
      {
	Double propertyValue = new Double(Double.parseDouble(propertyMap.get(propertyName)[0]));
	Util.setProperty(entity, propertyName, propertyValue);
      }
      else if (Collection.class.isAssignableFrom(propertyType))
      {
	// FIXME: commands are passed through from the HTML form, without any sanitation.
	// This needs proper design with some form of command descriptors, that are set up by the client (struts action)
	String[] opAndId = propertyMap.get(propertyName);
	String op = opAndId[0];
	String id = opAndId[1];
	if (!"add".equals(op) && !"remove".equals(op))
	{
	  op = opAndId[1];
	  id = opAndId[0];
	}
	System.err.println(String.format("Access.updateEntity: op = %s, id = %s", op, id));
	Integer associatedEntityId = new Integer(Integer.parseInt(id));
	// FIXME: clumsy and perhaps not so safe way to get at associated entity type
	Class<?> associatedEntityType = Util.findAssociationPropertyMap(entity).get(propertyName);
	System.err.println(String.format("associated entity type for %s: %s, property type: %s", propertyName, associatedEntityType.toString(), propertyType.toString()));
	Object associatedEntity = this.entityManager.find(associatedEntityType, associatedEntityId);
	if ("add".equals(op))
	{
	  if (associatedEntity != null)
	  {
	    // FIXME: no bidirectional setup of association -- need to get at mappedBy element...
	    Set associationSet = genericTypecast(Util.getProperty(entity, propertyName));
	    System.err.println(String.format("Access.updateEntity: before add: %d associated entities", associationSet.size()));
	    associationSet.add(associatedEntity);
	    System.err.println(String.format("Access.updateEntity: after add: %d associated entities", associationSet.size()));
	    this.entityManager.persist(entity);
	  }
	  else
	  {
	    System.err.println(String.format("Access.updateEntity: no entity of type %s with id %d", associatedEntityType.toString(), associatedEntityId.intValue()));
	  }
	}
	else if ("remove".equals(op))
	{
	  if (associatedEntity != null)
	  {
	    // FIXME: no bidirectional setup of association -- need to get at mappedBy element...
	    Set<?> associationSet = genericTypecast(Util.getProperty(entity, propertyName));
	    System.err.println(String.format("Access.updateEntity: before remove: %d associated entities", associationSet.size()));
	    associationSet.remove(associatedEntity);
	    System.err.println(String.format("Access.updateEntity: after remove: %d associated entities", associationSet.size()));
	    this.entityManager.persist(entity);
	  }
	  else
	  {
	    System.err.println(String.format("Access.updateEntity: no entity of type %s with id %d", associatedEntityType.toString(), associatedEntityId.intValue()));
	  }
	}
	else
	{
	  System.err.println(String.format("Access.updateEntity: unknown op \"%s\"", op));
	}
      }
      else if (Util.isEntityClass(propertyType))
      {
	Integer associatedEntityId = new Integer(Integer.parseInt(propertyMap.get(propertyName)[0]));
	Object associatedEntity = this.entityManager.find(propertyType, associatedEntityId);
	{
	  if (associatedEntity != null)
	  {
	    // FIXME: association not set up bidirectionally
	    Util.setProperty(entity, propertyName, associatedEntity);
	  }
	  else
	  {
	    System.err.println(String.format("Access.updateEntity: no entity of type %s with id %d", propertyType.toString(), associatedEntityId.intValue()));
	  }
	}
      }
      else
      {
	System.err.println(String.format("Access.updateEntity: no support for type %s", propertyType.toString()));
      }
    }
    if (entityId == null)
    {
      this.entityManager.persist(entity);
    }
  }


  public void removeEntity(Class<?> entityClass, Integer id)
  {
    Object entity = this.entityManager.find(entityClass, id);
    if (!(entity instanceof IsacrodiEntity))
    {
      throw new RuntimeException("not an Isacrodi entity");
    }
    IsacrodiEntity isacrodiEntity = (IsacrodiEntity) entity;
    isacrodiEntity.unlink();
    this.entityManager.remove(entity);
  }
}
