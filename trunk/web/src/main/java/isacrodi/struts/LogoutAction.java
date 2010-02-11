package isacrodi.struts;

import javax.naming.NamingException;


public class LogoutAction extends IsacrodiActionSupport
{
  public static final long serialVersionUID = 1;


  public LogoutAction() throws NamingException
  {
    super();
  }


  public String execute() throws NamingException, Exception
  {
    this.logoutUser();
    return (SUCCESS);
  }
}
