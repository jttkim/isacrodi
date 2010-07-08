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


public class EditCropDisorderRecordAction extends IsacrodiActionSupport implements ModelDriven<CropDisorderRecord>, Preparable
{
  private Integer cropDisorderRecordId;
  private CropDisorderRecord cropDisorderRecord;
  private CropDisorderRecordManager cropDisorderRecordManager;
  private String cropScientificName;
  private String expertDiagnosedCropDisorderScientificName;


  public EditCropDisorderRecordAction() throws NamingException
  {
    super();
    InitialContext context = new InitialContext();
    this.cropDisorderRecordManager = (CropDisorderRecordManager) context.lookup("isacrodi/CropDisorderRecordManagerBean/remote");
  }


  public Integer getCropDisorderRecordId()
  {
    return (this.cropDisorderRecordId);
  }


  public void setCropDisorderRecordId(Integer cropDisorderRecordId)
  {
    this.cropDisorderRecordId = cropDisorderRecordId;
    this.LOG.info(String.format("showCDR: cdr Id set to %d", this.cropDisorderRecordId));
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


  public List<NumericDescriptor> getNumericDescriptorList()
  {
    if (this.cropDisorderRecord == null)
    {
      return (null);
    }
    else
    {
      ArrayList<NumericDescriptor> numericDescriptorList = new ArrayList<NumericDescriptor>(this.cropDisorderRecord.findNumericDescriptorSet());
      // FIXME: sort descriptor here to maintain stable order
      return (numericDescriptorList);
    }
  }


  public List<ImageDescriptor> getImageDescriptorList()
  {
    if (this.cropDisorderRecord == null)
    {
      return (null);
    }
    else
    {
      ArrayList<ImageDescriptor> imageDescriptorList = new ArrayList<ImageDescriptor>(this.cropDisorderRecord.findImageDescriptorSet());
      // FIXME: sort descriptor here to maintain stable order
      return (imageDescriptorList);
    }
  }


  public void prepare()
  {
    if (this.cropDisorderRecordId == null)
    {
      this.LOG.info("no cdr id set");
      this.cropDisorderRecord = new CropDisorderRecord();
    }
    else
    {
      this.cropDisorderRecord = this.cropDisorderRecordManager.findCropDisorderRecord(this.cropDisorderRecordId);
      this.LOG.info(String.format("got cdr %d", this.cropDisorderRecord.getId().intValue()));
    }
  }


  public CropDisorderRecord getModel()
  {
    return (this.cropDisorderRecord);
  }


  public String execute()
  {
    this.LOG.info("show CDR: executing");
    this.cropDisorderRecordManager.update(this.cropDisorderRecord, this.cropScientificName, this.expertDiagnosedCropDisorderScientificName);
    return (SUCCESS);
  }


  public void validate()
  {
    if (this.cropDisorderRecord.getId() == null)
    {
    }
    else
    {
    }
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
