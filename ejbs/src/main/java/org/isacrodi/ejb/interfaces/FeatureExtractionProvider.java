package isacrodi.ejb.interfaces;


public class FeatureExtractionProvider implements DiagnosisProvider
{

  private String diagnose;


  public FeatureExtractionProvider()
  {
  }

  public String getDiagnose()
  {
    return diagnose;
  }
}
