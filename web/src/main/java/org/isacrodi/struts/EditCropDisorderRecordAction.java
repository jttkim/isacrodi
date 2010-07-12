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


public class EditCropDisorderRecordAction extends EditCropDisorderRecordSupport
{
  private String cropScientificName;
  private String expertDiagnosedCropDisorderScientificName;


  public EditCropDisorderRecordAction() throws NamingException
  {
    super();
  }


  public String getCropScientificName()
  {
    return (this.cropScientificName);
  }


  public void setCropScientificName(String cropScientificName)
  {
    this.cropScientificName = cropScientificName;
  }


  public String getExpertDiagnosedCropDisorderScientificName()
  {
    return (this.expertDiagnosedCropDisorderScientificName);
  }


  public void setExpertDiagnosedCropDisorderScientificName(String expertDiagnosedCropDisorderScientificName)
  {
    this.expertDiagnosedCropDisorderScientificName = expertDiagnosedCropDisorderScientificName;
  }


  public String execute()
  {
    this.LOG.info("edit CDR: executing");
    this.cropDisorderRecordManager.update(this.cropDisorderRecord, this.cropScientificName, this.expertDiagnosedCropDisorderScientificName);
    return (SUCCESS);
  }


  public void validate()
  {
    super.validate();
    if ((this.cropScientificName != null) && (this.cropScientificName.length() > 0))
    {
      Crop crop = this.access.findCrop(cropScientificName);
      if (crop == null)
      {
	this.addFieldError("cropScientificName", "unknown crop");
      }
    }
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
