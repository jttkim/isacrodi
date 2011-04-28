package org.isacrodi.cmdtool;

import java.io.IOException;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.PrintStream;
import jsc.distributions.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Collections;

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
import org.isacrodi.diagnosis.AbstractFeature;
import org.isacrodi.diagnosis.NumericFeature;
import org.isacrodi.diagnosis.CategoricalFeature;
import org.isacrodi.diagnosis.FeatureVector;


class SvmDiagnosisProviderTester
{
  private List<RangedCropDisorderRecord> rangedCropDisorderRecordList;
  private MemoryDB memoryDB;
  private Random rng;
  private String distType;
  private PrintStream out;
  private List<String> descriptorTypeNameList;


  public SvmDiagnosisProviderTester(List<RangedCropDisorderRecord> rangedCropDisorderRecordList, MemoryDB memoryDB, Random rng, String distType, String testResultFileName) throws IOException
  {
    this.rangedCropDisorderRecordList = rangedCropDisorderRecordList;
    this.descriptorTypeNameList = RangedCropDisorderRecord.findDescriptorTypeNameList(this.rangedCropDisorderRecordList);
    this.memoryDB = memoryDB;
    this.rng = rng;
    this.distType = distType;
    this.out = new PrintStream(testResultFileName);
    this.out.print("testType\texpertDiagnosis\tcomputedDiagnosis");
    for (String descriptorTypeName : this.descriptorTypeNameList)
    {
      this.out.printf("\t%s", descriptorTypeName);
    }
    this.out.println();
  }


  public void test(SVMDiagnosisProvider svmDiagnosisProvider, int numTestSamples, double missingNumericDescriptorProbability, double missingCategoricalDescriptorProbability, double missingImageDescriptorProbability, String testTypeLabel)
  {
    for (RangedCropDisorderRecord rangedCropDisorderRecord : this.rangedCropDisorderRecordList)
    {
      for (int i = 0; i < numTestSamples; i++)
      {
	CropDisorderRecord cropDisorderRecord = rangedCropDisorderRecord.randomCropDisorderRecord(this.rng, this.distType, this.memoryDB, missingNumericDescriptorProbability, missingCategoricalDescriptorProbability, missingImageDescriptorProbability);

	Diagnosis diagnosis = svmDiagnosisProvider.diagnose(cropDisorderRecord);
	DisorderScore highestScore = diagnosis.highestDisorderScore();
	CropDisorder diagnosedCropDisorder = highestScore.getCropDisorder();
	this.out.printf("%s\t%s\t%s", testTypeLabel, cropDisorderRecord.getExpertDiagnosedCropDisorder().getScientificName(), diagnosedCropDisorder.getScientificName());
	FeatureVector featureVector = svmDiagnosisProvider.extract(cropDisorderRecord);
	for (String descriptorTypeName : this.descriptorTypeNameList)
	{
	  AbstractFeature feature = featureVector.get(descriptorTypeName);
	  if (feature == null)
	  {
	    this.out.print("\t");
	  }
	  else
	  {
	    if (feature instanceof CategoricalFeature)
	    {
	      CategoricalFeature categoricalFeature = (CategoricalFeature) feature;
	      List<String> stateList = new ArrayList<String>(categoricalFeature.getStateSet());
	      Collections.sort(stateList);
	      this.out.print("\t");
	      String glue = String.format("%s:", descriptorTypeName);
	      for (String state : stateList)
	      {
		this.out.printf("%s%s", glue, state);
		glue = "_";
	      }
	    }
	    else if (feature instanceof NumericFeature)
	    {
	      NumericFeature numericFeature = (NumericFeature) feature;
	      this.out.printf("\t%f", numericFeature.getValue());
	    }
	  }
	}
	this.out.println();
      }
    }
  }


  public void close()
  {
    this.out.close();
  }
}


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
    String distType;
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
    distType = "uniform";
    memoryDB.printSummary(System.err);
    Random rng = new Random(rndseed);
    List<CropDisorderRecord> trainingList = new ArrayList<CropDisorderRecord>();
    for (RangedCropDisorderRecord rangedCropDisorderRecord : rangedCropDisorderRecordList)
    {
      for (int i = 0; i < numTrainingSamples; i++)
      {
	trainingList.add(rangedCropDisorderRecord.randomCropDisorderRecord(rng, distType, memoryDB, missingDescriptorProbability, missingDescriptorProbability, missingDescriptorProbability));
      }
    }
    distType = "cauchy";
    SVMDiagnosisProvider svmDiagnosisProvider = new SVMDiagnosisProvider();
    svmDiagnosisProvider.train(trainingList);
    // continue here -- open output file before actually producing output...
    SvmDiagnosisProviderTester svmDiagnosisProviderTester = new SvmDiagnosisProviderTester(rangedCropDisorderRecordList, memoryDB, rng, distType, testResultFileName);
    svmDiagnosisProviderTester.test(svmDiagnosisProvider, numTestSamples, missingDescriptorProbability, missingDescriptorProbability, missingDescriptorProbability, "standard");
    svmDiagnosisProviderTester.test(svmDiagnosisProvider, numNumericOnlyTestSamples, missingDescriptorProbability, 1.0, 1.0, "numericOnly");
    svmDiagnosisProviderTester.test(svmDiagnosisProvider, numCategoricalOnlyTestSamples, 1.0, missingDescriptorProbability, 1.0, "categoricalOnly");
    svmDiagnosisProviderTester.test(svmDiagnosisProvider, numImageOnlyTestSamples, 1.0, 1.0, missingDescriptorProbability, "imageOnly");
    svmDiagnosisProviderTester.close();
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
      System.out.println(args[0]);
      testSvmDiagnosisProvider(args[1]);
    }

    else if ("testtool".equals(command))
    {
      Cauchy cauchy = new Cauchy();
      cauchy.setSeed(1);
      System.out.println(cauchy.cdf(10.0));
      System.out.println(cauchy.random());

    }
    else
    {
      System.err.println(String.format("unknown command \"%s\"", command));
      System.exit(1);
    }
  }
}
