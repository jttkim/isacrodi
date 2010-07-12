package org.isacrodi.ejb.session;

import java.lang.reflect.InvocationTargetException;

import java.util.List;
import java.util.Map;

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
  Crop findCrop(String scientificName);
  CropDisorder findCropDisorder(String scientificName);
  NumericType findNumericType(Integer id);
  NumericType findNumericType(String name);
  ImageType findImageType(String name);
}
