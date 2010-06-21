package org.isacrodi.struts;

import java.util.List;
import java.util.ArrayList;

import javax.naming.NamingException;

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;

import org.isacrodi.ejb.entity.*;

import static org.javamisc.Util.genericTypecast;


public class ShowCropDisorderRecordAction extends IsacrodiActionSupport implements ModelDriven<CropDisorderRecord>, Preparable
{
  private Integer cropDisorderRecordId;
  private CropDisorderRecord cropDisorderRecord;


  public ShowCropDisorderRecordAction() throws NamingException
  {
    super();
  }


  public void setCropDisorderRecordId(Integer cropDisorderRecordId)
  {
    this.cropDisorderRecordId = cropDisorderRecordId;
    this.LOG.info(String.format("showCDR: cdr Id set to %d", this.cropDisorderRecordId));
  }


  public Integer getCropDisorderRecordid()
  {
    return (this.cropDisorderRecordId);
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
      this.cropDisorderRecord = null;
    }
    else
    {
      this.cropDisorderRecord = this.entityAccess.findEntity(CropDisorderRecord.class, this.cropDisorderRecordId);
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
    return (SUCCESS);
  }
}
