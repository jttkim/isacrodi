package org.isacrodi.struts;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import org.isacrodi.ejb.entity.*;

import org.isacrodi.ejb.session.CropDisorderRecordManager;

import static org.javamisc.Util.genericTypecast;


public class EditNumericDescriptorsAction extends EditCropDisorderRecordSupport implements ServletRequestAware
{
  static final String numericparameterPrefix = "numerictype_id_";
  private HttpServletRequest servletRequest;
  private Map<Integer, Double> numericDescriptorMap;
  private String newNumericTypeName;
  private Double newNumericValue;


  public EditNumericDescriptorsAction() throws NamingException
  {
    super();
  }


  public void setServletRequest(HttpServletRequest servletRequest)
  {
    this.servletRequest = servletRequest;
  }


  public String getNewNumericTypeName()
  {
    return (this.newNumericTypeName);
  }


  public void setNewNumericTypeName(String newNumericTypeName)
  {
    this.newNumericTypeName = newNumericTypeName;
  }


  public Double getNewNumericValue()
  {
    return (this.newNumericValue);
  }


  public void setNewNumericValue(Double newNumericValue)
  {
    this.newNumericValue = newNumericValue;
  }


  private static Integer numericTypeIdFromParameter(String parameterName)
  {
    if (!parameterName.startsWith(numericparameterPrefix))
    {
      return (null);
    }
    return (new Integer(parameterName.substring(numericparameterPrefix.length())));
  }


  public void extractNumericDescriptorMap()
  {
    this.numericDescriptorMap = new HashMap<Integer, Double>();
    Map<String, String[]> parameterMap = genericTypecast(this.servletRequest.getParameterMap());
    for (String parameterName : parameterMap.keySet())
    {
      Integer numericTypeId = numericTypeIdFromParameter(parameterName);
      if (numericTypeId != null)
      {
	if (this.access.findNumericType(numericTypeId) == null)
	{
	  this.addFieldError(parameterName, "unknown numeric type");
	}
	if (numericTypeId != null)
	{
	  // FIXME: just assuming that there's only one parameter of this name
	  String numericValueStr = parameterMap.get(parameterName)[0];
	  if (numericValueStr.length() > 0)
	  {
	    try
	    {
	      Double numericValue = new Double(Double.parseDouble(numericValueStr));
	      this.numericDescriptorMap.put(numericTypeId, numericValue);
	    }
	    catch (NumberFormatException e)
	    {
	      this.addFieldError(parameterName, "malformed number");
	    }
	  }
	}
      }
    }
    if (this.newNumericTypeName.length() > 0)
    {
      NumericType newNumericType = this.access.findNumericType(this.newNumericTypeName);
      if (newNumericType == null)
      {
	this.addFieldError("newNumericTypeName", "unknown numeric type");
      }
      else
      {
	for (Integer id : this.numericDescriptorMap.keySet())
	{
	  if (newNumericType.getId().equals(id))
	  {
	    this.addFieldError("newNumericTypeName", "duplicate numeric descriptor");
	  }
	}
      }
      if (this.newNumericValue == null)
      {
	this.addFieldError("newNumericValue", "no value specified");
      }
      if ((newNumericType != null) && (this.newNumericValue != null))
      {
	this.numericDescriptorMap.put(newNumericType.getId(), this.newNumericValue);
      }
    }
  }


  public String execute()
  {
    this.LOG.info("edit numeric descriptors: executing");
    this.cropDisorderRecordManager.updateNumericDescriptors(this.cropDisorderRecord.getId(), this.numericDescriptorMap);
    return (SUCCESS);
  }


  public void validate()
  {
    super.validate();
    this.extractNumericDescriptorMap();
  }
}
