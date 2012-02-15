package org.isacrodi.struts;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletContext;

import javax.naming.NamingException;
import javax.naming.InitialContext;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.dispatcher.SessionMap;
import org.apache.struts2.util.ServletContextAware;

import org.javamisc.jee.entitycrud.EntityAccess;

import org.isacrodi.util.EjbSupport;

import org.isacrodi.ejb.session.*;
import org.isacrodi.ejb.entity.*;


/**
 * Base class for Struts action classes.
 *
 * <p>This class provides the stateless session beans interfacing to
 * the EJB tier.</p>
 *
 * <p>This class also is to provide mechanics for user identification
 * (not yet implemented).</p>
 */
public abstract class IsacrodiActionSupport extends ActionSupport implements SessionAware, ServletRequestAware, ServletContextAware
{
  public static final String ISACRODIUSER_SESSIONKEY = "isacrodiUser";

  // session bean members go here
  protected Map<String, Object> sessionMap;
  protected HttpServletRequest servletRequest;
  protected ServletContext servletContext;
  protected IsacrodiUser isacrodiUser;
  protected UserHandler userHandler;
  protected Access access;
  protected EntityAccess entityAccess;


  public IsacrodiActionSupport() throws NamingException
  {
    InitialContext context = new InitialContext();
    this.userHandler = (UserHandler) context.lookup(EjbSupport.sessionJndiName(UserHandlerBean.class, UserHandler.class));
    this.access = (Access) context.lookup(EjbSupport.sessionJndiName(AccessBean.class, Access.class));
    this.entityAccess = (EntityAccess) context.lookup(EjbSupport.sessionJndiName(EntityAccessBean.class, EntityAccess.class));
  }


  @Override
  public void setSession(Map<String, Object> sessionMap)
  {
    this.sessionMap = sessionMap;
    this.isacrodiUser = (IsacrodiUser) sessionMap.get("isacrodiUser");
  }


  public Map<String, Object> getSession()
  {
    return (this.sessionMap);
  }


  public void setServletRequest(HttpServletRequest servletRequest)
  {
    this.servletRequest = servletRequest;
  }


  public HttpServletRequest getServletRequest()
  {
    return (this.servletRequest);
  }


  public void setServletContext(ServletContext servletContext)
  {
    this.servletContext = servletContext;
  }


  public ServletContext getServletContext()
  {
    return (this.servletContext);
  }


  public IsacrodiUser getIsacrodiUser()
  {
    return (this.isacrodiUser);
  }


  public String getInstallationType()
  {
    String installationType = "unconfigured";
    if (this.servletContext != null)
    {
      installationType = this.servletContext.getInitParameter("installationType");
      if (installationType == null)
      {
	installationType = "xunconfigured";
      }
    }
    return (installationType);
  }


  /**
   * Log in a user.
   *
   * <p>Notice that this method does <strong>not</strong> perform any
   * authentication. The user is logged in unconditionally. It is the
   * caller's responsibility to check for authentication before
   * calling this method.
   *
   * @param isacrodiUser the user to be logged in
   */
  public void loginUser(IsacrodiUser isacrodiUser)
  {
    this.sessionMap.put(ISACRODIUSER_SESSIONKEY, isacrodiUser);
    this.isacrodiUser = isacrodiUser;
    // this.LOG.info(String.format("IsacrodiActionSupport.loginUser: logged in user %s, version %s", isacrodiUser.getEmail(), isacrodiUser.getVersion()));
  }


  public void logoutUser()
  {
    this.sessionMap.remove("isacrodiUser");
  }
}
