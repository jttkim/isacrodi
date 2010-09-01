package org.isacrodi.struts;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import org.isacrodi.ejb.entity.*;

import org.isacrodi.ejb.session.CropDisorderRecordManager;

import static org.javamisc.Util.genericTypecast;


public class EditCategoricalDescriptorsAction extends CropDisorderRecordActionSupport implements ServletRequestAware
{
  static final String categoricalparameterPrefix = "categoricaltype_id_";
  private HttpServletRequest servletRequest;
  private Map<Integer, Set<String>> categoricalDescriptorMap;
  private String newCategoricalTypeName;
  private String newCategoricalValue;


  public EditCategoricalDescriptorsAction() throws NamingException
  {
    super();
  }


  public void setServletRequest(HttpServletRequest servletRequest)
  {
    this.servletRequest = servletRequest;
  }


  public String getNewCategoricalTypeName()
  {
    return (this.newCategoricalTypeName);
  }


  public void setNewCategoricalTypeName(String newCategoricalTypeName)
  {
    this.newCategoricalTypeName = newCategoricalTypeName;
  }


  public String getNewCategoricalValue()
  {
    return (this.newCategoricalValue);
  }


  public void setNewCategoricalValue(String newCategoricalValue)
  {
    this.newCategoricalValue = newCategoricalValue;
  }


  private static Integer categoricalTypeIdFromParameter(String parameterName)
  {
    if (!parameterName.startsWith(categoricalparameterPrefix))
    {
      return (null);
    }
    return (new Integer(parameterName.substring(categoricalparameterPrefix.length())));
  }


  public void extractCategoricalDescriptorMap()
  {
    this.categoricalDescriptorMap = new HashMap<Integer, Set<String>>();
    Map<String, String[]> parameterMap = genericTypecast(this.servletRequest.getParameterMap());
    for (String parameterName : parameterMap.keySet())
    {
      Integer categoricalTypeId = categoricalTypeIdFromParameter(parameterName);
      if (categoricalTypeId != null)
      {
	if (this.access.findCategoricalType(categoricalTypeId) == null)
	{
	  this.addFieldError(parameterName, "unknown categorical type");
	}
	if (categoricalTypeId != null)
	{
	  // FIXME: just assuming that there's only one parameter of this name
	  String categoricalValue = parameterMap.get(parameterName)[0];
	  // FIXME: empty values silently skipped
	  if (categoricalValue.length() > 0)
	  {
	    Set<String> categoricalTypeValueSet = this.categoricalDescriptorMap.get(categoricalTypeId);
	    if (categoricalTypeValueSet == null)
	    {
	      categoricalTypeValueSet = new HashSet<String>();
	      this.categoricalDescriptorMap.put(categoricalTypeId, categoricalTypeValueSet);
	    }
	    categoricalTypeValueSet.add(categoricalValue);
	  }
	}
      }
    }
    if (this.newCategoricalTypeName.length() > 0)
    {
      CategoricalType newCategoricalType = this.access.findCategoricalType(this.newCategoricalTypeName);
      if (newCategoricalType == null)
      {
	this.addFieldError("newCategoricalTypeName", "unknown categorical type");
      }
      else
      {
	for (Integer id : this.categoricalDescriptorMap.keySet())
	{
	  if (newCategoricalType.getId().equals(id))
	  {
	    this.addFieldError("newCategoricalTypeName", "duplicate categorical descriptor");
	  }
	}
      }
      if (this.newCategoricalValue == null)
      {
	this.addFieldError("newCategoricalValue", "no value specified");
      }
      if ((newCategoricalType != null) && (this.newCategoricalValue != null))
      {
	Set<String> valueSet = new HashSet<String>();
	valueSet.add(this.newCategoricalValue);
	this.categoricalDescriptorMap.put(newCategoricalType.getId(), valueSet);
      }
    }
  }


  public String execute()
  {
    this.LOG.info("edit categorical descriptors: executing");
    this.cropDisorderRecordManager.updateCategoricalDescriptors(this.cropDisorderRecord.getId(), this.categoricalDescriptorMap);
    return (SUCCESS);
  }


  public void validate()
  {
    super.validate();
    this.extractCategoricalDescriptorMap();
  }
}
