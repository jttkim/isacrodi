package org.isacrodi.ejb.io;

import java.io.IOException;
import java.io.PrintStream;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

import org.javamisc.Util;
import org.javamisc.csv.CsvReader;
import org.javamisc.csv.CsvTable;

import org.isacrodi.ejb.session.UserHandler;
import org.isacrodi.ejb.session.Access;
import org.isacrodi.ejb.session.CropDisorderRecordManager;
import org.isacrodi.ejb.session.Kludge;

import org.isacrodi.ejb.io.Import;

import org.isacrodi.ejb.entity.*;


/**
 * Class for loading an Isacrodi dataset into memory.
 *
 * <p>This class supports loading datasets from files in a
 * noninteractive, reproducible way. It is, however, not an ACID
 * database. Specifically, after any exception, consistency is not
 * guaranteed -- so it's best to let exceptions bomb out
 * completely.</p>
 *
 * <p><strong>Important note:</strong> All instances added to the
 * database (e.g. by the <code>insert</code> methods) and retrieved
 * from the database <em>belong to the database</em>. This is to say
 * that changes that clients make to instances may affect the in
 * memory database. This is to emulate persisting and transactions as
 * provided by a session bean, to an extent that is reasonably
 * possible.</p>
 */
public class MemoryDB implements Access, UserHandler
{
  int nextId;
  private Map<Integer, Crop> cropMap;
  private Map<Integer, CropDisorder> cropDisorderMap;
  private Map<Integer, CategoricalTypeValue> categoricalTypeValueMap;
  private Map<Integer, DescriptorType> descriptorTypeMap;
  private Map<Integer, Descriptor> descriptorMap;
  private Map<Integer, CropDisorderRecord> cropDisorderRecordMap;
  private Map<Integer, Diagnosis> diagnosisMap;
  private Map<Integer, DisorderScore> disorderScoreMap;
  private Map<Integer, IsacrodiUser> isacrodiUserMap;
  private Map<Integer, Procedure> procedureMap;
  private Map<Integer, Recommendation> recommendationMap;


  public MemoryDB()
  {
    super();
    this.nextId = 1;
    this.cropMap = new HashMap<Integer, Crop>();
    this.cropDisorderMap = new HashMap<Integer, CropDisorder>();
    this.categoricalTypeValueMap = new HashMap<Integer, CategoricalTypeValue>();
    this.descriptorTypeMap = new HashMap<Integer, DescriptorType>();
    this.descriptorMap = new HashMap<Integer, Descriptor>();
    this.cropDisorderRecordMap = new HashMap<Integer, CropDisorderRecord>();
    this.diagnosisMap = new HashMap<Integer, Diagnosis>();
    this.disorderScoreMap = new HashMap<Integer, DisorderScore>();
    this.isacrodiUserMap = new HashMap<Integer, IsacrodiUser>();
    this.procedureMap = new HashMap<Integer, Procedure>();
    this.recommendationMap = new HashMap<Integer, Recommendation>();
  }


  public void printSummary(PrintStream out)
  {
    out.println(String.format("%5d crops", this.cropMap.size()));
    out.println(String.format("%5d crop disorders", this.cropDisorderMap.size()));
    out.println(String.format("%5d categorical type values", this.categoricalTypeValueMap.size()));
    out.println(String.format("%5d descriptor types", this.descriptorTypeMap.size()));
    out.println(String.format("%5d descriptors", this.descriptorMap.size()));
    out.println(String.format("%5d crop disorder records", this.cropDisorderRecordMap.size()));
    out.println(String.format("%5d diagnoses", this.diagnosisMap.size()));
    out.println(String.format("%5d disorder scores", this.disorderScoreMap.size()));
    out.println(String.format("%5d users", this.isacrodiUserMap.size()));
    out.println(String.format("%5d procedures", this.procedureMap.size()));
    out.println(String.format("%5d recommendations", this.recommendationMap.size()));
  }


  private Map<String, Crop> makeScientificNameCropMap()
  {
    Map<String, Crop> scientificNameCropMap = new HashMap<String, Crop>();
    for (Crop crop : this.cropMap.values())
    {
      scientificNameCropMap.put(crop.getScientificName(), crop);
    }
    return (scientificNameCropMap);
  }


