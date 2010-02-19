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
  private String email;
  private String password;
  private IsacrodiUser authenticatedUser;

  public static final long serialVersionUID = 1;


  public LoginAction() throws NamingException
  {
    super();
  }


  public void setEmail(String email)
  {
    // this.LOG.info(String.format("LoginAction.setEmail(%s)", email));
    this.email = email;
  }


  public String getEmail()
  {
    return (this.email);
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
    this.LOG.info(String.format("LoginAction.execute: logging in user %s, version %s", this.authenticatedUser.getEmail(), this.authenticatedUser.getVersion()));
    this.loginUser(this.authenticatedUser);
    return (SUCCESS);
  }


  public void validate()
  {
    // this.LOG.info("LoginAction: validate starts");
    this.authenticatedUser = null;
    if ((this.email == null) || (this.email.length() == 0))
    {
      this.addFieldError("email", "No user ID given");
    }
    if ((this.password == null) || (this.password.length() == 0))
    {
      this.addFieldError("password", "No password given");
    }
    if (this.hasErrors())
    {
      return;
    }
    IsacrodiUser user = new IsacrodiUser("World", "Hello", "helloworld", IsacrodiUser.hash("secret"), this.email); //this.queryPerformer.findIsacrodiUser(this.email);
    if (user == null)
    {
      this.LOG.info(String.format("no user %s", this.email));
    }
    else
    {
      // this.LOG.info(String.format("found user %s, version %d", user.getEmail(), user.getVersion()));
      if (user.checkPassword(this.password))
      {
	this.authenticatedUser = user;
      }
      else
      {
	this.addActionError("User ID and password do not match");
      }
    }
  }
}
