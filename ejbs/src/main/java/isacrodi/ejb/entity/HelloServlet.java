package isacrodi.ejb.entity;

import java.io.PrintWriter;
import java.io.IOException;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.naming.InitialContext;

import isacrodi.ejb.entity.HelloEntity;
import isacrodi.ejb.entity.HelloSession;
import isacrodi.ejb.entity.HelloSessionBean;


public class HelloServlet extends HttpServlet
{
  private static final long serialVersionUID = 1;


  public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
  {
    String hello = request.getParameter("hello");
    if (hello == null)
    {
      hello = "world";
    }
    response.setContentType("text/html");
    PrintWriter out = response.getWriter();
    out.println("<html>");
    out.println("<head>");
    out.println("<title>hello</title>");
    out.println("</head>");
    out.println("<body>");
    out.println("<h1>Hello</h1>");
    out.println("<hr/>");
    out.println("<form method=\"POST\" action=\"hello\">");
    out.println("Hello");
    out.println("<input name=\"hello\"/>");
    out.println("<br/>");
    out.println("<input type=\"submit\"/>");
    out.println("</form>");
    out.println("<hr/>");
    try
    {
      InitialContext context = new InitialContext();
      HelloSession helloSession = (HelloSession) context.lookup("hello/HelloSessionBean/remote");
      helloSession.doHello(hello);
      out.println("<p>Hello " + helloSession.getHello() + "</p>");
    }
    catch (NamingException e)
    {
      out.println("<p><strong>caught <code>NamingException</code></strong</p>");
      out.println("<p>" + e.getMessage() + "</p>");
    }
    out.println("</body>");
    out.println("</html>");
  }


  public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
  {
    this.doGet(request, response);
  }
}
