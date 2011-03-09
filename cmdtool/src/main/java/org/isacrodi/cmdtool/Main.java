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


  private static String parseNamedString(String expectedLabel, BufferedReader in) throws IOException
  {
    String line = in.readLine();
    String[] w = line.split(":", 2);
    if (w.length != 2)
    {
      throw new RuntimeException(String.format("malformed line: %s", line));
    }
    String label = w[0].trim();
    if (!expectedLabel.equals(label))
    {
      throw new RuntimeException(String.format("expected %s but got %s", expectedLabel, label));
    }
    return (w[1].trim());
  }


  private static int parseNamedInt(String expectedLabel, BufferedReader in) throws IOException
  {
    return (Integer.parseInt(parseNamedString(expectedLabel, in)));
  }


  private static double parseNamedDouble(String expectedLabel, BufferedReader in) throws IOException
  {
    return (Double.parseDouble(parseNamedString(expectedLabel, in)));
  }


  private static void runSvmDiagnosisProviderTest(List<RangedCropDisorderRecord> rangedCropDisorderRecordList, SVMDiagnosisProvider svmDiagnosisProvider, MemoryDB memoryDB, int numTestSamples, double missingNumericDescriptorProbability, double missingCategoricalDescriptorProbability, double missingImageDescriptorProbability, Random rng, PrintStream testResultOut, String testTypeLabel)
  {
    for (RangedCropDisorderRecord rangedCropDisorderRecord : rangedCropDisorderRecordList)
    {
      for (int i = 0; i < numTestSamples; i++)
      {
	CropDisorderRecord cropDisorderRecord = rangedCropDisorderRecord.randomCropDisorderRecord(rng, memoryDB, missingNumericDescriptorProbability, missingCategoricalDescriptorProbability, missingImageDescriptorProbability);

	Diagnosis diagnosis = svmDiagnosisProvider.diagnose(cropDisorderRecord);
	DisorderScore highestScore = diagnosis.highestDisorderScore();
	CropDisorder diagnosedCropDisorder = highestScore.getCropDisorder();
	testResultOut.println(String.format("%s\t%s\t%s", testTypeLabel, cropDisorderRecord.getExpertDiagnosedCropDisorder().getScientificName(), diagnosedCropDisorder.getScientificName()));
      }
    }
  }


  private static void testSvmDiagnosisProvider(String configFileName) throws IOException
  {
    MemoryDB memoryDB = new MemoryDB();
    BufferedReader configIn = new BufferedReader(new InputStreamReader(new FileInputStream(configFileName)));
    String rangedCdrFileName = parseNamedString("rangedCdrFile", configIn);
    int numTrainingSamples = parseNamedInt("numTrainingSamples", configIn);
    int numTestSamples = parseNamedInt("numTestSamples", configIn);
    int numNumericOnlyTestSamples = parseNamedInt("numNumericOnlyTestSamples", configIn);
    int numCategoricalOnlyTestSamples = parseNamedInt("numCategoricalOnlyTestSamples", configIn);
    int numImageOnlyTestSamples = parseNamedInt("numImageOnlyTestSamples", configIn);
    double missingDescriptorProbability = parseNamedDouble("missingDescriptorProbability", configIn);
    String testResultFileName = parseNamedString("testResultFile", configIn);
    int rndseed = parseNamedInt("rndseed", configIn);
    if (!"".equals(configIn.readLine()))
    {
      throw new RuntimeException("no separator line after fixed config block");
    }
    for (String line = configIn.readLine(); line != null; line = configIn.readLine())
    {
      System.err.println(String.format("importing: %s", line));
      Import.importFile(line, memoryDB, memoryDB);
    }
    BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(rangedCdrFileName)));
    List<RangedCropDisorderRecord> rangedCropDisorderRecordList = RangedCropDisorderRecord.parseRangedCropDisorderRecordList(in);
    in.close();
    for (RangedCropDisorderRecord rangedCropDisorderRecord : rangedCropDisorderRecordList)
    {
      rangedCropDisorderRecord.resolve(memoryDB);
      if (memoryDB.findUser(rangedCropDisorderRecord.getIsacrodiUserName()) == null)
      {
	IsacrodiUser isacrodiUser = new IsacrodiUser("testsvm", "testsvm", rangedCropDisorderRecord.getIsacrodiUserName(), "", "");
	memoryDB.insertUser(isacrodiUser);
	System.err.println("created user: " + isacrodiUser.toString());
      }
    }
    memoryDB.printSummary(System.err);
    Random rng = new Random(rndseed);
    List<CropDisorderRecord> trainingList = new ArrayList<CropDisorderRecord>();
    for (RangedCropDisorderRecord rangedCropDisorderRecord : rangedCropDisorderRecordList)
    {
      for (int i = 0; i < numTrainingSamples; i++)
      {
	trainingList.add(rangedCropDisorderRecord.randomCropDisorderRecord(rng, memoryDB, missingDescriptorProbability, missingDescriptorProbability, missingDescriptorProbability));
      }
    }
    SVMDiagnosisProvider svmDiagnosisProvider = new SVMDiagnosisProvider();
    svmDiagnosisProvider.train(trainingList);
    // continue here -- open output file before actually producing output...
    PrintStream testResultOut = new PrintStream(testResultFileName);
    testResultOut.println("testType\texpertDiagnosis\tcomputedDiagnosis");
    runSvmDiagnosisProviderTest(rangedCropDisorderRecordList, svmDiagnosisProvider, memoryDB, numTestSamples, missingDescriptorProbability, missingDescriptorProbability, missingDescriptorProbability, rng, testResultOut, "standard");
    runSvmDiagnosisProviderTest(rangedCropDisorderRecordList, svmDiagnosisProvider, memoryDB, numTestSamples, missingDescriptorProbability, 1.0, 1.0, rng, testResultOut, "numericOnly");
    runSvmDiagnosisProviderTest(rangedCropDisorderRecordList, svmDiagnosisProvider, memoryDB, numTestSamples, 1.0, missingDescriptorProbability, 1.0, rng, testResultOut, "categoricalOnly");
    runSvmDiagnosisProviderTest(rangedCropDisorderRecordList, svmDiagnosisProvider, memoryDB, numTestSamples, 1.0, 1.0, missingDescriptorProbability, rng, testResultOut, "imageOnly");
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
