package org.isacrodi.struts;

import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import java.util.Collection;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.naming.NamingException;
import javax.naming.InitialContext;

import javax.servlet.http.HttpServletRequest;

import org.isacrodi.ejb.session.*;
import org.isacrodi.ejb.entity.*;

import static org.isacrodi.util.Util.genericTypecast;
import static org.isacrodi.util.Util.constructDefaultInstance;
import static org.isacrodi.util.Util.extractPropertyName;
import static org.isacrodi.util.Util.isAccessor;
import static org.isacrodi.util.Util.getProperty;
import static org.isacrodi.util.Util.setProperty;
import static org.isacrodi.util.Util.isEntityClass;
import static org.isacrodi.util.Util.isEntityInstance;
import static org.isacrodi.util.Util.findUniquePropertyNameSet;
import static org.isacrodi.util.Util.findPropertyType;
import static org.isacrodi.util.Util.findPropertyNameSet;


public class CrudAction extends IsacrodiActionSupport
{
  private String entityClassName;
  private String entityId;
  private String crudOp;
  private static final String packageName = "org.isacrodi.ejb.entity";
  private static final String[] hiddenPropertyNameList = {"class", "version"};
  private static final String[] readOnlyPropertyNameList = {"id"};
  private static final String crudParameterPrefix = "_crud_";


  public CrudAction() throws NamingException
  {
    super();
  }


  // obsolete
  private static String listAccessibleProperties(Object entity) throws IllegalAccessException, InvocationTargetException
  {
    if (entity == null)
    {
      return ("null entity");
    }
    String s = "";
    Method[] methodList = entity.getClass().getDeclaredMethods();
    for (Method method : methodList)
    {
      if (isAccessor(method))
      {
	String methodName = method.getName();
	Object property = method.invoke(entity);
	if (property == null)
	{
	  property = "null";
	}
	s += String.format("%s -> %s, %s\n", methodName, extractPropertyName(methodName), property.toString());
      }
    }
    return (s);
  }


  public String getEntityDump() throws ClassNotFoundException, IllegalAccessException, InvocationTargetException
  {
    Object entity = this.retrieveEntity();
    if (entity == null)
    {
      return ("<null entity>");
    }
    else
    {
      return (this.listAccessibleProperties(entity));
    }
  }