  private Map<String, CropDisorder> makeScientificNameCropDisorderMap()
  {
    Map<String, CropDisorder> scientificNameCropDisorderMap = new HashMap<String, CropDisorder>();
    for (CropDisorder cropDisorder : this.cropDisorderMap.values())
    {
      scientificNameCropDisorderMap.put(cropDisorder.getScientificName(), cropDisorder);
    }
    return (scientificNameCropDisorderMap);
  }


  private Map<String, Procedure> makeNameProcedureMap()
  {
    Map<String, Procedure> nameProcedureMap = new HashMap<String, Procedure>();
    for (Procedure procedure : this.procedureMap.values())
    {
      nameProcedureMap.put(procedure.getName(), procedure);
    }
    return (nameProcedureMap);
  }


  private Map<String, DescriptorType> makeTypeNameDescriptorTypeMap()
  {
    Map<String, DescriptorType> typeNameDescriptorTypeMap = new HashMap<String, DescriptorType>();
    for (DescriptorType descriptorType : this.descriptorTypeMap.values())
    {
      typeNameDescriptorTypeMap.put(descriptorType.getTypeName(), descriptorType);
    }
    return (typeNameDescriptorTypeMap);
  }


  private Map<String, IsacrodiUser> makeUsernameIsacrodiUserMap()
  {
    Map<String, IsacrodiUser> usernameIsacrodiUserMap = new HashMap<String, IsacrodiUser>();
    for (IsacrodiUser isacrodiUser : this.isacrodiUserMap.values())
    {
      usernameIsacrodiUserMap.put(isacrodiUser.getUsername(), isacrodiUser);
    }
    return (usernameIsacrodiUserMap);
  }


  private Set<Crop> findCropSet(String[] cropScientificNameSet)
  {
    Map<String, Crop> scientificNameCropMap = this.makeScientificNameCropMap();
    Set<Crop> cropSet = new HashSet<Crop>();
    for (String scientificName : cropScientificNameSet)
    {
      Crop crop = scientificNameCropMap.get(scientificName);
      if (crop == null)
      {
	throw new RuntimeException(String.format("no crop \"%s\"", scientificName));
      }
      cropSet.add(crop);
    }
    return (cropSet);
  }


  /**
   * Find a seet of crop disorders based on their scientific names.
   *
   * <p>Quirks compatibility with {@code AccessBean}: unknonw crop
   * disorders are silently ignored.</p>
   */
  private Set<CropDisorder> findCropDisorderSet(String[] cropDisorderScientificNameSet)
  {
    Map<String, CropDisorder> scientificNameCropDisorderMap = this.makeScientificNameCropDisorderMap();
    Set<CropDisorder> cropDisorderSet = new HashSet<CropDisorder>();
    for (String scientificName : cropDisorderScientificNameSet)
    {
      CropDisorder cropDisorder = scientificNameCropDisorderMap.get(scientificName);
      if (cropDisorder != null)
      {
	cropDisorderSet.add(cropDisorder);
      }
      else
      {
	// System.err.println(String.format("MemoryDB.findCropDisorderSet: ignoring unknown crop disorder \"%s\"", scientificName));
      }
    }
    return (cropDisorderSet);
  }


  /**
   * Find a set of procedures based on their names.
   *
   * <p>Quirks compatibility: Unknown procedures are silently ignored,
   * this matches the way that {@link
   * org.isacrodi.ejb.session.access.insert(Procedure)} operates
   * (working around forward references when reading from a file).</p>
   */
  private Set<Procedure> findProcedureSet(String[] procedureNameSet)
  {
    Map<String, Procedure> nameProcedureMap = this.makeNameProcedureMap();
    Set<Procedure> procedureSet = new HashSet<Procedure>();
    for (String name : procedureNameSet)
    {
      Procedure procedure = nameProcedureMap.get(name);
      if (procedure != null)
      {
	procedureSet.add(procedure);
      }
      else
      {
	// System.err.println(String.format("MemoryDB.findProcedureSet: ignoring unknown procedure \"%s\"", name));
      }
    }
    return (procedureSet);
  }


