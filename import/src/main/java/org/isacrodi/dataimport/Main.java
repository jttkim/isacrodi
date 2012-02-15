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
import java.util.Hashtable;
import java.util.Iterator;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.javamisc.Util;
import org.javamisc.csv.CsvReader;
import org.javamisc.csv.CsvTable;

import java.util.*;

// FIXME: wildcard import
import org.isacrodi.diagnosis.*;
import org.isacrodi.util.io.*;
import org.isacrodi.util.*;

import org.isacrodi.ejb.entity.*;
import org.isacrodi.ejb.session.UserHandler;
import org.isacrodi.ejb.session.UserHandlerBean;
import org.isacrodi.ejb.session.Access;
import org.isacrodi.ejb.session.AccessBean;
import org.isacrodi.ejb.session.CropDisorderRecordManager;
import org.isacrodi.ejb.session.CropDisorderRecordManagerBean;
import org.isacrodi.ejb.session.Kludge;
import org.isacrodi.ejb.session.KludgeBean;
import org.isacrodi.ejb.io.Import;



// FIXME: exceptions thrown by this class are a haphazard mess, util should provide exceptions for scanning and parsing

public class Main
{
  // FIXME: unused?
  private static final String[] headerUser = {"lastname", "firstname", "username", "password", "email"};


  public static void exportCropDisorderRecordFile()
  {
    CropDisorderRecordManager cdrm = null;
    List<CropDisorderRecord> lcdr = cdrm.findCropDisorderRecordList();
    for(CropDisorderRecord cdr : lcdr)
    {
      System.err.println(cdr);
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
      Kludge kludge = (Kludge) context.lookup(EjbSupport.sessionJndiName(KludgeBean.class, Kludge.class));
      kludge.makeRandomExpertDiagnosedCDRs(rndseed, numNumericTypes, numCrops, numCropDisorders, numCDRs, numDisorderAssociations, numericDescriptorPercentage, stddevBetween, stddevWithin);
    }
    else if (args[0].equals("-e"))
    {
      String filename = args[1];
      FileWriter out = new FileWriter(filename);
      InitialContext context = new InitialContext();
      // CropDisorderRecordManager cdrm = (CropDisorderRecordManager) context.lookup("isacrodi/CropDisorderRecordManagerBean/remote");
      CropDisorderRecordManager cdrm = (CropDisorderRecordManager) context.lookup(EjbSupport.sessionJndiName(CropDisorderRecordManagerBean.class, CropDisorderRecordManager.class));
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
    else
    {
      // final Hashtable jndiProperties = new Hashtable();
      // jndiProperties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
      Context context = new InitialContext();
      System.err.println(context);
      //UserHandler userHandler = (UserHandler) context.lookup("isacrodi/UserHandlerBean/remote");
      //Access access = (Access) context.lookup("isacrodi/AccessBean/remote");
      String jndiName = EjbSupport.sessionJndiName(UserHandlerBean.class, UserHandler.class);
      System.err.println(jndiName);
      UserHandler userHandler = (UserHandler) context.lookup(jndiName);
      System.err.println(userHandler);
      jndiName = EjbSupport.sessionJndiName(AccessBean.class, Access.class);
      Access access = (Access) context.lookup(jndiName);
      for (String arg : args)
      {
	Import.importFile(arg, access, userHandler);
      }
    }
  }
}
