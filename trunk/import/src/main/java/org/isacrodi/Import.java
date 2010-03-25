package org.isacrodi;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

import javax.naming.*;


import org.isacrodi.ejb.entity.*;
import org.isacrodi.ejb.session.*;


public class Import
{
  private static final String[] headerUser = {"lastname", "firstname", "username", "password", "email"};


  private static boolean isHeader(String[] expected, String[] header)
  {
    if (expected.length != header.length)
    {
      return (false);
    }
    for (int i = 0; i < expected.length; i++)
    {
      if (!expected[i].equals(header[i]))
      {
	// System.err.printf("mismatch: actual %s != expected %s\n", header[i], expected[i]);
	return (false);
      }
    }
    return (true);
  }


  private static boolean isUserHeader(String[] header)
  {
    return (isHeader(headerUser, header));
  }


  private static void importUser(UserHandler userHandler, String lastname, String firstname, String username, String password, String email)
  {
    IsacrodiUser user = new IsacrodiUser(lastname, firstname, username, IsacrodiUser.hash(password), email);
    userHandler.insertUser(user);
  }


  private static void importUserFile(CsvTable csvTable) throws IOException, NamingException
  {
    InitialContext context = new InitialContext();
    UserHandler userHandler = (UserHandler) context.lookup("isacrodi/UserHandlerBean/remote");
    while (csvTable.next())
    {
      importUser(userHandler, csvTable.getString("lastname"), csvTable.getString("firstname"), csvTable.getString("username"), csvTable.getString("password"), csvTable.getString("email"));
    }

  }


  private static void importFile(String filename) throws IOException, NamingException
  {
    CsvTable csvTable = new CsvTable(new CsvReader(new BufferedReader(new FileReader(filename))));
    String[] header= csvTable.getColumnNameList();
    if (isUserHeader(header))
    {
      System.err.println(String.format("%s: user file", filename));
      importUserFile(csvTable);
    }
    else
    {
      throw new IOException(String.format("cannot identify table type of %s", filename));
    }
  }


  public static void main(String[] args) throws NamingException, IOException
  {
    for (String arg : args)
    {
      importFile(arg);
    }
  }
}
