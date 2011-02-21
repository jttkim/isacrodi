package org.isacrodi.dataimport;

import java.util.Random;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.IOException;
import libsvm.*;
import java.io.*;

import java.util.List;
import java.util.HashMap;
import java.util.Iterator;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.javamisc.Util;
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
      String scientificName = scientificNameToken.getValue().trim();
      if (access.findCrop(scientificName) != null)
      {
	System.err.println(String.format("crop \"%s\" already exists", scientificName));
	continue;
      }
      Crop crop = new Crop(nameToken.getValue().trim(), scientificName);
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
      String scientificName = scientificNameToken.getValue().trim();
      Token cropSet = s.nextToken(Token.TokenType.NAMEVALUE, "cropSet");
      s.nextToken(Token.TokenType.SYMBOL, "}");
      if (access.findCropDisorder(scientificName) != null)
      {
	System.err.println(String.format("crop disorder \"%s\" already exists", scientificName));
	continue;
      }
      String[] csSplit = cropSet.getValue().split(",");
      for (int i = 0; i < csSplit.length; i++)
      {
	csSplit[i] = csSplit[i].trim();
      }
      CropDisorder cropDisorder = new CropDisorder(nameToken.getValue().trim(), scientificName);
      access.insert(cropDisorder, csSplit);
    }
  }


  private static void importProcedureFile(BufferedReader in, Access access) throws IOException, NamingException
  {
    Token procedureToken = new Token(Token.TokenType.BLOCKIDENTIFIER, "procedure");
    SimpleScanner scanner = new SimpleScanner(in);
    for (Token t = scanner.nextToken(); t != null; t = scanner.nextToken())
    {
      if (!procedureToken.equals(t))
      {
	throw new IllegalStateException("no procedure token");
      }
      scanner.nextToken(Token.TokenType.SYMBOL, "{");
      Token nameToken = scanner.nextToken(Token.TokenType.NAMEVALUE, "name");
      Token toxicologicalClassToken = scanner.nextToken(Token.TokenType.NAMEVALUE, "toxicologicalClass");
      Token descriptionToken = scanner.nextToken(Token.TokenType.NAMEVALUE, "description");
      Token incompatibleProcSetToken = scanner.nextToken(Token.TokenType.NAMEVALUE, "incompatibleProcedureSet");
      Token disorderSetToken = scanner.nextToken(Token.TokenType.NAMEVALUE, "disorderSet");
      scanner.nextToken(Token.TokenType.SYMBOL, "}");
      String procedureName = nameToken.getValue().trim();
      if (access.findProcedure(procedureName) != null)
      {
	System.err.println(String.format("procedure \"%s\" already exists", procedureName));
	continue;
      }
      String[] icSplit = incompatibleProcSetToken.getValue().split(",");
      for (int i = 0; i < icSplit.length; i++)
      {
	icSplit[i] = icSplit[i].trim();
      }
      String[] dsSplit = disorderSetToken.getValue().split(",");
      for (int i = 0; i < dsSplit.length; i++)
      {
	dsSplit[i] = dsSplit[i].trim();
      }
      /*
      for (String s : icSplit)
      {
	System.err.printf("incompatible: \"%s\"\n", s);
      }
      for (String s : dsSplit)
      {
	System.err.printf("disorder: \"%s\"\n", s);
      }
      */
      Procedure procedure = new Procedure(procedureName, descriptionToken.getValue().trim());
      procedure.setToxicologicalClass(toxicologicalClassToken.getValue().trim());
      System.err.println(String.format("new procedure: %s", procedure.toString()));
      access.insert(procedure, icSplit, dsSplit);
    }
  }


  private static void importCategoricalTypeFile(BufferedReader in, Access access) throws IOException, NamingException
  {
    Token categoricalTypeToken = new Token(Token.TokenType.BLOCKIDENTIFIER, "categoricaltype");
    SimpleScanner scanner = new SimpleScanner(in);
    for (Token t = scanner.nextToken(); t != null; t = scanner.nextToken())
    {
      if (!categoricalTypeToken.equals(t))
      {
	throw new IllegalStateException("no categoricaltype token");
      }
      scanner.nextToken(Token.TokenType.SYMBOL, "{");
      String typename = scanner.nextToken(Token.TokenType.NAMEVALUE, "typename").getValue().trim();
      System.err.println(String.format("importing categorical type \"%s\"", typename));
      String description = scanner.nextToken(Token.TokenType.NAMEVALUE, "description").getValue().trim();
      String valueSetString = scanner.nextToken(Token.TokenType.NAMEVALUE, "valueSet").getValue().trim();
      String multipleString = scanner.nextToken(Token.TokenType.NAMEVALUE, "multiple").getValue().trim();
      scanner.nextToken(Token.TokenType.SYMBOL, "}");
      if (access.findCategoricalType(typename) != null)
      {
	System.err.println(String.format("categorical type \"%s\" already exists", typename));
      }
      else
      {
	String[] valueString = valueSetString.split(",");
	for (int i = 0; i < valueString.length; i++)
	{
	  valueString[i] = valueString[i].trim();
	}
	CategoricalType categoricalType = new CategoricalType(typename);
	categoricalType.setDescription(description);
	categoricalType.setMultivalue("true".equals(multipleString));
	access.insert(categoricalType, valueString);
      }
    }
  }


  private static void importNumericTypeFile(BufferedReader in, Access access) throws IOException, NamingException
  {
    Token numericTypeToken = new Token(Token.TokenType.BLOCKIDENTIFIER, "numerictype");
    SimpleScanner scanner = new SimpleScanner(in);
    for (Token t = scanner.nextToken(); t != null; t = scanner.nextToken())
    {
      if (!numericTypeToken.equals(t))
      {
	throw new IllegalStateException("no numerictype token");
      }
      scanner.nextToken(Token.TokenType.SYMBOL, "{");
      String typename = scanner.nextToken(Token.TokenType.NAMEVALUE, "typename").getValue().trim();
      String unit = scanner.nextToken(Token.TokenType.NAMEVALUE, "unit").getValue().trim();
      String description = scanner.nextToken(Token.TokenType.NAMEVALUE, "description").getValue().trim();
      scanner.nextToken(Token.TokenType.SYMBOL, "}");
      if (access.findNumericType(typename) != null)
      {
	System.err.println(String.format("numeric type \"%s\" already exists", typename));
      }
      else
      {
	NumericType numericType = new NumericType(typename, unit);
	numericType.setDescription(description);
	access.insert(numericType);
      }
    }
  }


  private static void importImageTypeFile(BufferedReader in, Access access) throws IOException, NamingException
  {
    Token imageTypeToken = new Token(Token.TokenType.BLOCKIDENTIFIER, "imagetype");
    SimpleScanner scanner = new SimpleScanner(in);
    for (Token t = scanner.nextToken(); t != null; t = scanner.nextToken())
    {
      if (!imageTypeToken.equals(t))
      {
	throw new IllegalStateException("no imagetype token");
      }
      scanner.nextToken(Token.TokenType.SYMBOL, "{");
      String typename = scanner.nextToken(Token.TokenType.NAMEVALUE, "typename").getValue().trim();
      scanner.nextToken(Token.TokenType.SYMBOL, "}");
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


  private static void importCropDisorderBlock(SimpleScanner scanner, Access access) throws IOException
  {
    Token endBlockToken = new Token(Token.TokenType.SYMBOL, "}");
    scanner.nextToken(Token.TokenType.SYMBOL, "{");
    Token userToken = scanner.nextToken(Token.TokenType.NAMEVALUE, "user");
    String username = userToken.getValue().trim();
    Token cropToken = scanner.nextToken(Token.TokenType.NAMEVALUE, "crop");
    String cropScientificName = cropToken.getValue().trim();
    scanner.nextToken(Token.TokenType.BLOCKIDENTIFIER, "numericDescriptors");
    scanner.nextToken(Token.TokenType.SYMBOL, "{");
    Set<Descriptor> descriptorSet = new HashSet<Descriptor>();
    for (Token t = scanner.nextToken(); !endBlockToken.equals(t); t = scanner.nextToken())
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
      NumericDescriptor d = new NumericDescriptor();
      d.setDescriptorType(numericType);
      d.setNumericValue(v);
      // System.err.println(d);
      descriptorSet.add(d);
    }
    scanner.nextToken(Token.TokenType.BLOCKIDENTIFIER, "categoricalDescriptors");
    scanner.nextToken(Token.TokenType.SYMBOL, "{");
    for (Token t = scanner.nextToken(); !endBlockToken.equals(t); t = scanner.nextToken())
    {
      if (t == null)
      {
	throw new IllegalStateException("unexpected EOF");
      }
      if (t.getTokenType() != Token.TokenType.NAMEVALUE)
      {
	throw new IllegalStateException("unexpected token type: " + t);
      }
      String categoricalTypeName = t.getName();
      CategoricalType categoricalType = access.findCategoricalType(categoricalTypeName);
      if (categoricalType == null)
      {
	throw new IOException(String.format("no such categorical type: %s", t.toString()));
      }
      CategoricalDescriptor d = new CategoricalDescriptor();
      d.setDescriptorType(categoricalType);
      String[] categoricalValueNameList = Util.splitTrim(t.getValue(), ",");
      // FIXME: should move to proper subclassing for single / multiple values
      if (!categoricalType.getMultivalue() && (categoricalValueNameList.length != 1))
      {
	throw new RuntimeException(String.format("categorical type %s does not permit multiple values", categoricalType.getTypeName()));
      }
      for (String categoricalValueName : categoricalValueNameList)
      {
	CategoricalTypeValue categoricalTypeValue = access.findCategoricalTypeValue(categoricalTypeName, categoricalValueName);
	if (categoricalTypeValue == null)
	{
	  throw new IOException(String.format("no such categorical type value: %s", categoricalValueName));
	}
	d.linkCategoricalTypeValue(categoricalTypeValue);
      }
      // System.err.println(d);
      descriptorSet.add(d);
    }
    // s.nextToken(Token.TokenType.SYMBOL, "}");
    scanner.nextToken(Token.TokenType.BLOCKIDENTIFIER, "imageDescriptors");
    scanner.nextToken(Token.TokenType.SYMBOL, "{");
    for (Token t = scanner.nextToken(); !endBlockToken.equals(t); t = scanner.nextToken())
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
      scanner.nextToken(Token.TokenType.SYMBOL, "{");
      Token mimeToken = scanner.nextToken(Token.TokenType.NAMEVALUE, "mimeType");
      Token fileToken = scanner.nextToken(Token.TokenType.NAMEVALUE, "file");
      ImageDescriptor d = new ImageDescriptor(imageType, mimeToken.getValue().trim(), fileToken.getValue().trim());
      d.setDescriptorType(imageType);
      descriptorSet.add(d);
      scanner.nextToken(Token.TokenType.SYMBOL, "}");
    }
    Token expertDiagnosisToken = scanner.nextToken(Token.TokenType.NAMEVALUE, "expertDiagnosis");
    String expertDiagnosis = expertDiagnosisToken.getValue().trim();
    if (expertDiagnosis.length() == 0)
    {
      expertDiagnosis = null;
    }
    scanner.nextToken(Token.TokenType.SYMBOL, "}");
    access.insert(username, cropScientificName, descriptorSet, expertDiagnosis);
  }


  private static void importCropDisorderRecordFile(BufferedReader in, Access access) throws IOException, NamingException
  {
    Token cdrToken = new Token(Token.TokenType.BLOCKIDENTIFIER, "cdr");
    SimpleScanner scanner = new SimpleScanner(in);
    // t will be null at EOF, so can't just expect a cdr token
    for (Token t = scanner.nextToken(); t != null; t = scanner.nextToken())
    {
      if (!cdrToken.equals(t))
      {
	throw new IllegalStateException("no cdr token");
      }
      importCropDisorderBlock(scanner, access);
    }
  }

  private static void exportCropDisorderRecordFile()
  {
     CropDisorderRecordManager cdrm = null;
     List<CropDisorderRecord> lcdr = cdrm.findCropDisorderRecordList();
     for(CropDisorderRecord cdr : lcdr)
       System.err.println(cdr);
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
    else if (magic.equals("isacrodi-categoricaltypes-0.1"))
    {
      importCategoricalTypeFile(in, access);
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
    else if (magic.equals("isacrodi-procedures-0.1"))
    {
      importProcedureFile(in, access);
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
      List<CropDisorderRecord> cdrList = cdrm.findCropDisorderRecordList();
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
    else if (args[0].equals("-e"))
    {
      String filename = args[1];
      FileWriter out = new FileWriter(filename);
      InitialContext context = new InitialContext();
      CropDisorderRecordManager cdrm = (CropDisorderRecordManager) context.lookup("isacrodi/CropDisorderRecordManagerBean/remote");
      List<CropDisorderRecord> cdrList = cdrm.findCropDisorderRecordList();
      CropDisorderRecord cdro = null;
      out.append("isacrodi-cdrs-0.1\n"); 
      for (CropDisorderRecord cdr : cdrList)
      {
	out.append(cdr.fileRepresentation());
      }
      out.flush();
      out.close();
    }

    else if (args[0].equals("-c"))
    {
      Scanner input = new Scanner( System.in );
      System.out.println("Type cdr id: ");
      int cdrid = input.nextInt();
      Random generator = new Random();
      String filename = args[1];
      FileWriter out = new FileWriter(filename);
      InitialContext context = new InitialContext();
      CropDisorderRecordManager cdrm = (CropDisorderRecordManager) context.lookup("isacrodi/CropDisorderRecordManagerBean/remote");
      CropDisorderRecord cdro = null;
      //cdro = cdrm.findCropDisorderRecord(Integer.parseInt(args[2]));

      out.append("isacrodi-cdrs-0.1\n"); 
      while (cdrid != 0) 
      {
        double min = 0.0;
        double max = 0.0;
        cdro = cdrm.findCropDisorderRecord(cdrid);
        HashMap hmax = new HashMap();
        HashMap hmin = new HashMap();
        for (NumericDescriptor numericDescriptor : cdro.findNumericDescriptorSet())
        {
          System.out.println(numericDescriptor.getDescriptorType().getTypeName() + " " + numericDescriptor.getNumericValue());
          hmax.put(numericDescriptor.getDescriptorType().getTypeName(), input.nextDouble());
          hmin.put(numericDescriptor.getDescriptorType().getTypeName(), numericDescriptor.getNumericValue());
        }

        for (int j = 0; j <= 100; j++) 
        {
          CropDisorderRecord cdrcopy = null;
	  cdrcopy = cdro;
          for (NumericDescriptor numericDescriptor : cdrcopy.findNumericDescriptorSet())
          {
	    min = (Double) hmin.get(numericDescriptor.getDescriptorType().getTypeName());
	    max = (Double) hmax.get(numericDescriptor.getDescriptorType().getTypeName());

	    double dummy = min + (double)(Math.random() * (max - min));
            numericDescriptor.setNumericValue(dummy);
          }
          out.append(cdrcopy.fileRepresentation());
        }
        System.out.println("Type cdr id:  ");
	cdrid = input.nextInt();
      }	
      out.flush();
      out.close();
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
