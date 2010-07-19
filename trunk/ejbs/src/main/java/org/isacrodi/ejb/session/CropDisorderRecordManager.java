package org.isacrodi.ejb.session;

import java.util.List;
import java.util.Map;

import org.isacrodi.diagnosis.DiagnosisProvider;
import org.isacrodi.ejb.entity.*;


public interface CropDisorderRecordManager
{
  /**
   * Find all persisted crop disorder records.
   *
   * @return a list containing all persisted crop disorder records.
   */
  List<CropDisorderRecord> findCropDisorderRecordList();

  /**
   * Find all persisted crop disorder records that have a disorder diagnosed by an expert.
   *
   * @return a list containing all persisted crop disorder records with an expert diagnosis.
   */
  List<CropDisorderRecord> findExpertDiagnosedCropDisorderRecordList();

  /**
   * Find a crop disorder record by ID.
   *
   * @param id the id
   * @return the crop disorder record, or {@code null} if no record with that id exists.
   */
  CropDisorderRecord findCropDisorderRecord(Integer id);

  /**
   * Get a current diagnosis provider.
   */
  DiagnosisProvider constructDiagnosisProvider();

  /**
   * Request a diagnosis for a crop disorder record.
   *
   * @param cropDisorderRecordId the ID of the crop disorder record to be diagnosed
   */
  void requestDiagnosis(int cropDisorderRecordId);


  /**
   * Request a diagnosis for a crop disorder record, specifying
   * whether a new (updated) diagnosis provider should be constructed
   * for this purpose.
   *
   * @param cropDisorderRecordId the ID of the crop disorder record to be diagnosed
   * @param constructNewDiagnosisProvider passing in {@true} forces
   * construction of a new diagnosis provider based on the training
   * data currently available
   */
  void requestDiagnosis(int cropDisorderRecordId, boolean constructNewDiagnosisProvider);

  /**
   * Update a crop disorder record.
   *
   * <p>This method uses the contents of the record passed in to
   * update the simple properties in the persistent storage. Links to
   * other entities are updated based on the further parameters. This
   * method does not update any collection properties.</p>
   *
   * <p>If the record passed to this method has no ID, it will be
   * persisted. Otherwise, the existing record is updated according to
   * the properties provided by the record passed in.</p>
   *
   * @param cropDisorderRecord the record containing up to date properties
   * @param cropScientificName the scientific name of the crop
   * @param expertDiagnosedCropDisorderScientificName the scientific name of the disorder diagnosed by an expert
   */
  void update(CropDisorderRecord cropDisorderRecord, String cropScientificName, String expertDiagnosedCropDisorderScientificName);

  /**
   * Replace the numeric descriptors of a crop disorder record with new ones specified by a map.
   *
   * The keys of the map are numeric type IDs, the values are the numeric values.
   *
   * @param cropDisorderRecordId the ID of the crop disorder record to be updated
   * @param numericDescriptorMap the map from which to update the numeric descriptors
   */
  void updateNumericDescriptors(Integer cropDisorderRecordId, Map<Integer, Double> numericDescriptorMap);
}
