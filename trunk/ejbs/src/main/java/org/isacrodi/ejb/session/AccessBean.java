package org.isacrodi.ejb.session;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.io.File;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.Remote;
import javax.ejb.EJB;
import javax.persistence.PersistenceContext;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.isacrodi.ejb.entity.*;

import static org.javamisc.Util.genericTypecast;


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


  public void insert(Procedure procedure, String[] incompatibleProcedureNameSet, String[] cropDisorderScientificNameSet)
  {
    System.err.printf("AccessBean.insert(Procedure): starting, procedure = %s\n", procedure.toString());
    this.entityManager.persist(procedure);
    System.err.println("persisted procedure");
    Query procedureQuery = this.entityManager.createQuery("SELECT p FROM Procedure p WHERE name = :s");
    for (String incompatibleProcedureName : incompatibleProcedureNameSet)
    {
      System.err.println(String.format("processing incompatible procedure \"%s\"\n", incompatibleProcedureName));
      procedureQuery.setParameter("s", incompatibleProcedureName);
      List<Procedure> procedureList = genericTypecast(procedureQuery.getResultList());
      if (procedureList.size() == 0)
      {
	System.err.println(String.format("AccessBean.insert(Procedure): ignoring unknown procedure \"%s\"", incompatibleProcedureName));
      }
      else
      {
	System.err.println(String.format("AccessBean.insert(Procedure): found existing procedure \"%s\"", incompatibleProcedureName));
	Procedure incompatibleProcedure = procedureList.get(0);
	procedure.linkIncompatibleProcedure(incompatibleProcedure);
      }
    }
    System.err.println("processing incompatible procedures finished");
    Query cropDisorderQuery = this.entityManager.createQuery("SELECT c FROM CropDisorder c WHERE scientificName = :s");
    for (String cropDisorderName : cropDisorderScientificNameSet)
    {
      System.err.println(String.format("processing crop disorder \"%s\"\n", cropDisorderName));
      cropDisorderQuery.setParameter("s", cropDisorderName);
      List<CropDisorder> cropDisorderList = genericTypecast(cropDisorderQuery.getResultList());
      if (cropDisorderList.size() == 0)
      {
	System.err.println(String.format("AccessBean.insert(Procedure): ignoring unknown crop disorder \"%s\"", cropDisorderName));
      }
      else
      {
	CropDisorder cropDisorder = cropDisorderList.get(0);
	procedure.linkCropDisorder(cropDisorder);
      }
    }
    System.err.println("AccessBean.insert(Procedure): finished");
  }


  public void insert(CategoricalType categoricalType, String[] valueString)
  {
    // FIXME: should check for duplicates
    this.entityManager.persist(categoricalType);
    for (String v : valueString)
    {
      CategoricalTypeValue categoricalTypeValue = new CategoricalTypeValue(v);
      categoricalTypeValue.linkCategoricalType(categoricalType);
      this.entityManager.persist(categoricalTypeValue);
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


  public Integer insert(String username, String cropScientificName, Set<Descriptor> descriptorSet, String expertDiagnosisName)
  {
    // FIXME: need to fix up related entities...
    CropDisorderRecord cropDisorderRecord = new CropDisorderRecord();
    IsacrodiUser isacrodiUser = this.userHandler.findUser(username);
    this.entityManager.merge(isacrodiUser);
    System.err.println(String.format("searching for crop with scientific name \"%s\"", cropScientificName));
    Query query = this.entityManager.createQuery("SELECT c FROM Crop c WHERE scientificName = :s");
    query.setParameter("s", cropScientificName);
    List<Crop> cropList = genericTypecast(query.getResultList());
    if (cropList.size() == 0)
    {
      throw new RuntimeException(String.format("crop \"%s\" not found", cropScientificName));
    }
    Crop crop = cropList.get(0);
    cropDisorderRecord.setIsacrodiUser(isacrodiUser);
    cropDisorderRecord.setCrop(crop);
    this.entityManager.persist(cropDisorderRecord);
    for (Descriptor descriptor : descriptorSet)
    {
      cropDisorderRecord.linkDescriptor(descriptor);
      this.entityManager.persist(descriptor);
      this.entityManager.merge(descriptor.getDescriptorType());
    }
    if (expertDiagnosisName != null)
    {
      Query expertQuery = this.entityManager.createQuery("SELECT d FROM CropDisorder d WHERE scientificName = :s");
      expertQuery.setParameter("s", expertDiagnosisName);
      List<CropDisorder> cropDisorderList = genericTypecast(expertQuery.getResultList());
      if (cropDisorderList.size() == 0)
      {
	throw new RuntimeException(String.format("no crop disorder \"%s\" (specified as expert diagnosis)", expertDiagnosisName));
      }
      CropDisorder expertDiagnosis = cropDisorderList.get(0);
      cropDisorderRecord.linkExpertDiagnosedCropDisorder(expertDiagnosis);
    }
    return (cropDisorderRecord.getId());
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


  public List<Crop> findCropList()
  {
    Query query = this.entityManager.createQuery("SELECT c FROM Crop c");
    List<Crop> cropList = genericTypecast(query.getResultList());
    return (cropList);
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


  public CategoricalType findCategoricalType(Integer id)
  {
    return (this.entityManager.find(CategoricalType.class, id));
  }


  public CategoricalType findCategoricalType(String typename)
  {
    Query query = this.entityManager.createQuery("SELECT c FROM CategoricalType c WHERE typename = :s");
    query.setParameter("s", typename);
    List<CategoricalType> categoricalTypeList = genericTypecast(query.getResultList());
    if (categoricalTypeList.size() == 1)
    {
      return (categoricalTypeList.get(0));
    }
    else
    {
      return (null);
    }
  }


  public CategoricalTypeValue findCategoricalTypeValue(String categoricalTypeName, String categoricalTypeValueName)
  {
    Query query = this.entityManager.createQuery("SELECT c FROM CategoricalTypeValue c WHERE valueType = :s");
    query.setParameter("s", categoricalTypeValueName);
    List<CategoricalTypeValue> categoricalTypeValueList = genericTypecast(query.getResultList());
    if (categoricalTypeValueList.size() > 0)
    {
      CategoricalTypeValue categoricalTypeValue = null;
      // FIXME: terrible hack to linearly search list...
      for (CategoricalTypeValue c : categoricalTypeValueList)
      {
	if (c.getCategoricalType().getTypeName().equals(categoricalTypeName))
	{
	  categoricalTypeValue = c;
	  break;
	}
      }
      HashSet<CategoricalDescriptor> h = new HashSet<CategoricalDescriptor>();
      for (CategoricalDescriptor categoricalDescriptor : categoricalTypeValue.getCategoricalDescriptorSet())
      {
	h.add(categoricalDescriptor);
      }
      categoricalTypeValue.setCategoricalDescriptorSet(h);
      return (categoricalTypeValue);
    }
    else
    {
      return (null);
    }
  }


  public List<CategoricalTypeValue> findCategoricalTypeValueList(String typeName)
  {
    Query query = this.entityManager.createQuery("SELECT c FROM CategoricalTypeValue c WHERE c.categoricalType.typeName = :typeName");
    query.setParameter("typeName", typeName);
    System.err.println(String.format("AccessBean.findCategoricalTypeValueList: query = %s", query.toString()));
    List<Object> objectList = null;
    System.err.println("got the list");
    try
    {
      objectList = query.getResultList();
      for (Object o : objectList)
      {
	System.err.println(String.format("AccessBean.findCategoricalTypeValueList: object %s", o.toString()));
      }
    }
    catch (Exception e)
    {
      System.err.println(e.toString());
      e.printStackTrace();
    }
    List<CategoricalTypeValue> categoricalTypeValueList = genericTypecast(objectList);
    System.err.println(String.format("AccessBean.findCategoricalTypeValueList: found %d values for %s", categoricalTypeValueList.size(), typeName));
    for (CategoricalTypeValue categoricalTypeValue : categoricalTypeValueList)
    {
      if (categoricalTypeValue == null)
      {
	System.err.println("AccessBean.findCategoricalTypeValueList: found null reference");
      }
      else
      {
	System.err.println(String.format("AccessBean.findCategoricalTypeValueList: found value %s", categoricalTypeValue.getValueType()));
      }
    }
    return (categoricalTypeValueList);
  }


  public NumericType findNumericType(Integer id)
  {
    return (this.entityManager.find(NumericType.class, id));
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


  public Procedure findProcedure(Integer id)
  {
    return (this.entityManager.find(Procedure.class, id));
  }


  public Procedure findProcedure(String procedureName)
  {
    Query query = this.entityManager.createQuery("SELECT p FROM Procedure p where name = :s");
    query.setParameter("s", procedureName);
    List<Procedure> procedureList = genericTypecast(query.getResultList());
    if (procedureList.size() == 1)
    {
      return (procedureList.get(0));
    }
    else
    {
      return (null);
    }
  }


  public void dumpEntities(String basename) throws IOException
  {
    System.err.println(String.format("AccessBean.dumpEntities: basename = \"%s\"", basename));
    Query cropQuery = this.entityManager.createQuery("SELECT c FROM Crop c");
    List<Crop> cropList = genericTypecast(cropQuery.getResultList());
    String cropfileName = String.format("%s_crops.txt", basename);
    PrintWriter w = new PrintWriter(new File(cropfileName));
    w.println("isacrodi-crop-0.1");
    for (Crop crop : cropList)
    {
      w.println(crop.fileRepresentation());
    }
    w.close();
    Query cropDisorderQuery = this.entityManager.createQuery("SELECT c FROM CropDisorder c");
    List<CropDisorder> cropDisorderList = genericTypecast(cropDisorderQuery.getResultList());
    String cropDisorderfileName = String.format("%s_disorders.txt", basename);
    w = new PrintWriter(new File(cropDisorderfileName));
    w.println("isacrodi-disorders-0.1");
    for (CropDisorder cropDisorder : cropDisorderList)
    {
      w.println(cropDisorder.fileRepresentation());
    }
    w.close();
    Query numericTypeQuery = this.entityManager.createQuery("SELECT c FROM NumericType c");
    List<NumericType> numericTypeList = genericTypecast(numericTypeQuery.getResultList());
    String numericTypefileName = String.format("%s_numerictypes.txt", basename);
    w = new PrintWriter(new File(numericTypefileName));
    w.println("isacrodi-numerictypes-0.1");
    for (NumericType numericType : numericTypeList)
    {
      w.println(numericType.fileRepresentation());
    }
    w.close();
    Query categoricalTypeQuery = this.entityManager.createQuery("SELECT c FROM CategoricalType c");
    List<CategoricalType> categoricalTypeList = genericTypecast(categoricalTypeQuery.getResultList());
    String categoricalTypefileName = String.format("%s_categoricaltypes.txt", basename);
    w = new PrintWriter(new File(categoricalTypefileName));
    w.println("isacrodi-categoricaltypes-0.1");
    for (CategoricalType categoricalType : categoricalTypeList)
    {
      w.println(categoricalType.fileRepresentation());
    }
    w.close();
    Query imageTypeQuery = this.entityManager.createQuery("SELECT c FROM ImageType c");
    List<ImageType> imageTypeList = genericTypecast(imageTypeQuery.getResultList());
    String imageTypefileName = String.format("%s_imagetypes.txt", basename);
    w = new PrintWriter(new File(imageTypefileName));
    w.println("isacrodi-imagetypes-0.1");
    for (ImageType imageType : imageTypeList)
    {
      w.println(imageType.fileRepresentation());
    }
    w.close();
    Query cropDisorderRecordQuery = this.entityManager.createQuery("SELECT c FROM CropDisorderRecord c");
    List<CropDisorderRecord> cropDisorderRecordList = genericTypecast(cropDisorderRecordQuery.getResultList());
    String cropDisorderRecordfileName = String.format("%s_cdrs", basename);
    w = new PrintWriter(new File(cropDisorderRecordfileName));
    w.println("isacrodi-cropDisorderRecord-0.1");
    for (CropDisorderRecord cropDisorderRecord : cropDisorderRecordList)
    {
      w.println(cropDisorderRecord.fileRepresentation());
      for (ImageDescriptor imageDescriptor : cropDisorderRecord.findImageDescriptorSet())
      {
	String imageFileName = String.format("%s_%s", basename, imageDescriptor.makeFileName());
	FileOutputStream imageOutputStream = new FileOutputStream(new File(imageFileName));
	imageOutputStream.write(imageDescriptor.getImageData());
	imageOutputStream.close();
      }
    }
    w.close();
  }
}
