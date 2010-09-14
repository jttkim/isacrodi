package org.isacrodi.struts;

import java.util.List;
import java.util.ArrayList;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;

import org.isacrodi.ejb.entity.*;

import org.isacrodi.ejb.session.CropDisorderRecordManager;

import static org.javamisc.Util.genericTypecast;


public class EditExpertDiagnosisAction extends CropDisorderRecordActionSupport
{
  private String cropScientificName;
  private String expertDiagnosedCropDisorderScientificName;
  private List<CropDisorder> cropDisorderList;


  public EditExpertDiagnosisAction() throws NamingException
  {
    super();
  }


  public String getExpertDiagnosesCropDisorderScientificName()
  {
    return (this.expertDiagnosedCropDisorderScientificName);
  }


  public void setExpertDiagnosedCropDisorderScientificName(String expertDiagnosedCropDisorderScientificName)
  {
    this.expertDiagnosedCropDisorderScientificName = expertDiagnosedCropDisorderScientificName;
  }


  public void prepare()
  {
    super.prepare();
    if (this.cropDisorderRecord != null)
    {
      CropDisorder cropDisorder = cropDisorderRecord.getExpertDiagnosedCropDisorder();
      if (cropDisorder != null)
      {
	this.expertDiagnosedCropDisorderScientificName = cropDisorder.getScientificName();
      }
    }
    this.cropDisorderList = this.access.findCropDisorderList();
  }


  public String execute()
  {
    this.LOG.info(String.format("updating expert diagnosis to %s", this.expertDiagnosedCropDisorderScientificName));
    this.cropDisorderRecordManager.update(this.cropDisorderRecord, null, this.expertDiagnosedCropDisorderScientificName);
    return (SUCCESS);
  }


  public List getCropDisorderList()
  {
    return (this.cropDisorderList);
  }


  public void validate()
  {
    super.validate();
    if ((this.expertDiagnosedCropDisorderScientificName != null) && (this.expertDiagnosedCropDisorderScientificName.length() > 0))
    {
      CropDisorder expertDiagnosedCropDisorder = this.access.findCropDisorder(expertDiagnosedCropDisorderScientificName);
      if (expertDiagnosedCropDisorder == null)
      {
	this.addFieldError("expertDiagnosedCropDisorderScientificName", "unknown crop disorder");
      }
    }
  }
}
