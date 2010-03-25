package org.isacrodi.struts;

import java.util.Map;

import javax.naming.NamingException;
import javax.naming.InitialContext;

import javax.servlet.http.HttpServletRequest;

import com.opensymphony.xwork2.ActionSupport;

import org.isacrodi.ejb.session.*;
import org.isacrodi.ejb.entity.*;


public class LoginAction extends IsacrodiActionSupport
{
  private String username;
  private String password;
  private IsacrodiUser authenticatedUser;

  public static final long serialVersionUID = 1;


  public LoginAction() throws NamingException
  {
    super();
  }


  public void setUsername(String username)
  {
    // this.LOG.info(String.format("LoginAction.setUsername(%s)", username));
    this.username = username;
  }


  public String getUsername()
  {
    return (this.username);
  }


  public void setPassword(String password)
  {
    // this.LOG.info(String.format("LoginAction.setPassword(%s)", password));
    this.password = password;
  }


  /**
   * Password accessor.
   *
   * <p>This returns the password in clear text, which is a security
   * risk. This method may be replaced with one that returns a
   * password hash.
   * </p>
   */
  public String getPassword()
  {
    return (this.password);
  }


  public String execute() throws NamingException, Exception
  {
    if (this.authenticatedUser == null)
    {
      this.LOG.error("LoginAction.execute: no authenticated user");
      this.logoutUser();
      return (ERROR);
    }
    this.LOG.info(String.format("LoginAction.execute: logging in user %s, version %s", this.authenticatedUser.getUsername(), this.authenticatedUser.getVersion()));
    this.loginUser(this.authenticatedUser);
    return (SUCCESS);
  }


  public void validate()
  {
    // this.LOG.info("LoginAction: validate starts");
    this.authenticatedUser = null;
    if ((this.username == null) || (this.username.length() == 0))
    {
      this.addFieldError("username", "No user ID given");
    }
    if ((this.password == null) || (this.password.length() == 0))
    {
      this.addFieldError("password", "No password given");
    }
    if (this.hasErrors())
    {
      return;
    }
    IsacrodiUser user = this.userHandler.authenticate(this.username, this.password);
    if (user != null)
    {
      this.authenticatedUser = user;
    }
    else
    {
      this.addActionError("User ID and password do not match");
    }
  }
}
