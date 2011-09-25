package org.isacrodi.ejb.session;

import java.lang.reflect.InvocationTargetException;

import java.io.IOException;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.isacrodi.ejb.entity.*;


/**
 * Access to persistent entities.
 *
 * This interface provides basic create, read, update, delete (CRUD)
 * methods.
 *
 * The business methods in this interface are not particularly proofed
 * against passing in bad parameters, such as {@code null} references.
 */
public interface Access
{
  void insert(Crop crop);
  void insert(CropDisorder cropDisorder, String[] cropScientificNameSet);
  void insert(Procedure procedure, String[] incompatibleProcedureNameSet, String[] cropDisorderScientificNameSet);
  void insert(CategoricalType categoricalType, String[] valueString);
  void insert(NumericType numericType);
  void insert(ImageType imageType);
  /**
   * Insert a new {@link org.isacrodi.ejb.entity.CropDisorderRecord} instance.
   *
   * <p>The parameters have to specify existing persistent entities by
   * their respective appropriate names.</p>
   *
   * @param username the Isacrodi username
   * @param cropScientificname the crop's scientific name, may be {@code null} or {@code ""} to leave crop unspecified
   * @param descriptorSet set of descriptors (may be {@code null})
   * @param expertDiagnosisName the scientific name of the disorder diagnosed by an expert (may be {@code null})
   */
  Integer insert(String username, String cropScientificName, Set<Descriptor> descriptorSet, String expertDiagnosisName);
  Crop findCrop(String scientificName);
  List<Crop> findCropList();
  CropDisorder findCropDisorder(String scientificName);
  List<CropDisorder> findCropDisorderList();
  List<DescriptorType> findDescriptorTypeList();
  List<CategoricalType> findCategoricalTypeList();
  CategoricalType findCategoricalType(Integer id);
  CategoricalType findCategoricalType(String typename);
  List<NumericType> findNumericTypeList();
  NumericType findNumericType(Integer id);
  NumericType findNumericType(String typename);
  List<ImageType> findImageTypeList();
  ImageType findImageType(Integer id);
  ImageType findImageType(String typename);
  List<Procedure> findProcedureList();
  Procedure findProcedure(Integer id);
  Procedure findProcedure(String procedureName);
  // @Deprecated
  CategoricalTypeValue findCategoricalTypeValue(String categoricalTypeName, String categoricalTypeValueName);
  /**
   * Find the list of values that are possible for a categorical type
   * specified by a name.
   *
   * <p>Should better use {@link findCategoricalType(String)} and work
   * with the values contained in the return value.</p>
   */
  @Deprecated
  List<CategoricalTypeValue> findCategoricalTypeValueList(String typename);
  void dumpEntities(String basename) throws IOException;
}
