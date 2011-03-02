package org.isacrodi.cmdtool;

import java.io.IOException;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.PrintStream;

import java.util.List;
import java.util.Random;

import javax.naming.NamingException;
import javax.naming.InitialContext;

import org.isacrodi.ejb.session.CropDisorderRecordManager;
import org.isacrodi.ejb.session.Access;
import org.isacrodi.ejb.session.Kludge;
import org.javamisc.jee.entitycrud.EntityAccess;

import org.isacrodi.ejb.session.Access;

import org.isacrodi.ejb.io.Import;
import org.isacrodi.ejb.io.MemoryDB;

import org.isacrodi.ejb.entity.*;

import org.isacrodi.diagnosis.DiagnosisProvider;



public class Main
{
  private static void diagnosischeck() throws NamingException
  {
    InitialContext context = new InitialContext();
    CropDisorderRecordManager cropDisorderRecordManager = (CropDisorderRecordManager) context.lookup("isacrodi/CropDisorderRecordManagerBean/remote");
    for (CropDisorderRecord cropDisorderRecord : cropDisorderRecordManager.findExpertDiagnosedCropDisorderRecordList())
    {
      cropDisorderRecordManager.requestDiagnosis(cropDisorderRecord.getId());
    }
    int numCorrectDiagnoses = 0;
    int numCdrs = 0;
    for (CropDisorderRecord cropDisorderRecord : cropDisorderRecordManager.findExpertDiagnosedCropDisorderRecordList())
    {
      CropDisorder expertDiagnosedCropDisorder = cropDisorderRecord.getExpertDiagnosedCropDisorder();
      Diagnosis diagnosis = cropDisorderRecord.getDiagnosis();
      DisorderScore highscore = diagnosis.highestDisorderScore();
      CropDisorder cropDisorder = highscore.getCropDisorder();
      System.err.printf("expert: %s, diagnosis: %s\n", expertDiagnosedCropDisorder.getScientificName(), cropDisorder.getScientificName());
      if ((cropDisorder != null) && (cropDisorder.equals(expertDiagnosedCropDisorder)))
      {
	numCorrectDiagnoses++;
      }
      numCdrs++;
    }
    System.out.printf("%d out of %d diagnoses are correct (%5.2f%%)\n", numCorrectDiagnoses, numCdrs, ((double) numCorrectDiagnoses) / ((double) numCdrs));
  }


  private static void dumpEntities(String basename) throws NamingException, IOException
  {
    InitialContext context = new InitialContext();
    Access access = (Access) context.lookup("isacrodi/AccessBean/remote");
    access.dumpEntities(basename);
  }


  private static void generateCdrs(String infileName, String categoricalDescriptorTypeFileName, String outfileName, int rndseed, int numCdrs) throws IOException
  {
    Random rng = new Random(rndseed);
    BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(infileName)));
    MemoryDB memoryDB = new MemoryDB();
    Import.importFile(categoricalDescriptorTypeFileName, memoryDB, memoryDB);
    List<RangedCropDisorderRecord> rangedCropDisorderRecordList = RangedCropDisorderRecord.parseRangedCropDisorderRecordList(in);
    for (RangedCropDisorderRecord  rangedCropDisorderRecord : rangedCropDisorderRecordList)
    {
      rangedCropDisorderRecord.resolve(memoryDB);
    }
    PrintStream out = System.out;
    if (outfileName != null)
    {
      out = new PrintStream(outfileName);
    }
    out.println("isacrodi-cdrs-0.1");
    for (RangedCropDisorderRecord rangedCropDisorderRecord : rangedCropDisorderRecordList)
    {
      for (int i = 0; i < numCdrs; i++)
      {
	out.println(rangedCropDisorderRecord.randomCropDisorderRecordString(rng));
      }
    }
    if (outfileName != null)
    {
      out.close();
    }
  }


  private static void usage()
  {
    System.out.println("usage: cmdtool <command> [parameters ...]");
    System.out.println("commands are:");
    System.out.println("  diagnosischeck");
    System.out.println("  featuremappercheck");
    System.out.println("  dump <basename>");
    System.out.println("  cdrgen <infile> <categoricaldescriptortypefile> <rndseed> <numcdrs> <outfile>");
  }


  public static void main(String[] args) throws Exception
  {
    if (args.length == 0)
    {
      usage();
      return;
    }
    String command = args[0];
    if ("diagnosischeck".equals(command))
    {
      diagnosischeck();
    }
    else if ("featuremappercheck".equals(command))
    {
      FeatureMapping.check();
    }
    else if ("dump".equals(command))
    {
      if (args.length < 2)
      {
	throw new RuntimeException("no base name specified");
      }
      String basename = args[1];
      dumpEntities(basename);
    }
    else if ("cdrgen".equals(command))
    {
      String infileName = args[1];
      String categoricalDescriptorTypeFileName = args[2];
      int rndseed = Integer.parseInt(args[3]);
      int numCdrs = Integer.parseInt(args[4]);
      String outfileName = null;
      if (args.length > 5)
      {
	outfileName = args[5];
      }
      generateCdrs(infileName, categoricalDescriptorTypeFileName, outfileName, rndseed, numCdrs);
    }
    else
    {
      System.err.println(String.format("unknown command \"%s\"", command));
      System.exit(1);
    }
  }
}
