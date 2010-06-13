package org.isacrodi.util;

import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;


/**
 * Utilities for use throughout the Isacrodi system.
 */
public class Util
{
  /**
   * Util is not to be instantiated.
   */
  private Util()
  {
  }


  /**
   * Construct an instance of a class specified by a name.
   *
   * @param className the canonical name of the class to be instantiated
   * @return an instance of the class, obtained by invoking the parameterless constructor of the class
   */
  public static Object constructDefaultInstance(String className) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException
  {
    Class<?> c = Class.forName(className);
    Constructor defaultConstructor = c.getConstructor();
    Object o = defaultConstructor.newInstance();
    return (o);
  }


  /**
   * Extract the name of a property from the name of an accessor or mutator method.
   *
   * @param methodName the name of the accessor or mutator method
   * @return the name of the property
   */
  public static String extractPropertyName(String methodName)
  {
    int i = 0;
    if (methodName.startsWith("get") || methodName.startsWith("set"))
    {
      i = 3;
    }
    else if (methodName.startsWith("is"))
    {
      i = 2;
    }
    else
    {
      throw new IllegalArgumentException(String.format("not an accessor or mutator name: %s", methodName));
    }
    String propertyName = methodName.substring(i, i + 1).toLowerCase() + methodName.substring(i + 1);
    return (propertyName);
  }


  /**
   * Compute the accessor name corresponding to a property.
   *
   * <p>This method prefixes the property name with {@code get} and
   * converts the first character of the property to upper case. There
   * is no provision for boolean accessors prefixed with {@code is}.</p>
   *
   * @param propertyName the name of the property
   @ return the corresponding accessor name
   */
  public static String makeAccessorName(String propertyName)
  {
    // FIXME: cannot generate isSomething style boolean accessors
    return ("get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1));
  }


  /**
   * Compute the mutator name corresponding to a property.
   *
   * <p>This method prefixes the property name with {@code set} and
   * converts the first character of the property to upper case.</p>
   *
   * @param propertyName the name of the property
   @ return the corresponding mutator name
   */
  public static String makeMutatorName(String propertyName)
  {
    return ("set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1));
  }


  /**
   * Determine whether a method is an accessor method.
   *
   * <p>Currently checks are (1) whether the method's name starts with
   * {@code get} and (2) whether the method takes no parameters.</p>
   *
   * @param method the method to be checked
   * @return {@code true} if the method is considered to be an accessor method.
   */
  public static boolean isAccessor(Method method)
  {
    String methodName = method.getName();
    if (!methodName.startsWith("get") && !methodName.startsWith("is"))
    {
      return (false);
    }
    Class parameterTypes[] = method.getParameterTypes();
    if (parameterTypes.length > 0)
    {
      return (false);
    }
    else
    {
      return (true);
    }
  }


  /**
   * Determine whether a class is an entity class.
   *
   * <p>This method relies on the {@code javax.persistence.Entity} annotation.</p>
   *
   * @param aClass the class to be checked
   * @return {@code true} if the class is an entity class
   */
  public static boolean isEntityClass(Class<?> aClass)
  {
    return (aClass.getAnnotation(javax.persistence.Entity.class) != null);
  }


  /**
   * Determine whether an object is an instance of an entity class.
   *
   * @param obj the object to be checked
   * @return {@code true} if the object is an entity instance
   */
  public static boolean isEntityInstance(Object obj)
  {
    return (isEntityClass(obj.getClass()));
  }


  /**
   * Get a property from an entity (or generally a bean like object).
   *
   * @param object the object to get the property from
   * @param propertyName the name of the property to get
   * @return the property's value
   */
  public static Object getProperty(Object entity, String propertyName) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
  {
    String accessorName = makeAccessorName(propertyName);
    Class<?> entityClass = entity.getClass();
    Method accessorMethod = entityClass.getMethod(accessorName);
    Object property = accessorMethod.invoke(entity);
    return (property);
  }


  /**
   * Set a property on an entity (or generally a bean like object).
   *
   * @param object the object to get the property from
   * @param propertyName the name of the property to get
   * @param propertyValue the value to set
   */
  public static void setProperty(Object entity, String propertyName, Object propertyValue) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
  {
    String mutatorName = makeMutatorName(propertyName);
    Class<?> entityClass = entity.getClass();
    Class<?> valueClass = propertyValue.getClass();
    Method mutatorMethod = entityClass.getMethod(mutatorName, valueClass);
    // FIXME: ignoring the return value, assuming method returns void
    mutatorMethod.invoke(entity, propertyValue);
  }


  /**
   * Helper method to cast values to parameterised types without
   * triggering an "unchecked" warning.
   *
   * <p>Idea found at
   * <a href="http://weblogs.java.net/blog/emcmanus/archive/2007/03/getting_rid_of.html">Eammon
   * McManus's blog</a>
   * </p>
   */
  @SuppressWarnings("unchecked")
  public static <T> T genericTypecast(Object o)
  {
    return ((T) o);
  }
}
