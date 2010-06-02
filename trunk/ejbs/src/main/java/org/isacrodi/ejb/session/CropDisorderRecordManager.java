package org.isacrodi.ejb.session;

import java.util.List;

import org.isacrodi.ejb.entity.*;


public interface CropDisorderRecordManager
{
  List<CropDisorderRecord> findCropDisorderRecordList();
  CropDisorderRecord findCropDisorderRecord(Integer id);
}
