package isacrodi.struts;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import javax.naming.NamingException;
import javax.naming.InitialContext;
import javax.naming.Context;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ActionContext;
import org.apache.struts2.interceptor.SessionAware;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.dispatcher.SessionMap;

import isacrodi.ejb.session.*;


public class HelloAction extends ActionSupport
{
  String name;
  Hello helloEjb;


  public HelloAction() throws NamingException 
  {
    Context ctx = new InitialContext();
    this.helloEjb = (Hello) ctx.lookup("isacrodi/HelloBean/local");
    this.LOG.info("got hello EJB");
  }


  public void setName(String name)
  {
    this.name = name;
  }


  public String getName()
  {
    return (this.name);
  }


  public String execute()
  {
    return (SUCCESS);
  }


  public String getEjbHello()
  {
    if (this.name == null)
    {
      return (this.helloEjb.sayHello("anonymous coward"));
    }
    else
    {
      return (this.helloEjb.sayHello(this.name));
    }
  }


  public void validate()
  {
    if (this.name.equalsIgnoreCase("hello") || this.name.equalsIgnoreCase("hola"))
    {
      this.LOG.info(String.format("got valid name: %s", this.name));
    }
    else
    {
      this.LOG.info(String.format("got invalid name: %s", this.name));
      this.addFieldError("name", "invalid 'hello' name (bad language?)");
    }
  }
}
