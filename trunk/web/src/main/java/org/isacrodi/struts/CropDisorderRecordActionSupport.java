package org.isacrodi.struts;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;

import org.isacrodi.ejb.entity.*;

import org.isacrodi.ejb.session.CropDisorderRecordManager;

import static org.javamisc.Util.genericTypecast;


public abstract class CropDisorderRecordActionSupport extends IsacrodiActionSupport implements ModelDriven<CropDisorderRecord>, Preparable
{
  protected Integer cropDisorderRecordId;
  protected CropDisorderRecord cropDisorderRecord;
  protected CropDisorderRecordManager cropDisorderRecordManager;


  public CropDisorderRecordActionSupport() throws NamingException
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
    this.LOG.info(String.format("CDR action support: cdr Id set to %d", this.cropDisorderRecordId));
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
      Comparator<NumericDescriptor> comparator = new Comparator<NumericDescriptor>()
      {
	public int compare(NumericDescriptor n1, NumericDescriptor n2)
	{
	  String t1Name = n1.getNumericType().getTypeName();
	  String t2Name = n2.getNumericType().getTypeName();
	  return (t1Name.compareTo(t2Name));
	}
      };
      Collections.sort(numericDescriptorList, comparator);
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
      Comparator<ImageDescriptor> comparator = new Comparator<ImageDescriptor>()
      {
	public int compare(ImageDescriptor i1, ImageDescriptor i2)
	{
	  String t1Name = i1.getImageType().getTypeName();
	  String t2Name = i2.getImageType().getTypeName();
	  return (t1Name.compareTo(t2Name));
	}
      };
      Collections.sort(imageDescriptorList, comparator);

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


  public abstract String execute();


  public void validate()
  {
    if (this.cropDisorderRecord.getId() == null)
    {
      this.addActionError("crop disorder record ID missing");
    }
    else
    {
      // FIXME: should validate that CDR to be updated exists etc.
    }
  }
}
