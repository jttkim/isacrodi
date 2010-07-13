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


public class ShowCropDisorderRecordAction extends CropDisorderRecordActionSupport implements ModelDriven<CropDisorderRecord>, Preparable
{
  private Integer cropDisorderRecordId;
  private CropDisorderRecord cropDisorderRecord;
  private CropDisorderRecordManager cropDisorderRecordManager;


  public ShowCropDisorderRecordAction() throws NamingException
  {
    super();
  }


  public String execute()
  {
    this.LOG.info("show CDR: executing");
    return (SUCCESS);
  }
}
