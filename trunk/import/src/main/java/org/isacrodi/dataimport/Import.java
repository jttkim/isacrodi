package org.isacrodi.dataimport;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import libsvm.*;

import java.util.List;
import java.util.Iterator;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.javamisc.csv.CsvReader;
import org.javamisc.csv.CsvTable;

import org.isacrodi.ejb.entity.*;
import org.isacrodi.ejb.session.*;
import org.isacrodi.util.io.*;

// FIXME: wildcard imports
import java.util.*;
import org.isacrodi.diagnosis.*;


// FIXME: exceptions thrown by this class are a haphazard mess, util should provide exceptions for scanning and parsing
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
    Token cropToken = new Token(Token.TokenType.BLOCKIDENTIFIER, "crop");
    SimpleScanner s = new SimpleScanner(in);
    for (Token t = s.nextToken(); t != null; t = s.nextToken())
    {
      if (!cropToken.equals(t))
      {
	throw new IllegalStateException("no crop token");
      }
      s.nextToken(Token.TokenType.SYMBOL, "{");
      Token nameToken = s.nextToken(Token.TokenType.NAMEVALUE, "name");
      Token scientificNameToken = s.nextToken(Token.TokenType.NAMEVALUE, "scientificName");
      s.nextToken(Token.TokenType.SYMBOL, "}");
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
    Token disorderToken = new Token(Token.TokenType.BLOCKIDENTIFIER, "disorder");
    SimpleScanner s = new SimpleScanner(in);
    for (Token t = s.nextToken(); t != null; t = s.nextToken())
    {
      if (!disorderToken.equals(t))
      {
	throw new IllegalStateException("no disorder token");
      }
      s.nextToken(Token.TokenType.SYMBOL, "{");
      Token nameToken = s.nextToken(Token.TokenType.NAMEVALUE, "name");
      Token scientificNameToken = s.nextToken(Token.TokenType.NAMEVALUE, "scientificName");
      String scientificName = scientificNameToken.getValue();
      Token cropSet = s.nextToken(Token.TokenType.NAMEVALUE, "cropSet");
      s.nextToken(Token.TokenType.SYMBOL, "}");
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
    Token numericTypeToken = new Token(Token.TokenType.BLOCKIDENTIFIER, "numerictype");
    SimpleScanner s = new SimpleScanner(in);
    for (Token t = s.nextToken(); t != null; t = s.nextToken())
    {
      if (!numericTypeToken.equals(t))
      {
	throw new IllegalStateException("no numerictype token");
      }
      s.nextToken(Token.TokenType.SYMBOL, "{");
      String typename = s.nextToken(Token.TokenType.NAMEVALUE, "typename").getValue();
      s.nextToken(Token.TokenType.SYMBOL, "}");
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


  private static void importImageTypeFile(BufferedReader in, Access access) throws IOException, NamingException
  {
    Token imageTypeToken = new Token(Token.TokenType.BLOCKIDENTIFIER, "imagetype");
    SimpleScanner s = new SimpleScanner(in);
    for (Token t = s.nextToken(); t != null; t = s.nextToken())
    {
      if (!imageTypeToken.equals(t))
      {
	throw new IllegalStateException("no imagetype token");
      }
      s.nextToken(Token.TokenType.SYMBOL, "{");
      String typename = s.nextToken(Token.TokenType.NAMEVALUE, "typename").getValue();
      s.nextToken(Token.TokenType.SYMBOL, "}");
      if (access.findImageType(typename) != null)
      {
	System.err.println(String.format("image type \"%s\" already exists", typename));
      }
      else
      {
	ImageType imageType = new ImageType(typename);
	access.insert(imageType);
      }
    }
  }


  private static void importCropDisorderBlock(SimpleScanner s, Access access) throws IOException
  {
    Token endBlockToken = new Token(Token.TokenType.SYMBOL, "}");
    s.nextToken(Token.TokenType.SYMBOL, "{");
    Token userToken = s.nextToken(Token.TokenType.NAMEVALUE, "user");
    String username = userToken.getValue();
    Token cropToken = s.nextToken(Token.TokenType.NAMEVALUE, "crop");
    String cropScientificName = cropToken.getValue();
    CropDisorderRecord cdr = new CropDisorderRecord();
    s.nextToken(Token.TokenType.BLOCKIDENTIFIER, "numericDescriptors");
    s.nextToken(Token.TokenType.SYMBOL, "{");
    for (Token t = s.nextToken(); !endBlockToken.equals(t); t = s.nextToken())
    {
      if (t == null)
      {
	throw new IllegalStateException("unexpected EOF");
      }
      if (t.getTokenType() != Token.TokenType.NAMEVALUE)
      {
	throw new IllegalStateException("unexpected token type: " + t);
      }
      NumericType numericType = access.findNumericType(t.getName());
      if (numericType == null)
      {
	throw new IOException(String.format("no such numeric type: %s", t.toString()));
      }
      double v = Double.parseDouble(t.getValue());
      // System.err.println(String.format("value: \"%s\", double: %f", t.getValue(), v));
      NumericDescriptor d = new NumericDescriptor(numericType, v);
      // System.err.println(d);
      cdr.linkDescriptor(d);
    }
    // s.nextToken(Token.TokenType.SYMBOL, "}");
    s.nextToken(Token.TokenType.BLOCKIDENTIFIER, "imageDescriptors");
    s.nextToken(Token.TokenType.SYMBOL, "{");
    for (Token t = s.nextToken(); !endBlockToken.equals(t); t = s.nextToken())
    {
      if (t == null)
      {
	throw new IllegalStateException("unexpected EOF");
      }
      if (t.getTokenType() != Token.TokenType.BLOCKIDENTIFIER)
      {
	throw new IllegalStateException("unknown token type: " + t);
      }
      ImageType imageType = access.findImageType(t.getName());
      if (imageType == null)
      {
	throw new IOException(String.format("no such image type: %s", t.toString()));
      }
      s.nextToken(Token.TokenType.SYMBOL, "{");
      Token mimeToken = s.nextToken(Token.TokenType.NAMEVALUE, "mimeType");
      Token fileToken = s.nextToken(Token.TokenType.NAMEVALUE, "file");
      ImageDescriptor d = new ImageDescriptor(imageType, mimeToken.getValue(), fileToken.getValue());
      cdr.linkDescriptor(d);
      s.nextToken(Token.TokenType.SYMBOL, "}");
    }
    s.nextToken(Token.TokenType.SYMBOL, "}");
    access.insert(cdr, username, cropScientificName);
  }


  private static void importCropDisorderRecordFile(BufferedReader in, Access access) throws IOException, NamingException
  {
    Token cdrToken = new Token(Token.TokenType.BLOCKIDENTIFIER, "cdr");
    SimpleScanner s = new SimpleScanner(in);
    // t will be null at EOF, so can't just expect a cdr token
    for (Token t = s.nextToken(); t != null; t = s.nextToken())
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
    else if (magic.equals("isacrodi-imagetypes-0.1"))
    {
      importImageTypeFile(in, access);
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
      System.err.println("usage: import <file> [<file>]* or import -r ... (check code for parameters)");
    }
    if (args[0].equals("-r"))
    {
      int rndseed = Integer.parseInt(args[1]);
      int numNumericTypes = Integer.parseInt(args[2]);
      int numCrops = Integer.parseInt(args[3]);
      int numCropDisorders = Integer.parseInt(args[4]);
      int numCDRs = Integer.parseInt(args[5]);
      int numDisorderAssociations = Integer.parseInt(args[6]);
      double numericDescriptorPercentage = Double.parseDouble(args[7]);
      double stddevBetween = Double.parseDouble(args[8]);
      double stddevWithin = Double.parseDouble(args[9]);
      InitialContext context = new InitialContext();
      Kludge kludge = (Kludge) context.lookup("isacrodi/KludgeBean/remote");
      kludge.makeRandomExpertDiagnosedCDRs(rndseed, numNumericTypes, numCrops, numCropDisorders, numCDRs, numDisorderAssociations, numericDescriptorPercentage, stddevBetween, stddevWithin);
    }
    else if (args[0].equals("-x"))
    {
      /* jtk: inactivated experimental code -- parsing of feature vector mapper no longer necessary
      String model_filename = "ejbs/src/test/java/org/isacrodi/diagnosis/isacrodi_model";
      String parse_filename = "ejbs/src/test/java/org/isacrodi/diagnosis/isacrodi_feature_mapper_v1.txt";
      InitialContext context = new InitialContext();
      CropDisorderRecordManager cdrm = (CropDisorderRecordManager) context.lookup("isacrodi/CropDisorderRecordManagerBean/remote");
      HashMap<String, FeatureVector> labelledFeatureVectorMap = new HashMap<String, FeatureVector>();
      DummyCDRFeatureExtractor extractor = new DummyCDRFeatureExtractor();
      List<CropDisorderRecord> cdrList = cdrm.findExpertDiagnosedCropDisorderRecordList();
      CropDisorderRecord cdro = null;

      for (CropDisorderRecord cdr : cdrList)
      {
	System.err.println(String.format("cdr #%d: crop: %s", cdr.getId().intValue(), cdr.getCrop().getName()));
	System.err.println("  " + cdr.getExpertDiagnosedCropDisorder().getScientificName());
	labelledFeatureVectorMap.put(cdr.getExpertDiagnosedCropDisorder().getScientificName(), extractor.extract(cdr));
	cdro = cdr;
      }

      for (String label : labelledFeatureVectorMap.keySet())
      {
	System.err.println(String.format("%s: %s", label, labelledFeatureVectorMap.get(label).toString()));
      }

      SVMDiagnosisProvider svmdp = new SVMDiagnosisProvider(model_filename, parse_filename);
      svmdp.train(cdrList);

      Diagnosis diagnosis = svmdp.diagnose(cdrm.findCropDisorderRecord(Integer.parseInt(args[1])));
      Kludge kludge = (Kludge) context.lookup("isacrodi/KludgeBean/remote");

      for (int i = 1; i < args.length; i++)
      {
	Integer cdrId = new Integer(Integer.parseInt(args[i]));
	kludge.concoctDiagnosis(cdrId);
      }
      */
    }
    else
    {
      for (String arg : args)
      {
	importFile(arg);
      }
    }
  }
}
