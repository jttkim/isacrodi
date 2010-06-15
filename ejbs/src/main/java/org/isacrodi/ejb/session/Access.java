package org.isacrodi.ejb.session;

import java.util.List;

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
  void insert(NumericType numericType);
  void insert(ImageType imageType);
  void insert(CropDisorderRecord cropDisorderRecord, String username, String cropScientificName);
  <EntityClass> EntityClass findEntity(Class<EntityClass> entityClass, Integer id);
  List<?> findEntityList(Class<?> entityClass);
  Crop findCrop(String scientificName);
  CropDisorder findCropDisorder(String scientificName);
  NumericType findNumericType(String name);
  ImageType findImageType(String name);
}
