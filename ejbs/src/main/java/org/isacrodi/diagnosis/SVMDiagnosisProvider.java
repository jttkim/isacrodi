package org.isacrodi.diagnosis;


/**
 * Compute a diagnosis for a CDR using a SVM operating on entire CDRs.
 */
public class SVMDiagnosisProvider implements DiagnosisProvider
{
  private FeatureExtractor featureExtractor;
  private FeatureVectorMapper svmFeatureVectorMapper;
  private svm_model model;

  
  public Diagnosis diagnose(CropDisorderRecord cropDisorderRecord)
  {
    FeatureVector featureVector = this.featureExtractor.extract(cropDisorderRecord);
    svm_node[] svmFeatureVector = this.svmFeatureVectorMapper.map(featureVector);
    probabilities = this.model.predict(svm_node);
    // construct Diagnosis instance
    // put probabilities into Diagnosis instance
    // return diagnosis instance
  }
}