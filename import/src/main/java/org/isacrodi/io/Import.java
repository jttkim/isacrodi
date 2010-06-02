package org.isacrodi.io;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

import java.util.Iterator;

import org.json.JSONObject;
import org.json.JSONTokener;

import javax.naming.*;


import org.isacrodi.ejb.entity.*;
import org.isacrodi.ejb.session.*;


public class Import
{
  private static final String[] headerUser = {"lastname", "firstname", "username", "password", "email"};


  private static void importUser(UserHandler userHandler, String lastname, String firstname, String username, String password, String email)
  {
    if (userHandler.findUser(username) != null)
    {
      System.err.println(String.format("user \"%s\" already exists", username));
      return;
    }
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


  private static void importCropFile(BufferedReader in, Access access) throws IOException, NamingException
  {
    ImportToken cropToken = new ImportToken(ImportToken.TokenType.BLOCKIDENTIFIER, "crop");
    ImportScanner s = new ImportScanner(in);
    for (ImportToken t = s.nextToken(); t != null; t = s.nextToken())
    {
      if (!cropToken.equals(t))
      {
	throw new IllegalStateException("no crop token");
      }
      s.nextToken(ImportToken.TokenType.SYMBOL, "{");
      ImportToken nameToken = s.nextToken(ImportToken.TokenType.NAMEVALUE, "name");
      ImportToken scientificNameToken = s.nextToken(ImportToken.TokenType.NAMEVALUE, "scientificName");
      s.nextToken(ImportToken.TokenType.SYMBOL, "}");
      String scientificName = scientificNameToken.getValue();
      if (access.findCrop(scientificName) != null)
      {
	System.err.println(String.format("crop \"%s\" already exists", scientificName));
	return;
      }
      Crop crop = new Crop(nameToken.getValue(), scientificName);
      access.insert(crop);
    }
  }


  private static void importDisorderFile(BufferedReader in, Access access) throws IOException, NamingException
  {
    ImportToken disorderToken = new ImportToken(ImportToken.TokenType.BLOCKIDENTIFIER, "disorder");
    ImportScanner s = new ImportScanner(in);
    for (ImportToken t = s.nextToken(); t != null; t = s.nextToken())
    {
      if (!disorderToken.equals(t))
      {
	throw new IllegalStateException("no disorder token");
      }
      s.nextToken(ImportToken.TokenType.SYMBOL, "{");
      ImportToken nameToken = s.nextToken(ImportToken.TokenType.NAMEVALUE, "name");
      ImportToken scientificNameToken = s.nextToken(ImportToken.TokenType.NAMEVALUE, "scientificName");
      String scientificName = scientificNameToken.getValue();
      ImportToken cropSet = s.nextToken(ImportToken.TokenType.NAMEVALUE, "cropSet");
      s.nextToken(ImportToken.TokenType.SYMBOL, "}");
      if (access.findCropDisorder(scientificName) != null)
      {
	System.err.println(String.format("crop disorder \"%s\" already exists", scientificName));
	return;
      }
      String[] csSplit = cropSet.getValue().split(",");
      for (int i = 0; i < csSplit.length; i++)
      {
	csSplit[i] = csSplit[i].trim();
      }
      CropDisorder cropDisorder = new CropDisorder(nameToken.getValue(), scientificName);
      access.insert(cropDisorder, csSplit);
    }
  }


  private static void importNumericTypeFile(BufferedReader in, Access access) throws IOException, NamingException
  {
    ImportToken numericTypeToken = new ImportToken(ImportToken.TokenType.BLOCKIDENTIFIER, "numerictype");
    ImportScanner s = new ImportScanner(in);
    for (ImportToken t = s.nextToken(); t != null; t = s.nextToken())
    {
      if (!numericTypeToken.equals(t))
      {
	throw new IllegalStateException("no numerictype token");
      }
      s.nextToken(ImportToken.TokenType.SYMBOL, "{");
      String typename = s.nextToken(ImportToken.TokenType.NAMEVALUE, "typename").getValue();
      s.nextToken(ImportToken.TokenType.SYMBOL, "}");
      if (access.findNumericType(typename) != null)
      {
	System.err.println(String.format("numeric type \"%s\" already exists", typename));
      }
      else
      {
	NumericType numericType = new NumericType(typename);
	access.insert(numericType);
      }
    }
  }


  private static void importCropDisorderBlock(ImportScanner s, Access access) throws IOException
  {
    ImportToken endBlockToken = new ImportToken(ImportToken.TokenType.SYMBOL, "}");
    s.nextToken(ImportToken.TokenType.SYMBOL, "{");
    ImportToken userToken = s.nextToken(ImportToken.TokenType.NAMEVALUE, "user");
    String username = userToken.getValue();
    ImportToken cropToken = s.nextToken(ImportToken.TokenType.NAMEVALUE, "crop");
    String cropScientificName = cropToken.getValue();
    CropDisorderRecord cdr = new CropDisorderRecord();
    s.nextToken(ImportToken.TokenType.BLOCKIDENTIFIER, "numericDescriptors");
    s.nextToken(ImportToken.TokenType.SYMBOL, "{");
    for (ImportToken t = s.nextToken(); !endBlockToken.equals(t); t = s.nextToken())
    {
      if (t == null)
      {
	throw new IllegalStateException("unexpected EOF");
      }
      if (t.getTokenType() != ImportToken.TokenType.NAMEVALUE)
      {
	throw new IllegalStateException("unexpected token type: " + t);
      }
      NumericType numericType = access.findNumericType(t.getName());
      double v = Double.parseDouble(t.getValue());
      System.err.println(String.format("value: \"%s\", double: %f", t.getValue(), v));
      NumericDescriptor d = new NumericDescriptor(numericType, v);
      System.err.println(d);
      cdr.addDescriptor(d);
      d.setCropDisorderRecord(cdr);
    }
    s.nextToken(ImportToken.TokenType.SYMBOL, "}");
    access.insert(cdr, username, cropScientificName);
  }


  private static void importCropDisorderRecordFile(BufferedReader in, Access access) throws IOException, NamingException
  {
    ImportToken cdrToken = new ImportToken(ImportToken.TokenType.BLOCKIDENTIFIER, "cdr");
    ImportScanner s = new ImportScanner(in);
    // t will be null at EOF, so can't just expect a cdr token
    for (ImportToken t = s.nextToken(); t != null; t = s.nextToken())
    {
      if (!cdrToken.equals(t))
      {
	throw new IllegalStateException("no cdr token");
      }
      importCropDisorderBlock(s, access);
    }
  }


  private static void importFile(String filename) throws IOException, NamingException
  {
    InitialContext context = new InitialContext();
    Access access = (Access) context.lookup("isacrodi/AccessBean/remote");
    BufferedReader in = new BufferedReader(new FileReader(filename));
    /*
    JSONTokener jsonTokener = new JSONTokener(new FileReader(filename));
    JSONObject jsonObject = new JSONObject(jsonTokener);
    if (jsonObject.length() != 1)
    {
      throw new IllegalArgumentException(String.format("top level JSON object has %d pairs, expected 1", jsonObject.length()));
    }
    Iterator<String> i = (Iterator<String>) jsonObject.keys();
    String contentKey = i.next();
    */

    String magic = in.readLine();
    if (magic.equals("isacrodi-users-0.1"))
    {
      CsvTable csvTable = new CsvTable(new CsvReader(in));
      String[] header= csvTable.getColumnNameList();
      System.err.println(String.format("%s: user file", filename));
      importUserFile(csvTable);
    }
    else if (magic.equals("isacrodi-crop-0.1"))
    {
      importCropFile(in, access);
    }
    else if (magic.equals("isacrodi-disorders-0.1"))
    {
      importDisorderFile(in, access);
    }
    else if (magic.equals("isacrodi-numerictypes-0.1"))
    {
      importNumericTypeFile(in, access);
    }
    else if (magic.equals("isacrodi-cdrs-0.1"))
    {
      importCropDisorderRecordFile(in, access);
    }
    else
    {
      throw new IOException(String.format("cannot identify table type of %s: unknown magic \"%s\"", filename, magic));
    }
  }


  public static void main(String[] args) throws NamingException, IOException
  {
    if (args.length == 0)
    {
      System.err.println("usage: import <file> [<file>]*");
    }
    for (String arg : args)
    {
      importFile(arg);
    }
  }
}
