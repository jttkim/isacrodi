package org.isacrodi.struts;

import java.util.List;
import java.util.ArrayList;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.ValueStack;

import org.isacrodi.ejb.entity.*;

import org.isacrodi.ejb.session.CropDisorderRecordManager;

import static org.javamisc.Util.genericTypecast;


public class RequestDiagnosisAction extends IsacrodiActionSupport
{
  private Integer cropDisorderRecordId;
  private CropDisorderRecord cropDisorderRecord;
  private CropDisorderRecordManager cropDisorderRecordManager;


  public RequestDiagnosisAction() throws NamingException
  {
    super();
    InitialContext context = new InitialContext();
    this.cropDisorderRecordManager = (CropDisorderRecordManager) context.lookup("isacrodi/CropDisorderRecordManagerBean/remote");
  }


  public void setCropDisorderRecordId(Integer cropDisorderRecordId)
  {
    this.cropDisorderRecordId = cropDisorderRecordId;
    this.LOG.info(String.format("requestDiagnosisActin: cdr Id set to %d", this.cropDisorderRecordId));
  }


  public Integer getCropDisorderRecordId()
  {
    return (this.cropDisorderRecordId);
  }


  // probably needed by showcropdisorderrrecord.jsp so leaving these...
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
      this.cropDisorderRecord = this.cropDisorderRecordManager.findCropDisorderRecord(this.cropDisorderRecordId);
      this.LOG.info(String.format("got cdr %d", this.cropDisorderRecord.getId().intValue()));
    }
  }


  public String execute()
  {
    this.LOG.info("requestDiagnosisAction: executing");
    if (this.cropDisorderRecordId == null)
    {
      this.LOG.error("RequestDiagnosisAction.execute: no CDR id");
      return (ERROR);
    }
    this.cropDisorderRecordManager.requestDiagnosis(this.cropDisorderRecordId.intValue());
    this.cropDisorderRecord = this.cropDisorderRecordManager.findCropDisorderRecord(this.cropDisorderRecordId);
    ActionContext actionContext = ActionContext.getContext();
    ValueStack valueStack = actionContext.getValueStack();
    valueStack.push(this.cropDisorderRecord);
    return (SUCCESS);
  }
}
