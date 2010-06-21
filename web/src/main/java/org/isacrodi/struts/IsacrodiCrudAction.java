package org.isacrodi.struts;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import java.util.Collection;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.naming.NamingException;
import javax.naming.InitialContext;

import javax.servlet.http.HttpServletRequest;

import org.javamisc.jee.CrudAction;
import org.javamisc.jee.EntityAccess;

import org.isacrodi.ejb.session.*;
import org.isacrodi.ejb.entity.*;


public class IsacrodiCrudAction extends CrudAction
{
  private static final String packageName = "org.isacrodi.ejb.entity";
  private static final String[] hiddenPropertyNameList = {"class", "version"};
  private static final String[] readOnlyPropertyNameList = {"id"};
  private static final String crudParameterPrefix = "_crud_";


  public IsacrodiCrudAction() throws NamingException
  {
    super(lookupAccess());
  }


  private static EntityAccess lookupAccess() throws NamingException
  {
    InitialContext context = new InitialContext();
    EntityAccess entityAccess = (EntityAccess) context.lookup("isacrodi/EntityAccessBean/remote");
    return (entityAccess);
  }


  public static String canonicalEntityClassName(String entityClassName)
  {
    return (packageName + "." + entityClassName);
  }


  public Class<?> findEntityClass() throws ClassNotFoundException
  {
    if (this.entityClassName == null)
    {
      this.LOG.info("trying to find entity class without entity class name");
      return (null);
    }
    Class<?> entityClass = Class.forName(canonicalEntityClassName(this.entityClassName));
    if (!this.entityAccess.isEntityClass(entityClass))
    {
      throw new IllegalArgumentException(String.format("%s is not an entity class", this.entityClassName));
    }
    return (entityClass);
  }


  public boolean isHidden(Class<?> entityClass, String propertyName)
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


  public boolean isReadOnly(Class<?> entityClass, String propertyName)
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
}
