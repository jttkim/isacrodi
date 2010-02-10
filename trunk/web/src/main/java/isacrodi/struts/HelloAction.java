package isacrodi.struts;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ejb.EJB;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.struts.action.*; 
import isacrodi.ejb.session.Hello;


public class HelloAction extends Action 
{
	Hello helloEjb;

	public HelloAction() throws NamingException 
	{
		Context ctx = new InitialContext();
		helloEjb = (Hello) ctx.lookup("HelloBeanLocal");
	}
	
	public ActionForward execute(ActionMapping map, ActionForm form, HttpServletRequest req, HttpServletResponse resp)
		throws IOException, ServletException 
		{
			HelloForm f = (HelloForm) form;
			String message = helloEjb.sayHello(f.getName());
			req.setAttribute("message", message);
			return map.findForward("hello");
		}
}

