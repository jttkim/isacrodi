package org.isacrodi.ejb.session;

import org.isacrodi.ejb.entity.*;


public interface Kludge
{
  void concoctDiagnosis(Integer cdrId);
  void makeRandomExpertDiagnosedCDRs(int rndseed, int numNumericTypes, int numCrops, int numCropDisorders, int numCDRs, int numDisorderAssociations, double numericDescriptorPercentage, double stddevBetween, double stddevWithin);
  ImageDescriptor findImageDescriptor(Integer id);
/*
  void makeDummies();
  Dummy0 findDummy0(Integer id);
  Dummy0 findDummy0(String name);
  void makeScenario();
*/
}