  public static String htmlEscape(String s)
  {
    return(s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;"));
  }


  public Class<?> findEntityClass() throws ClassNotFoundException
  {
    if (this.entityClassName == null)
    {
      this.LOG.info("trying to find entity class without entity class name");
      return (null);
    }
    Class<?> entityClass = Class.forName(canonicalEntityClassName(this.entityClassName));
    if (!isEntityClass(entityClass))
    {
      throw new IllegalArgumentException(String.format("%s is not an entity class", this.entityClassName));
    }
    return (entityClass);
  }


  public Object newEntity() throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException
  {
    if (this.entityClassName == null)
    {
      return (null);
    }
    else
    {
      return (constructDefaultInstance(this.findEntityClass()));
    }
  }


  public Object retrieveEntity() throws ClassNotFoundException
  {
    if (this.entityClassName == null)
    {
      this.LOG.info("Cannot retrieve entity: no entity class name");
      return (null);
    }
    else if (this.entityId == null)
    {
      this.LOG.info(String.format("cannot retrieve entity %s: no entity id", this.entityClassName));
      return (null);
    }
    else
    {
      this.LOG.info(String.format("entity name: %s, id: %s", this.entityClassName, this.entityId));
      Class<?> entityClass = this.findEntityClass();
      // FIXME: assuming retrieval by Integer id
      Object entity = this.access.findEntity(entityClass, new Integer(Integer.parseInt(this.entityId, 10)));
      return (entity);
    }
  }


  public static String entityLinkText(Object entity) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
  {
    String s = "";
    String glue = "";
    for (String propertyName : findUniquePropertyNameSet(entity))
    {
      s += String.format("%s%s: %s", glue, propertyName, getProperty(entity, propertyName).toString());
      glue = ", ";
    }
    return (s);
  }


  public static String entityHtmlLink(Object entity) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
  {
    // FIXME: just assuming that id is the id property, should really look for persistence annotation @Id
    // FIXME: hard-coded assumption that action name is "crud"
    Object id = getProperty(entity, "id");
    String s = String.format("<a href=\"crud?entityClassName=%s&entityId=%s\">%s</a>", htmlEscape(entity.getClass().getSimpleName()), htmlEscape(id.toString()), htmlEscape(entityLinkText(entity)));
    return (s);
  }


  public static String entityHtmlLinkList(Collection<?> entityCollection) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
  {
    if (entityCollection.size() == 0)
    {
      return ("");
    }
    String s = "<ul>\n";
    for (Object entity : entityCollection)
    {
      s += String.format("<li>%s</li>\n", entityHtmlLink(entity));
    }
    s += "</ul>\n";
    return (s);
  }


  public String crudMenu()
  {
    String s = "<hr/>\n";
    s += "[";
    if (this.entityId != null)
    {
      s += String.format("<a href=\"crud?entityClassName=%s&entityId=%s\">show</a>", this.entityClassName, this.entityId);
      s += "|";
      s += String.format("<a href=\"crud?entityClassName=%s&entityId=%s&crudOp=form\">edit</a>", this.entityClassName, this.entityId);
      s += "|";
    }
    s += String.format("<a href=\"crud?entityClassName=%s&crudOp=form\">new <code>%s</code></a>", this.entityClassName, htmlEscape(this.entityClassName));
    s += "|";
    s += String.format("<a href=\"crud?entityClassName=%s\">list all <code>%s</code></a>", this.entityClassName, htmlEscape(this.entityClassName));
    s += "]\n";
    return (s);
  }


  public String propertyHtmlTableRow(Object entity, String propertyName) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
  {
    Object property = getProperty(entity, propertyName);
    Class<?> propertyType = findPropertyType(entity, propertyName);
    String s = "<tr>";
    s += String.format("<td>%s</td>", htmlEscape(propertyName));
    s += "<td>";
    if (property == null)
    {
      s += htmlEscape("<null>");
    }
    else if (Collection.class.isAssignableFrom(propertyType))
    {
      // FIXME: ought to check whether this is a set of entities
      Collection<?> propertyCollection = genericTypecast(property);
      s += entityHtmlLinkList(propertyCollection);
    }
    else if (isEntityInstance(property))
    {
      s += entityHtmlLink(property);
    }
    else
    {
      // FIXME: probably not suitable for all property types...
      s += htmlEscape(property.toString());
    }
    s += "</td>";
    s += "</tr>\n";
    return (s);
  }


  public String entityHtmlTable(Object entity) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
  {
    Set<String> propertyNameSet = findPropertyNameSet(entity);
    String s = "<table>\n";
    for (String propertyName : propertyNameSet)
    {
      s += propertyHtmlTableRow(entity, propertyName);
    }
    s += "</table>\n";
    s += this.crudMenu();
    return (s);
  }


  public static boolean isHidden(String propertyName)
  {
    for (String h : hiddenPropertyNameList)
    {
      if (h.equals(propertyName))
      {
	return (true);
      }
    }
    return (false);
  }


  public static boolean isReadOnly(String propertyName)
  {
    for (String r : readOnlyPropertyNameList)
    {
      if (r.equals(propertyName))
      {
	return (true);
      }
    }
    return (false);
  }


  public static String crudFormParameterName(String propertyName)
  {
    return (crudParameterPrefix + propertyName);
  }


  public static boolean isEditableType(Class<?> c)
  {
    return (Integer.class.isAssignableFrom(c) || Double.class.isAssignableFrom(c) || String.class.isAssignableFrom(c));
  }


  public String propertyHtmlFormRow(Object entity, String propertyName) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
  {
    String s = "";
    Object property = getProperty(entity, propertyName);
    Class<?> propertyType = findPropertyType(entity, propertyName);
    if (Collection.class.isAssignableFrom(propertyType))
    {
      s += "<tr>";
      s += String.format("<td>%s</td>", htmlEscape(propertyName));
      s += "<td>";
      if (property == null)
      {
	s += "<strong><code>%s</code>: null collection</strong>";
      }
      else
      {
	// using multi-value for adding removing: first value is
	// command ("add" or "remove"), second value is id of entity
	// to be associated / disassociated
	s += String.format("<select name = %s>", crudFormParameterName(propertyName));
	s += "<option name=\"add\">add</option>";
	s += "<option name=\"remove\">remove</option>";
	s += "</select>";
	s += String.format("<input name=\"%s\" value=\"\"/>", crudFormParameterName(propertyName));
	Collection<?> collection = (Collection<?>) property;
	s += entityHtmlLinkList(collection);
      }
      s += "</td>";
      s += "</tr>\n";
    }
    else if (isEntityClass(propertyType))
    {
      s += "<tr>";
      s += String.format("<td>id of %s</td>", htmlEscape(propertyName));
      s += "<td>";
      String propertyValue = "";
      if (property != null)
      {
	Integer associatedEntityId = (Integer) getProperty(property, "id");
	propertyValue = associatedEntityId.toString();
      }
      s += String.format("<input name=\"%s\" value=\"%s\"/>", crudFormParameterName(propertyName), propertyValue);
      if (property != null)
      {
	s += "<br/>";
	s += entityHtmlLink(property);
      }
      s += "</td>";
      s += "</tr>\n";
    }
    else if (isEditableType(propertyType))
    {
      s += "<tr>";
      s += String.format("<td>%s</td>", htmlEscape(propertyName));
      s += "<td>";
      String propertyValue = "";
      if (property != null)
      {
	propertyValue = property.toString();
      }
      if (isReadOnly(propertyName))
      {
	s += htmlEscape(propertyValue);
      }
      else
      {
	s += String.format("<input name=\"%s\" value=\"%s\"/>", crudFormParameterName(propertyName), propertyValue);
      }
      s += "</td>";
      s += "</tr>\n";
    }
    else
    {
      s = propertyHtmlTableRow(entity, propertyName);
    }
    return (s);
  }


  public String entityHtmlForm(Object entity) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
  {
    // FIXME: consider introducing an IsacrodiEntity interface declaring getId (and setId), so that we can use typecasts to get at ids.
    Object id = getProperty(entity, "id");
    Set<String> propertyNameSet = findPropertyNameSet(entity);
    // FIXME: order properties?
    String s = "<form method=\"post\" action=\"crud\">\n";
    s += "<input type=\"hidden\" name=\"crudOp\" value=\"update\"/>";
    s += String.format("<input type=\"hidden\" name=\"entityClassName\" value=\"%s\"/>", entity.getClass().getSimpleName());
    if (id != null)
    {
      s += String.format("<input type=\"hidden\" name=\"entityId\" value=\"%s\"/>", id.toString());
    }
    s += "<table>\n";
    for (String propertyName : propertyNameSet)
    {
      if (!isHidden(propertyName))
      {
	if (isReadOnly(propertyName))
	{
	  s += propertyHtmlTableRow(entity, propertyName);
	}
	else
	{
	  s += propertyHtmlFormRow(entity, propertyName);
	}
      }
    }
    s += "<tr><td><input type=\"submit\"/></td><td></td></tr>\n";
    s += "</table>\n";
    s += "</form>\n";
    if (id != null)
    {
      s += "<form method=\"post\" action=\"crud\">\n";
      s += "<input type=\"hidden\" name=\"crudOp\" value=\"delete\"/>";
      s += String.format("<input type=\"hidden\" name=\"entityClassName\" value=\"%s\"/>", entity.getClass().getSimpleName());
      s += String.format("<input type=\"hidden\" name=\"entityId\" value=\"%s\"/>", id.toString());
      s += String.format("<input type=\"submit\" value=\"Delete %s\"/>", id.toString());
      s += "</form>\n";
    }
    s += this.crudMenu();
    return (s);
  }


  public String entitySetHtmlList() throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
  {
    Class<?> entityClass = this.findEntityClass();
    List<?> entityList = this.access.findEntityList(entityClass);
    String s;
    if (entityList.size() == 0)
    {
      s = (String.format("<p>no entities of class <code>%s</code> found</p>\n", htmlEscape(entityClassName)));
    }
    else
    {
      s = entityHtmlLinkList(entityList);
    }
    s += this.crudMenu();
    return (s);
  }


  public String getEntityHtml() throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException
  {
    Object entity = this.retrieveEntity();
    if (entity != null)
    {
      if (this.crudOp != null)
      {
	if  ("form".equals(this.crudOp))
	{
	  return (entityHtmlForm(entity));
	}
      }
      return (entityHtmlTable(entity));
    }
    else if (this.entityClassName != null)
    {
      if ("form".equals(this.crudOp))
      {
	entity = this.newEntity();
	if (entity != null)
	{
	  return (entityHtmlForm(entity));
	}
      }
      return (entitySetHtmlList());
    }
    else
    {
      return ("<strong>no entity class name specified</strong>");
    }
  }


  public static String canonicalEntityClassName(String entityClassName)
  {
    return (packageName + "." + entityClassName);
  }


  public void setEntityClassName(String entityClassName)
  {
    this.entityClassName = entityClassName;
  }


  public String getEntityClassName()
  {
    return (this.entityClassName);
  }


  public void setEntityId(String entityId)
  {
    this.entityId = entityId;
  }


  public String getCrudOp()
  {
    return (this.crudOp);
  }


  public void setCrudOp(String crudOp)
  {
    this.crudOp = crudOp;
  }


  public String getEntityId()
  {
    this.LOG.info("returning entity id " + this.entityId);
    return (this.entityId);
  }


  public Map<String, String[]> getCrudParameterMap()
  {
    Map<String, String[]> crudParameterMap = new HashMap<String, String[]>();
    Map<String, String[]> parameterMap = genericTypecast(this.servletRequest.getParameterMap());
    for (String parameterName : parameterMap.keySet())
    {
      if (parameterName.startsWith(crudParameterPrefix))
      {
	String crudParameterName = parameterName.substring(crudParameterPrefix.length());
	// note that multi-value parameters are used for adding and removing from to-many sets
	String[] v = parameterMap.get(parameterName);
	// weed out empty strings -- this really should be a proper translation from form parameters to sanitised commands for the session bean
	boolean containsBlank = false;
	for (String s : v)
	{
	  containsBlank = (s == null) || (s.length() == 0);
	  if (containsBlank)
	  {
	    break;
	  }
	}
	if (!containsBlank)
	{
	  crudParameterMap.put(crudParameterName, v);
	}
      }
    }
    return (crudParameterMap);
  }


  public void updateEntity() throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException
  {
    this.LOG.info("updating entity");
    Map<String, String[]> crudParameterMap = this.getCrudParameterMap();
    Integer id = null;
    if (this.entityId != null)
    {
      id = new Integer(Integer.parseInt(this.entityId));
    }
    this.access.updateEntity(this.findEntityClass(), id, crudParameterMap);
  }


  public void deleteEntity() throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException
  {
    if (this.entityClassName == null)
    {
      this.LOG.error("trying to delete an entity but no class name specified");
      return;
    }
    if (this.entityId == null)
    {
      this.LOG.error(String.format("trying to delete an entity of class %s without specifying an id", this.entityClassName));
      return;
    }
    Integer id = new Integer(Integer.parseInt(this.entityId));
    this.access.removeEntity(this.findEntityClass(), id);
  }


  public String execute() throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException
  {
    if ((this.crudOp != null) && this.crudOp.equals("update"))
    {
      this.updateEntity();
    }
    else if ((this.crudOp != null) && this.crudOp.equals("delete"))
    {
      this.deleteEntity();
    }
    return (SUCCESS);
  }
}