  public void insert(Crop crop)
  {
    crop.setId(new Integer(this.nextId++));
    this.cropMap.put(crop.getId(), crop);
  }


  public void insert(CropDisorder cropDisorder, String[] cropScientificNameSet)
  {
    Set<Crop> cropSet = this.findCropSet(cropScientificNameSet);
    cropDisorder.setCropSet(cropSet);
    cropDisorder.setId(new Integer(this.nextId++));
    this.cropDisorderMap.put(cropDisorder.getId(), cropDisorder);
  }


  public void insert(Procedure procedure, String[] incompatibleProcedureNameSet, String[] cropDisorderScientificNameSet)
  {
    // System.err.println(String.format("importing procedure %s", procedure.getName()));
    Set<Procedure> incompatibleProcedureSet = this.findProcedureSet(incompatibleProcedureNameSet);
    Set<CropDisorder> cropDisorderSet = this.findCropDisorderSet(cropDisorderScientificNameSet);
    procedure.setId(new Integer(this.nextId++));
    procedure.setIncompatibleProcedureSet(incompatibleProcedureSet);
    procedure.setCropDisorderSet(cropDisorderSet);
    this.procedureMap.put(procedure.getId(), procedure);
  }


  public void insert(CategoricalType categoricalType, String[] valueString)
  {
    categoricalType.setId(this.nextId++);
    this.descriptorTypeMap.put(categoricalType.getId(), categoricalType);
    for (String v : valueString)
    {
      CategoricalTypeValue categoricalTypeValue = new CategoricalTypeValue(new Integer(this.nextId++), categoricalType, v);
      categoricalTypeValue.linkCategoricalType(categoricalType);
    }
  }


  public void insert(NumericType numericType)
  {
    numericType.setId(this.nextId++);
    this.descriptorTypeMap.put(numericType.getId(), numericType);
  }


  public void insert(ImageType imageType)
  {
    imageType.setId(this.nextId++);
    this.descriptorTypeMap.put(imageType.getId(), imageType);
  }


  public Integer insert(String username, String cropScientificName, Set<Descriptor> descriptorSet, String expertDiagnosisName)
  {
    IsacrodiUser isacrodiUser = this.findUser(username);
    Crop crop = this.findCrop(cropScientificName);
    CropDisorderRecord cropDisorderRecord = new CropDisorderRecord();
    cropDisorderRecord.setId(new Integer(this.nextId++));
    cropDisorderRecord.linkIsacrodiUser(isacrodiUser);
    cropDisorderRecord.linkCrop(crop);
    for (Descriptor descriptor : descriptorSet)
    {
      descriptor.linkCropDisorderRecord(cropDisorderRecord);
    }
    if (expertDiagnosisName != null)
    {
      cropDisorderRecord.linkExpertDiagnosedCropDisorder(this.findCropDisorder(expertDiagnosisName));
    }
    this.cropDisorderRecordMap.put(cropDisorderRecord.getId(), cropDisorderRecord);
    return (cropDisorderRecord.getId());
  }


  public Crop findCrop(String scientificName)
  {
    Map<String, Crop> scientificNameCropMap = this.makeScientificNameCropMap();
    return (scientificNameCropMap.get(scientificName));
  }


  public List<Crop> findCropList()
  {
    return (new ArrayList<Crop>(this.cropMap.values()));
  }


  public CropDisorder findCropDisorder(String scientificName)
  {
    Map<String, CropDisorder> scientificNameCropDisorderMap = this.makeScientificNameCropDisorderMap();
    return (scientificNameCropDisorderMap.get(scientificName));
  }


  public List<CropDisorder> findCropDisorderList()
  {
    return (new ArrayList<CropDisorder>(this.cropDisorderMap.values()));
  }


  public List<DescriptorType> findDescriptorTypeList()
  {
    return (new ArrayList<DescriptorType>(this.descriptorTypeMap.values()));
  }


