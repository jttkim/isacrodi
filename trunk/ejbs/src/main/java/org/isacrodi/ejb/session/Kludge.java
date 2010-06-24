package org.isacrodi.ejb.session;


public interface Kludge
{
  void concoctDiagnosis(Integer cdrId);
  void makeRandomExpertDiagnosedCDRs(int rndseed, int numNumericTypes, int numCrops, int numCropDisorders, int numCDRs, int numDisorderAssociations, double numericDescriptorPercentage, double stddevBetween, double stddevWithin);
}
