package org.isacrodi.ejb.session;

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
  void insert(CropDisorderRecord cropDisorderRecord, String username, String cropScientificName);
  Crop findCrop(String scientificName);
  CropDisorder findCropDisorder(String scientificName);
  NumericType findNumericType(String name);
}