  public List<CategoricalType> findCategoricalTypeList()
  {
    List<CategoricalType> categoricalTypeList = new ArrayList<CategoricalType>();
    for (DescriptorType descriptorType : this.descriptorTypeMap.values())
    {
      if (descriptorType instanceof CategoricalType)
      {
	categoricalTypeList.add((CategoricalType) descriptorType);
      }
    }
    return (categoricalTypeList);
  }


  public CategoricalType findCategoricalType(Integer id)
  {
    return ((CategoricalType) this.descriptorTypeMap.get(id));
  }


  public CategoricalType findCategoricalType(String typename)
  {
    Map<String, DescriptorType> typeNameDescriptorTypeMap = this.makeTypeNameDescriptorTypeMap();
    return ((CategoricalType) typeNameDescriptorTypeMap.get(typename));
  }


  public List<NumericType> findNumericTypeList()
  {
    List<NumericType> numericTypeList = new ArrayList<NumericType>();
    for (DescriptorType descriptorType : this.descriptorTypeMap.values())
    {
      if (descriptorType instanceof NumericType)
      {
	numericTypeList.add((NumericType) descriptorType);
      }
    }
    return (numericTypeList);
  }


  public NumericType findNumericType(Integer id)
  {
    return ((NumericType) this.descriptorTypeMap.get(id));
  }


  public NumericType findNumericType(String typename)
  {
    Map<String, DescriptorType> typeNameDescriptorTypeMap = this.makeTypeNameDescriptorTypeMap();
    return ((NumericType) typeNameDescriptorTypeMap.get(typename));
  }


  public List<ImageType> findImageTypeList()
  {
    List<ImageType> imageTypeList = new ArrayList<ImageType>();
    for (DescriptorType descriptorType : this.descriptorTypeMap.values())
    {
      if (descriptorType instanceof ImageType)
      {
	imageTypeList.add((ImageType) descriptorType);
      }
    }
    return (imageTypeList);
  }


  public ImageType findImageType(Integer id)
  {
    return ((ImageType) this.descriptorTypeMap.get(id));
  }


  public ImageType findImageType(String typename)
  {
    Map<String, DescriptorType> typeNameDescriptorTypeMap = this.makeTypeNameDescriptorTypeMap();
    return ((ImageType) typeNameDescriptorTypeMap.get(typename));
  }


  public List<Procedure> findProcedureList()
  {
    return (new ArrayList<Procedure>(this.procedureMap.values()));
  }


  public Procedure findProcedure(Integer id)
  {
    return (this.procedureMap.get(id));
  }


  public Procedure findProcedure(String procedureName)
  {
    Map<String, Procedure> nameProcedureMap = this.makeNameProcedureMap();
    return (nameProcedureMap.get(procedureName));
  }


  public CategoricalTypeValue findCategoricalTypeValue(String categoricalTypeName, String categoricalTypeValueName)
  {
    CategoricalType categoricalType = this.findCategoricalType(categoricalTypeName);
    // linear search -- should be ok for small sets of values
    for (CategoricalTypeValue categoricalTypeValue : categoricalType.getCategoricalTypeValueSet())
    {
      if (categoricalTypeValueName.equals(categoricalTypeValue.getValueType()))
      {
	return (categoricalTypeValue);
      }
    }
    return (null);
  }


  public List<CategoricalTypeValue> findCategoricalTypeValueList(String typename)
  {
    CategoricalType categoricalType = this.findCategoricalType(typename);
    return (new ArrayList<CategoricalTypeValue>(categoricalType.getCategoricalTypeValueSet()));
  }


  public void dumpEntities(String basename) throws IOException
  {
    throw new RuntimeException("not yet implemented");
  }


  public void insertUser(IsacrodiUser isacrodiUser)
  {
    isacrodiUser.setId(new Integer(this.nextId++));
    this.isacrodiUserMap.put(isacrodiUser.getId(), isacrodiUser);
  }


  public IsacrodiUser findUser(String username)
  {
    Map<String, IsacrodiUser> usernameIsacrodiUserMap = this.makeUsernameIsacrodiUserMap();
    return (usernameIsacrodiUserMap.get(username));
  }


  public IsacrodiUser authenticate(String username, String password)
  {
    throw new RuntimeException("not yet implemented");
  }
}
