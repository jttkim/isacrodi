package org.isacrodi.ejb.session;


/**
 * Utilities, intended for internal workings of the EJB layer.
 */
public class Util
{
  private Util()
  {
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
