package org.isacrodi.ejb.session;

import java.io.IOException;

import org.isacrodi.ejb.entity.*;

import org.isacrodi.diagnosis.DiagnosisProvider;


public interface Kludge
{
  void concoctDiagnosis(Integer cdrId);
  void makeRandomExpertDiagnosedCDRs(int rndseed, int numNumericTypes, int numCrops, int numCropDisorders, int numCDRs, int numDisorderAssociations, double numericDescriptorPercentage, double stddevBetween, double stddevWithin);
  ImageDescriptor findImageDescriptor(Integer id);
  DiagnosisProvider findDiagnosisProvider() throws IOException, ClassNotFoundException;
  void storeDiagnosisProvider(DiagnosisProvider diagnosisProvider) throws IOException;
/*
  void makeDummies();
  Dummy0 findDummy0(Integer id);
  Dummy0 findDummy0(String name);
  void makeScenario();
*/
}
