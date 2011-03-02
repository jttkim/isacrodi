package org.isacrodi.ejb.io;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.IOException;

import java.util.List;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

import org.javamisc.Util;
import org.javamisc.csv.CsvReader;
import org.javamisc.csv.CsvTable;

import org.isacrodi.util.io.SimpleScanner;
import org.isacrodi.util.io.Token;

import org.isacrodi.ejb.session.UserHandler;
import org.isacrodi.ejb.session.Access;
import org.isacrodi.ejb.session.CropDisorderRecordManager;
import org.isacrodi.ejb.session.Kludge;

import org.isacrodi.ejb.io.Import;

import org.isacrodi.ejb.entity.*;


public class Import
{
  public static void importCropFile(BufferedReader in, Access access) throws IOException
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


  public static void importDisorderFile(BufferedReader in, Access access) throws IOException
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


  public static void importProcedureFile(BufferedReader in, Access access) throws IOException
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


  public static void importCategoricalTypeFile(BufferedReader in, Access access) throws IOException
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


  public static void importNumericTypeFile(BufferedReader in, Access access) throws IOException
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


  public static void importImageTypeFile(BufferedReader in, Access access) throws IOException
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


  public static void importCropDisorderBlock(SimpleScanner scanner, Access access) throws IOException
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


  public static void importCropDisorderRecordFile(BufferedReader in, Access access) throws IOException
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
}

