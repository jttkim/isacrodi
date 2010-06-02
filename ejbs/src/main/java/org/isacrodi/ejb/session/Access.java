package org.isacrodi.ejb.session;

import org.isacrodi.ejb.entity.*;


/**
 * Access to persistent entities.
 *
 * This interface provides basic create, read, update, delete (CRUD)
 * methods.
 */
public interface Access
{
  void insert(Crop crop);
  void insert(CropDisorder cropDisorder, String[] cropScientificNameSet);
  void insert(NumericType numericType);
  void insert(CropDisorderRecord cropDisorderRecord, String username, String cropScientificName);
  NumericType findNumericType(String name);
}
