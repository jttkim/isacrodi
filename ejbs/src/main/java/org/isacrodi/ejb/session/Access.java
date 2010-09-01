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
  void insert(Procedure procedure, String[] incompatibleProcedureNameSet, String[] CropDisorderScientificNameSet);
  void insert(CategoricalType categoricalType, String[] valueString);
  void insert(NumericType numericType);
  void insert(ImageType imageType);
  Integer insert(String username, String cropScientificName, Set<Descriptor> descriptorSet, String expertDiagnosisName);
  Crop findCrop(String scientificName);
  List<Crop> findCropList();
  CropDisorder findCropDisorder(String scientificName);
  CategoricalType findCategoricalType(Integer id);
  CategoricalType findCategoricalType(String typename);
  NumericType findNumericType(Integer id);
  NumericType findNumericType(String name);
  ImageType findImageType(String name);
  Procedure findProcedure(Integer id);
  Procedure findProcedure(String procedureName);
  CategoricalTypeValue findCategoricalTypeValue(String categoricalTypeName, String categoricalTypeValueName);
  List<CategoricalTypeValue> findCategoricalTypeValueList(String typename);
  void dumpEntities(String basename) throws IOException;
}
