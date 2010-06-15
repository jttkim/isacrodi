package org.isacrodi.struts;

import java.util.List;
import java.util.ArrayList;

import javax.naming.NamingException;

import org.isacrodi.ejb.entity.*;

import static org.isacrodi.util.Util.genericTypecast;


public class WelcomeAction extends IsacrodiActionSupport
{
  private List<CropDisorderRecord> cropDisorderRecordList;


  public WelcomeAction() throws NamingException
  {
    super();
    this.cropDisorderRecordList = new ArrayList<CropDisorderRecord>();
  }


  public List<CropDisorderRecord> getCropDisorderRecordList()
  {
    return (this.cropDisorderRecordList);
  }


  public String execute()
  {
    this.cropDisorderRecordList = genericTypecast(this.access.findEntityList(CropDisorderRecord.class));
    return (SUCCESS);
  }
}
