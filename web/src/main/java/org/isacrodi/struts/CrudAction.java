package org.isacrodi.struts;

import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.NamingException;
import javax.naming.InitialContext;

import javax.servlet.http.HttpServletRequest;

import org.isacrodi.ejb.session.*;
import org.isacrodi.ejb.entity.*;

import static org.isacrodi.util.Util.genericTypecast;
import static org.isacrodi.util.Util.extractPropertyName;
import static org.isacrodi.util.Util.isAccessor;
import static org.isacrodi.util.Util.getProperty;
import static org.isacrodi.util.Util.setProperty;
import static org.isacrodi.util.Util.isEntityClass;
import static org.isacrodi.util.Util.isEntityInstance;
import static org.isacrodi.util.Util.findUniquePropertyNameList;


public class CrudAction extends IsacrodiActionSupport
{
  private String entityClassName;
  private String entityId;
  private static String packageName = "org.isacrodi.ejb.entity";


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
    return (Class.forName(canonicalEntityClassName(this.entityClassName)));
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
    for (String propertyName : findUniquePropertyNameList(entity.getClass()))
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
    String s = "<ul>\n";
    for (Object entity : entityCollection)
    {
      s += String.format("<li>%s</li>\n", entityHtmlLink(entity));
    }
    s += "</ul>\n";
    return (s);
  }


  public static String entityHtmlTable(Object entity) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
  {
    String s = "<table>\n";
    Method[] methodList = entity.getClass().getMethods();
    for (Method method : methodList)
    {
      if (isAccessor(method))
      {
	String methodName = method.getName();
	String propertyName = extractPropertyName(methodName);
	Object property = method.invoke(entity);
	s += "<tr>";
	s += String.format("<td>%s</td>", htmlEscape(propertyName));
	s += "<td>";
	if (property == null)
	{
	  s += htmlEscape("<null>");
	}
	else if (property instanceof Collection<?>)
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
	s += "</td></tr>\n";
      }
    }
    s += "</table>\n";
    return (s);
  }


  public static String entityHtmlForm(Object entity) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
  {
    String s = "<form method=\"post\" action=\"crud\">\n";
    s += "<table>\n";
    Method[] methodList = entity.getClass().getMethods();
    for (Method method : methodList)
    {
      if (isAccessor(method))
      {
	String propertyName = extractPropertyName(method.getName());
	Object property = method.invoke(entity);
	s += "<tr>";
	s += String.format("<td>%s</td>", htmlEscape(propertyName));
	s += "<td>";
	if (property instanceof Collection<?>)
	{
	  Collection<?> collection = (Collection<?>) property;
	  s += entityHtmlLinkList(collection);
	}
	else if (isEntityInstance(property))
	{
	  s += entityHtmlLink(property);
	}
	else
	{
	  String propertyValue = "";
	  if (property != null)
	  {
	    propertyValue = property.toString();
	    s += String.format("<input name=\"%s\" value=\"%s\"/>", propertyName, propertyValue);
	  }
	}
	s += "</td></tr>\n";
      }
    }
    s += "<tr><td><input type=\"submit\"/></td><td></td></tr>\n";
    s += "</table>\n";
    s += "</form>\n";
    return (s);
  }


  public String entitySetHtmlList() throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
  {
    Class<?> entityClass = this.findEntityClass();
    List<?> entityList = this.access.findEntityList(entityClass);
    if (entityList.size() == 0)
    {
      return (String.format("<p>no entities of class <code>%s</code> found</p>", htmlEscape(entityClassName)));
    }
    return (entityHtmlLinkList(entityList));
  }


  public String getEntityHtml() throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
  {
    Object entity = this.retrieveEntity();
    if (entity != null)
    {
      return (entityHtmlTable(entity));
    }
    else if (this.entityClassName != null)
    {
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


  public String getEntityId()
  {
    this.LOG.info("returning entity id " + this.entityId);
    return (this.entityId);
  }


  public String execute()
  {
    return (SUCCESS);
  }
}
