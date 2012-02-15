package org.isacrodi.util;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;


/**
 * Utilities to support working with EJBs.
 */
public class EjbSupport
{
  /**
   * Generates a name for looking up a business interface in the {@code ejb:} namespace of JBoss AS7.
   *
   * Based on "EJB invocations from a remote client using JNDI", <code>https://docs.jboss.org/author/display/AS71/EJB+invocations+from+a+remote+client+using+JNDI</code>.
   * @param appName the application name
   * @param moduleName the name of the module (within the app)
   * @param distinctName the distinct name
   * @param beanName the name of the bean
   * @param interfaceName the name of the business interface
   * @return the JNDI name
   */
  public static String sessionJndiName(String appName, String moduleName, String distinctName, String beanName, String interfaceName)
  {
    return (String.format("ejb:%s/%s/%s/%s!%s", appName, moduleName, distinctName, beanName, interfaceName));
  }


  /**
   * Generates a name for looking up a business interface in the {@code ejb:} namespace of JBoss AS7.
   *
   * This is a convenience shorthand that fills in the default app
   * name, module name and distinct name for Isacrodi. See
   * {@link sessionJndiName(String, String, String, String, String)} for details.
   * @param beanName the name of the bean
   * @param interfaceName the name of the business interface
   * @return the JNDI name
   */
  public static String sessionJndiName(String beanName, String interfaceName)
  {
    return (sessionJndiName("isacrodi", "isacrodi-ejb", "", beanName, interfaceName));
  }


  /**
   * Generates a name for looking up a business interface in the {@code ejb:} namespace of JBoss AS7.
   *
   * This is a convenience shorthand that fills in the default app
   * name, module name and distinct name for Isacrodi. See
   * {@link #sessionJndiName(String, String, String, String, String)} for details.
   * @param beanClass the name of the bean
   * @param interfaceClass the name of the business interface
   * @return the JNDI name
   */
  public static String sessionJndiName(Class beanClass, Class interfaceClass)
  {
    return (sessionJndiName(beanClass.getSimpleName(), interfaceClass.getName()));
  }
}
