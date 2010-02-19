package isacrodi.ejb.session;

import javax.ejb.Stateless;
import javax.ejb.Local;

@Stateless
@Local({Hello.class})
public class HelloBean implements Hello 
{
  public HelloBean() 
  {
  }

  public String sayHello(String name) 
  {
    return "Hello " + name + "!";
  }

}

