package org.isacrodi.struts;

import java.util.Map;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import org.apache.struts2.interceptor.SessionAware;
import org.apache.struts2.dispatcher.SessionMap;

import org.isacrodi.ejb.session.*;
import org.isacrodi.ejb.entity.*;


/**
 * A simple login interceptor that checks whether an object {@code
 * isacrodiUser} is in the session.
 *
 * The object is cast to {@code IsacrodiUser}, thus providing a
 * rudimentary sanity check.
 *
 * @author jtk
 */
public class SimpleLoginInterceptor extends AbstractInterceptor
{
  public static final long serialVersionUID = 1;


  public SimpleLoginInterceptor()
  {
    super();
  }


  public String intercept(ActionInvocation invocation) throws Exception
  {
    // System.err.println("LoginInterceptor.intercept started");
    ActionContext actionContext = invocation.getInvocationContext();
    Map<String, Object> sessionMap = actionContext.getSession();
    IsacrodiUser isacrodiUser = (IsacrodiUser) sessionMap.get("isacrodiUser");
    if (isacrodiUser == null)
    {
      // System.err.println("nobody logged in");
      return (Action.LOGIN);
    }
    else
    {
      // System.err.println(String.format("user %s (version %d) logged in", isacrodiUser.getEmail(), isacrodiUser.getVersion()));
    }
    return (invocation.invoke());
  }
}
