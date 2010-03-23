package org.isacrodi;

import javax.naming.*;


import org.isacrodi.ejb.entity.*;
import org.isacrodi.ejb.session.*;


public class Import
{
  public static void main(String[] args) throws NamingException
  {
    IsacrodiUser jtk = new IsacrodiUser("Kim", "Jan", "jtk", IsacrodiUser.hash("blah"), "j.kim@uea.ac.uk");
    InitialContext context = new InitialContext();
    Object o = context.lookup("isacrodi/UserHandlerBean/remote");
    System.out.println(o);
    System.out.println(o.getClass());
    UserHandler userHandler = (UserHandler) o;
    userHandler.insertUser(jtk);
  }
}
