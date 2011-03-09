package org.isacrodi.cmdtool;

import java.io.IOException;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.PrintStream;

import java.util.List;
import java.util.ArrayList;
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
import org.isacrodi.diagnosis.SVMDiagnosisProvider;



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


  private static void generateCdrs(String infileName, String categoricalDescriptorTypeFileName, String numericDescriptorTypeFileName, String outfileName, int rndseed, int numCdrs) throws IOException
  {
    Random rng = new Random(rndseed);
    BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(infileName)));
    MemoryDB memoryDB = new MemoryDB();
    Import.importFile(categoricalDescriptorTypeFileName, memoryDB, memoryDB);
    Import.importFile(numericDescriptorTypeFileName, memoryDB, memoryDB);
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


  private static void testSvmDiagnosisProvider(String configFileName) throws IOException
  {
    MemoryDB memoryDB = new MemoryDB();
    int numTrainingsamples = 0;
    int numTestsamples = 0;
    int numNumericOnlyTestsamples = 0;
    int numCategoricalOnlyTestsamples = 0;
    int numImageOnlyTestsamples = 0;
    double missingDescriptorProbability = 0.0;
    int rndseed = -1;
    String testResultFile = null;
    BufferedReader configIn = new BufferedReader(new InputStreamReader(new FileInputStream(configFileName)));
    List<RangedCropDisorderRecord> rangedCropDisorderRecordList = null;
    for (String line = configIn.readLine(); !"".equals(line.trim()); line = configIn.readLine())
    {
      String[] w = line.split(":");
      if (w.length != 2)
      {
	throw new RuntimeException(String.format("malformed config line: %s", line));
      }
      if ("rangedcdrs".equals(w[0]))
      {
	BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(w[1].trim())));
	rangedCropDisorderRecordList = RangedCropDisorderRecord.parseRangedCropDisorderRecordList(in);
	for (RangedCropDisorderRecord rangedCropDisorderRecord : rangedCropDisorderRecordList)
	{
	  if (memoryDB.findUser(rangedCropDisorderRecord.getIsacrodiUserName()) == null)
	  {
	    IsacrodiUser isacrodiUser = new IsacrodiUser("testsvm", "testsvm", rangedCropDisorderRecord.getIsacrodiUserName(), "", "");
	    memoryDB.insertUser(isacrodiUser);
	    System.err.println("created user: " + isacrodiUser.toString());
	  }
	}
      }
      else if ("numTrainingsamples".equals(w[0]))
      {
	numTrainingsamples = Integer.parseInt(w[1].trim());
      }
      else if ("numTestsamples".equals(w[0]))
      {
	numTestsamples = Integer.parseInt(w[1].trim());
      }
      else if ("numNumericOnlyTestsamples".equals(w[0]))
      {
	numNumericOnlyTestsamples = Integer.parseInt(w[1].trim());
      }
      else if ("numCategoricalOnlyTestsamples".equals(w[0]))
      {
	numCategoricalOnlyTestsamples = Integer.parseInt(w[1].trim());
      }
      else if ("numImageOnlyTestsamples".equals(w[0]))
      {
	numImageOnlyTestsamples = Integer.parseInt(w[1].trim());
      }
      else if ("missingDescriptorProbability".equals(w[0]))
      {
	missingDescriptorProbability = Double.parseDouble(w[1].trim());
      }
      else if ("rndseed".equals(w[0]))
      {
	rndseed = Integer.parseInt(w[1].trim());
      }
      else if ("testResultFile".equals(w[0]))
      {
	testResultFile = w[1].trim();
      }
      else
      {
	throw new RuntimeException(String.format("unknown config parameter: %s", line));
      }
    }
    System.err.println(String.format("%d test samples, %d training samples", numTestsamples, numTrainingsamples));
    for (String line = configIn.readLine(); line != null; line = configIn.readLine())
    {
      System.err.println(String.format("importing: %s", line));
      Import.importFile(line, memoryDB, memoryDB);
    }
    memoryDB.printSummary(System.err);
    for (RangedCropDisorderRecord rangedCropDisorderRecord : rangedCropDisorderRecordList)
    {
      rangedCropDisorderRecord.resolve(memoryDB);
    }
    if (rndseed == -1)
    {
      throw new RuntimeException("no random seed");
    }
    Random rng = new Random(rndseed);
    List<CropDisorderRecord> trainingList = new ArrayList<CropDisorderRecord>();
    for (RangedCropDisorderRecord rangedCropDisorderRecord : rangedCropDisorderRecordList)
    {
      for (int i = 0; i < numTrainingsamples; i++)
      {
	trainingList.add(rangedCropDisorderRecord.randomCropDisorderRecord(rng, memoryDB, missingDescriptorProbability, missingDescriptorProbability, missingDescriptorProbability));
      }
    }
    List<CropDisorderRecord> testList = new ArrayList<CropDisorderRecord>();
    for (RangedCropDisorderRecord rangedCropDisorderRecord : rangedCropDisorderRecordList)
    {
      for (int i = 0; i < numTestsamples; i++)
      {
	CropDisorderRecord cropDisorderRecord = rangedCropDisorderRecord.randomCropDisorderRecord(rng, memoryDB, missingDescriptorProbability, missingDescriptorProbability, missingDescriptorProbability);

	Diagnosis diagnosis = svmDiagnosisProvider.diagnose(cropDisorderRecord);
	DisorderScore highestScore = diagnosis.highestDisorderScore();
	CropDisorder diagnosedCropDisorder = highestScore.getCropDisorder();
	testResultOut.println(String.format("%s\t%s", cropDisorderRecord.getExpertDiagnosedCropDisorder().getScientificName(), diagnosedCropDisorder.getScientificName()));
      }
    }
    List<CropDisorderRecord> testNumericOnlyList = new ArrayList<CropDisorderRecord>();
    for (RangedCropDisorderRecord rangedCropDisorderRecord : rangedCropDisorderRecordList)
    {
      for (int i = 0; i < numTestsamples; i++)
      {
	testNumericOnlyList.add(rangedCropDisorderRecord.randomCropDisorderRecord(rng, memoryDB, missingDescriptorProbability, 1.0, 1.0));
      }
    }
    List<CropDisorderRecord> testCategoricalOnlyList = new ArrayList<CropDisorderRecord>();
    for (RangedCropDisorderRecord rangedCropDisorderRecord : rangedCropDisorderRecordList)
    {
      for (int i = 0; i < numTestsamples; i++)
      {
	testCategoricalOnlyList.add(rangedCropDisorderRecord.randomCropDisorderRecord(rng, memoryDB, 1.0, missingDescriptorProbability, 1.0));
      }
    }
    List<CropDisorderRecord> testImageOnlyList = new ArrayList<CropDisorderRecord>();
    for (RangedCropDisorderRecord rangedCropDisorderRecord : rangedCropDisorderRecordList)
    {
      for (int i = 0; i < numTestsamples; i++)
      {
	testImageOnlyList.add(rangedCropDisorderRecord.randomCropDisorderRecord(rng, memoryDB, 1.0, 1.0, missingDescriptorProbability));
      }
    }
    SVMDiagnosisProvider svmDiagnosisProvider = new SVMDiagnosisProvider();
    svmDiagnosisProvider.train(trainingList);
    // continue here -- open output file before actually producing output...
    PrintStream testResultOut = new PrintStream(testResultFile);
    testResultOut.println("expertDiagnosis\tcomputedDiagnosis");
    for (CropDisorderRecord cropDisorderRecord : testList)
    {
      Diagnosis diagnosis = svmDiagnosisProvider.diagnose(cropDisorderRecord);
      DisorderScore highestScore = diagnosis.highestDisorderScore();
      CropDisorder diagnosedCropDisorder = highestScore.getCropDisorder();
      testResultOut.println(String.format("%s\t%s", cropDisorderRecord.getExpertDiagnosedCropDisorder().getScientificName(), diagnosedCropDisorder.getScientificName()));
    }
    testResultOut.close();
  }


  private static void usage()
  {
    System.out.println("usage: cmdtool <command> [parameters ...]");
    System.out.println("commands are:");
    System.out.println("  diagnosischeck");
    System.out.println("  featuremappercheck");
    System.out.println("  dump <basename>");
    System.out.println("  cdrgen <infile> <categoricaltypefile> <numerictypefile> <rndseed> <numcdrs> <outfile>");
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
      String numericDescriptorTypeFileName = args[3];
      int rndseed = Integer.parseInt(args[4]);
      int numCdrs = Integer.parseInt(args[5]);
      String outfileName = null;
      if (args.length > 6)
      {
	outfileName = args[6];
      }
      generateCdrs(infileName, categoricalDescriptorTypeFileName, numericDescriptorTypeFileName, outfileName, rndseed, numCdrs);
    }
    else if ("testsvm".equals(command))
    {
      testSvmDiagnosisProvider(args[1]);
    }
    else
    {
      System.err.println(String.format("unknown command \"%s\"", command));
      System.exit(1);
    }
  }
}
