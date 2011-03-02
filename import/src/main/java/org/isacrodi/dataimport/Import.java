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

import java.util.*;

// FIXME: wildcard import
import org.isacrodi.diagnosis.*;

import org.isacrodi.ejb.entity.*;
import org.isacrodi.ejb.session.UserHandler;
import org.isacrodi.ejb.session.Access;
import org.isacrodi.ejb.session.CropDisorderRecordManager;
import org.isacrodi.ejb.session.Kludge;
import org.isacrodi.ejb.io.Import;



// FIXME: exceptions thrown by this class are a haphazard mess, util should provide exceptions for scanning and parsing

public class Import
{
  // FIXME: unused?
  private static final String[] headerUser = {"lastname", "firstname", "username", "password", "email"};

  public static void importUser(UserHandler userHandler, String lastname, String firstname, String username, String password, String email)
  {
    if (userHandler.findUser(username) != null)
    {
      System.err.println(String.format("user \"%s\" already exists", username));
      return;
    }
    IsacrodiUser user = new IsacrodiUser(lastname, firstname, username, IsacrodiUser.hash(password), email);
    userHandler.insertUser(user);
  }


  // FIXME: should really not look up handler in this method
  public static void importUserFile(CsvTable csvTable) throws IOException, NamingException
  {
    InitialContext context = new InitialContext();
    UserHandler userHandler = (UserHandler) context.lookup("isacrodi/UserHandlerBean/remote");
    while (csvTable.next())
    {
      importUser(userHandler, csvTable.getString("lastname"), csvTable.getString("firstname"), csvTable.getString("username"), csvTable.getString("password"), csvTable.getString("email"));
    }
  }


  public static void exportCropDisorderRecordFile()
  {
    CropDisorderRecordManager cdrm = null;
    List<CropDisorderRecord> lcdr = cdrm.findCropDisorderRecordList();
    for(CropDisorderRecord cdr : lcdr)
    {
      System.err.println(cdr);
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

      out.append("isacrodi-cdrs-0.1\n"); 
      while (cdrid != 0) 
      {
        double min = 0.0;
        double max = 0.0;
        cdro = cdrm.findCropDisorderRecord(cdrid);
        HashMap hmax = new HashMap();
        HashMap hmin = new HashMap();
	String soil[] = null;
	String cropstage[] = null;
	String peststage[] = null;
	String firstsymptomcropstage[] = null;

        for (NumericDescriptor numericDescriptor : cdro.findNumericDescriptorSet())
        {
          System.out.println(numericDescriptor.getDescriptorType().getTypeName() + " " + numericDescriptor.getNumericValue());
          hmax.put(numericDescriptor.getDescriptorType().getTypeName(), input.nextDouble());
          hmin.put(numericDescriptor.getDescriptorType().getTypeName(), numericDescriptor.getNumericValue());
        }

        for (CategoricalDescriptor categoricalDescriptor : cdro.findCategoricalDescriptorSet())
	{
	  if (categoricalDescriptor.getDescriptorType().getTypeName().equals("soil"))
	  {
            System.out.println("How many values can soil have? ");
	    int j = input.nextInt();
	    soil = new String[j]; 
            System.out.println("Type values ");
	    for(int i = 0; i < j; i++)
	      soil[i] = input.next();
	  }
	  if (categoricalDescriptor.getDescriptorType().getTypeName().equals("cropstage"))
	  {
            System.out.println("How many values can crop stage have? ");
	    int j = input.nextInt();
	    cropstage = new String[j]; 
            System.out.println("Type values ");
	    for(int i = 0; i < j; i++)
	      cropstage[i] = input.next();
	  }
	  if (categoricalDescriptor.getDescriptorType().getTypeName().equals("firstsymptomcropstage"))
	  {
            System.out.println("How many values can firstsymptomcropstage stage have? ");
	    int j = input.nextInt();
	    firstsymptomcropstage = new String[j]; 
            System.out.println("Type values ");
	    for(int i = 0; i < j; i++)
	      firstsymptomcropstage[i] = input.next();
	  }
	  if (categoricalDescriptor.getDescriptorType().getTypeName().equals("peststage"))
	  {
            System.out.println("How many values can pest stage have? ");
	    int j = input.nextInt();
	    peststage = new String[j]; 
            System.out.println("Type values ");
	    for(int i = 0; i < j; i++)
	      peststage[i] = input.next();
	  }
	}


        for (int j = 0; j <= 20; j++) 
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

          for (CategoricalDescriptor categoricalDescriptor : cdrcopy.findCategoricalDescriptorSet())
          {
	    if (categoricalDescriptor.getDescriptorType().getTypeName().equals("soil"))
	    {
	      int dummy = 0 + (int)(Math.random() * (soil.length - 0));
	      for(CategoricalTypeValue categoricaltype : categoricalDescriptor.getCategoricalTypeValueSet())
	        categoricaltype.setValueType(soil[dummy]);
	    }
	    if (categoricalDescriptor.getDescriptorType().getTypeName().equals("cropstage"))
	    {
	      int dummy = 0 + (int)(Math.random() * (cropstage.length - 0));
	      for(CategoricalTypeValue categoricaltype : categoricalDescriptor.getCategoricalTypeValueSet())
	        categoricaltype.setValueType(cropstage[dummy]);
	    }
	    if (categoricalDescriptor.getDescriptorType().getTypeName().equals("peststage"))
	    {
	      int dummy = 0 + (int)(Math.random() * (peststage.length - 0));
	      for(CategoricalTypeValue categoricaltype : categoricalDescriptor.getCategoricalTypeValueSet())
	        categoricaltype.setValueType(peststage[dummy]);
	    }
	    if (categoricalDescriptor.getDescriptorType().getTypeName().equals("firstsymptomcropstage"))
	    {
	      int dummy = 0 + (int)(Math.random() * (firstsymptomcropstage.length - 0));
	      for(CategoricalTypeValue categoricaltype : categoricalDescriptor.getCategoricalTypeValueSet())
	        categoricaltype.setValueType(firstsymptomcropstage[dummy]);
	    }
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
