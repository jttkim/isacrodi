package isacrodi.struts;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import javax.naming.NamingException;
import javax.naming.InitialContext;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ActionContext;
import org.apache.struts2.interceptor.SessionAware;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.dispatcher.SessionMap;

import isacrodi.ejb.session.*;
import isacrodi.ejb.entity.*;


/**
 * Base class for Struts action classes.
 *
 * <p>This class provides the stateless session beans interfacing to
 * the EJB tier.</p>
 *
 * <p>This class also is to provide mechanics for user identification
 * (not yet implemented).</p>
 */
public abstract class IsacrodiActionSupport extends ActionSupport implements SessionAware, ServletRequestAware
{
  public static final String ISACRODIUSER_SESSIONKEY = "isacrodiUser";

  // session bean members go here
  protected Map<String, Object> sessionMap;
  protected HttpServletRequest servletRequest;
  protected IsacrodiUser isacrodiUser;


  public IsacrodiActionSupport() throws NamingException
  {
    InitialContext context = new InitialContext();
    // obtain session beans, example:
    // this.queryPerformer = (QueryPerformer) context.lookup("isacrodi/QueryPerformerBean/remote");
  }

  //@Override
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


  public IsacrodiUser getIsacrodiUser()
  {
    return (this.isacrodiUser);
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
