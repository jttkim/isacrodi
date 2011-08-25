package org.isacrodi.cmdtool;

import java.io.IOException;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.PrintStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
// import jsc.distributions.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Collection;
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


// FIXME: sample generation and testing are intermingled in this class
class SvmDiagnosisProviderTester
{
  private List<RangedCropDisorderRecord> rangedCropDisorderRecordList;
  private MemoryDB memoryDB;
  private Random rng;
  private String numericProbabilityDistribution;
  private double numericRangeMagnifier;
  private double categoricalErrorProbability;
  private PrintStream out;
  private List<String> descriptorTypeNameList;
  private int maxDiagnosedDisorders;


  public SvmDiagnosisProviderTester(List<RangedCropDisorderRecord> rangedCropDisorderRecordList, MemoryDB memoryDB, int maxDiagnosedDisorders, Random rng, String numericProbabilityDistribution, double numericRangeMagnifier, double categoricalErrorProbability, String testResultFileName) throws IOException
  {
    this.maxDiagnosedDisorders = maxDiagnosedDisorders;
    this.rangedCropDisorderRecordList = rangedCropDisorderRecordList;
    this.descriptorTypeNameList = RangedCropDisorderRecord.findDescriptorTypeNameList(this.rangedCropDisorderRecordList);
    this.memoryDB = memoryDB;
    this.rng = rng;
    this.numericProbabilityDistribution = numericProbabilityDistribution;
    this.numericRangeMagnifier = numericRangeMagnifier;
    this.categoricalErrorProbability = categoricalErrorProbability;
    this.out = new PrintStream(testResultFileName);
    this.out.print("testType\texpertDiagnosis");
    for (int i = 0; i < this.maxDiagnosedDisorders; i++)
    {
      this.out.printf("\tcomputedDiagnosis%d\tdiagnosisScore%d", i, i);
    }
    for (String descriptorTypeName : this.descriptorTypeNameList)
    {
      this.out.printf("\t%s", descriptorTypeName);
    }
    this.out.println();
  }


  public void testSample(SVMDiagnosisProvider svmDiagnosisProvider, CropDisorderRecord cropDisorderRecord, String testTypeLabel)
  {
    Diagnosis diagnosis = svmDiagnosisProvider.diagnose(cropDisorderRecord);
    DisorderScore highestScore = diagnosis.highestDisorderScore();
    List<DisorderScore> disorderScoreList = diagnosis.descendingDisorderScoreList();
    CropDisorder diagnosedCropDisorder = highestScore.getCropDisorder();
    this.out.printf("%s\t%s", testTypeLabel, cropDisorderRecord.getExpertDiagnosedCropDisorder().getScientificName());
    for (int i = 0; i < 3; i++)
    {
      if (i < disorderScoreList.size())
      {
	DisorderScore disorderScore = disorderScoreList.get(i);
	this.out.printf("\t%s\t%f", disorderScore.getCropDisorder().getScientificName(), disorderScore.getScore());
      }
      else
      {
	this.out.print("\t\t");
      }
    }
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


  public void testSamples(SVMDiagnosisProvider svmDiagnosisProvider, int numTestSamples, double missingNumericDescriptorProbability, double missingCategoricalDescriptorProbability, double missingImageDescriptorProbability, String testTypeLabel)
  {
    for (RangedCropDisorderRecord rangedCropDisorderRecord : this.rangedCropDisorderRecordList)
    {
      for (int i = 0; i < numTestSamples; i++)
      {
	CropDisorderRecord cropDisorderRecord = rangedCropDisorderRecord.randomCropDisorderRecord(this.rng, this.numericProbabilityDistribution, this.numericRangeMagnifier, this.categoricalErrorProbability, this.memoryDB, missingNumericDescriptorProbability, missingCategoricalDescriptorProbability, missingImageDescriptorProbability);
	this.testSample(svmDiagnosisProvider, cropDisorderRecord, testTypeLabel);
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


  private static void writeCdrFile(String fileName, Collection<CropDisorderRecord> cdrCollection) throws IOException
  {
    PrintStream out = new PrintStream(fileName);
    out.println("isacrodi-cdrs-0.1");
    for (CropDisorderRecord cdr : cdrCollection)
    {
      out.println(cdr.fileRepresentation());
    }
    out.close();
  }


  private static void testSvmDiagnosisProvider(String configFileName) throws IOException, ClassNotFoundException
  {
    MemoryDB memoryDB = new MemoryDB();
    File configFile = new File(configFileName);
    BufferedReader configIn = new BufferedReader(new InputStreamReader(new FileInputStream(configFileName)));
    String rangedCdrFileName = parseNamedString("rangedCdrFile", configIn);
    int numTrainingSamples = parseNamedInt("numTrainingSamples", configIn);
    int numTestSamples = parseNamedInt("numTestSamples", configIn);
    int numNumericOnlyTestSamples = parseNamedInt("numNumericOnlyTestSamples", configIn);
    int numCategoricalOnlyTestSamples = parseNamedInt("numCategoricalOnlyTestSamples", configIn);
    int numImageOnlyTestSamples = parseNamedInt("numImageOnlyTestSamples", configIn);
    double missingDescriptorProbability = parseNamedDouble("missingDescriptorProbability", configIn);
    String trainingCdrFileName = parseNamedString("trainingCdrFile", configIn);
    String testResultFileName = parseNamedString("testResultFile", configIn);
    String diagnosisProviderFileName = parseNamedString("diagnosisProviderFile", configIn);
    double cauchyRangeMagnifier = parseNamedDouble("cauchyRangeMagnifier", configIn);
    double categoricalErrorProbability = parseNamedDouble("categoricalErrorProbability", configIn);
    int rndseed = parseNamedInt("rndseed", configIn);
    if (!"".equals(configIn.readLine()))
    {
      throw new RuntimeException("no separator line after fixed config block");
    }
    for (String line = configIn.readLine(); line != null; line = configIn.readLine())
    {
      System.err.println(String.format("importing: %s", line));
      File f = new File(line);
      Import.importFile(f, memoryDB, memoryDB);
    }
    File rangedCdrFile = new File(rangedCdrFileName);
    BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(rangedCdrFile)));
    List<RangedCropDisorderRecord> rangedCropDisorderRecordList = RangedCropDisorderRecord.parseRangedCropDisorderRecordList(in);
    in.close();
    for (RangedCropDisorderRecord rangedCropDisorderRecord : rangedCropDisorderRecordList)
    {
      rangedCropDisorderRecord.resolve(memoryDB);
      if (memoryDB.findUser(rangedCropDisorderRecord.getIsacrodiUserName()) == null)
      {
	// FIXME: this seems to rely on insertUser to silently discard multiple insertions of same user
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
	trainingList.add(rangedCropDisorderRecord.randomCropDisorderRecord(rng, "uniform", 1.0, categoricalErrorProbability, memoryDB, missingDescriptorProbability, missingDescriptorProbability, missingDescriptorProbability));
      }
    }
    writeCdrFile(trainingCdrFileName, trainingList);
    SVMDiagnosisProvider svmDiagnosisProvider = new SVMDiagnosisProvider();
    svmDiagnosisProvider.train(trainingList);
    File diagnosisProviderFile = new File(diagnosisProviderFileName);
    ObjectOutputStream diagnosisProviderOut = new ObjectOutputStream(new FileOutputStream(diagnosisProviderFile));
    diagnosisProviderOut.writeObject(svmDiagnosisProvider);
    diagnosisProviderOut.close();
    System.err.println(String.format("diagnosis provider trained and saved to %s", diagnosisProviderFileName));
    // FIXME: should distinguish between missing descriptor probabilities for training and testing
    // FIXME: number of disorder scores hard-coded to 3
    SvmDiagnosisProviderTester svmDiagnosisProviderTester = new SvmDiagnosisProviderTester(rangedCropDisorderRecordList, memoryDB, 3, rng, "cauchy", cauchyRangeMagnifier, categoricalErrorProbability, testResultFileName);
    for (CropDisorderRecord cropDisorderRecord : trainingList)
    {
      svmDiagnosisProviderTester.testSample(svmDiagnosisProvider, cropDisorderRecord, "training");
    }
    System.err.println("diagnosis provider tested on training data");
    svmDiagnosisProviderTester.testSamples(svmDiagnosisProvider, numTestSamples, missingDescriptorProbability, missingDescriptorProbability, missingDescriptorProbability, "standard");
    System.err.println("diagnosis provider tested on standard test data");
    svmDiagnosisProviderTester.testSamples(svmDiagnosisProvider, numNumericOnlyTestSamples, missingDescriptorProbability, 1.0, 1.0, "numericOnly");
    System.err.println("diagnosis provider tested on numeric only test data");
    svmDiagnosisProviderTester.testSamples(svmDiagnosisProvider, numCategoricalOnlyTestSamples, 1.0, missingDescriptorProbability, 1.0, "categoricalOnly");
    System.err.println("diagnosis provider tested on categorical only test data");
    svmDiagnosisProviderTester.testSamples(svmDiagnosisProvider, numImageOnlyTestSamples, 1.0, 1.0, missingDescriptorProbability, "imageOnly");
    System.err.println("diagnosis provider tested on image only test data");
    svmDiagnosisProviderTester.close();
  }


  private static void trainWithRangedCDRs(String configFileName) throws IOException, ClassNotFoundException
  {
    File configFile = new File(configFileName);
    BufferedReader configIn = new BufferedReader(new InputStreamReader(new FileInputStream(configFileName)));
    String rangedCdrFileName = parseNamedString("rangedCdrFile", configIn);
    int numTrainingSamples = parseNamedInt("numTrainingSamples", configIn);
    double missingNumericDescriptorProbability = parseNamedDouble("missingNumericDescriptorProbability", configIn);
    String numericProbabilityDistribution = parseNamedString("numericProbabilityDistribution", configIn);
    double numericRangeMagnifier = parseNamedDouble("numericRangeMagnifier", configIn);
    double missingCategoricalDescriptorProbability = parseNamedDouble("missingCategoricalDescriptorProbability", configIn);
    double categoricalErrorProbability = parseNamedDouble("categoricalErrorProbability", configIn);
    double missingImageDescriptorProbability = parseNamedDouble("missingImageDescriptorProbability", configIn);
    int rndseed = parseNamedInt("rndseed", configIn);
    int svmRndseed = parseNamedInt("svmRndseed", configIn);
    String trainingCdrFileName = parseNamedString("trainingCdrFile", configIn);
    String diagnosisProviderFileName = parseNamedString("diagnosisProviderFile", configIn);
    if (!"".equals(configIn.readLine()))
    {
      throw new RuntimeException("no separator line after fixed config block");
    }
    MemoryDB memoryDB = new MemoryDB();
    for (String line = configIn.readLine(); line != null; line = configIn.readLine())
    {
      System.err.println(String.format("importing: %s", line));
      File f = new File(line);
      Import.importFile(f, memoryDB, memoryDB);
    }
    File rangedCdrFile = new File(rangedCdrFileName);
    BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(rangedCdrFile)));
    List<RangedCropDisorderRecord> rangedCropDisorderRecordList = RangedCropDisorderRecord.parseRangedCropDisorderRecordList(in);
    in.close();
    for (RangedCropDisorderRecord rangedCropDisorderRecord : rangedCropDisorderRecordList)
    {
      rangedCropDisorderRecord.resolve(memoryDB);
      if (memoryDB.findUser(rangedCropDisorderRecord.getIsacrodiUserName()) == null)
      {
	// FIXME: this seems to rely on insertUser to silently discard multiple insertions of same user
	IsacrodiUser isacrodiUser = new IsacrodiUser("svmtrainer", "svmtrainer", rangedCropDisorderRecord.getIsacrodiUserName(), "", "");
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
	trainingList.add(rangedCropDisorderRecord.randomCropDisorderRecord(rng, numericProbabilityDistribution, numericRangeMagnifier, categoricalErrorProbability, memoryDB, missingNumericDescriptorProbability, missingCategoricalDescriptorProbability, missingImageDescriptorProbability));
      }
    }
    writeCdrFile(trainingCdrFileName, trainingList);
    SVMDiagnosisProvider svmDiagnosisProvider = new SVMDiagnosisProvider();
    svmDiagnosisProvider.train(trainingList, svmRndseed);
    File diagnosisProviderFile = new File(diagnosisProviderFileName);
    ObjectOutputStream diagnosisProviderOut = new ObjectOutputStream(new FileOutputStream(diagnosisProviderFile));
    diagnosisProviderOut.writeObject(svmDiagnosisProvider);
    diagnosisProviderOut.close();
    System.err.println(String.format("diagnosis provider trained and saved to %s", diagnosisProviderFileName));
  }


  private static void testWithRangedCDRs(String configFileName) throws IOException, ClassNotFoundException
  {
    File configFile = new File(configFileName);
    BufferedReader configIn = new BufferedReader(new InputStreamReader(new FileInputStream(configFileName)));
    String testName = parseNamedString("testName", configIn);
    String rangedCdrFileName = parseNamedString("rangedCdrFile", configIn);
    String diagnosisProviderFileName = parseNamedString("diagnosisProviderFile", configIn);
    int numTestSamples = parseNamedInt("numTestSamples", configIn);
    double missingNumericDescriptorProbability = parseNamedDouble("missingNumericDescriptorProbability", configIn);
    String numericProbabilityDistribution = parseNamedString("numericProbabilityDistribution", configIn);
    double numericRangeMagnifier = parseNamedDouble("numericRangeMagnifier", configIn);
    double missingCategoricalDescriptorProbability = parseNamedDouble("missingCategoricalDescriptorProbability", configIn);
    double categoricalErrorProbability = parseNamedDouble("categoricalErrorProbability", configIn);
    double missingImageDescriptorProbability = parseNamedDouble("missingImageDescriptorProbability", configIn);
    int rndseed = parseNamedInt("rndseed", configIn);
    String testResultFileName = parseNamedString("testResultFile", configIn);
    if (!"".equals(configIn.readLine()))
    {
      throw new RuntimeException("no separator line after fixed config block");
    }
    MemoryDB memoryDB = new MemoryDB();
    for (String line = configIn.readLine(); line != null; line = configIn.readLine())
    {
      System.err.println(String.format("importing: %s", line));
      File f = new File(line);
      Import.importFile(f, memoryDB, memoryDB);
    }
    File rangedCdrFile = new File(rangedCdrFileName);
    BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(rangedCdrFile)));
    List<RangedCropDisorderRecord> rangedCropDisorderRecordList = RangedCropDisorderRecord.parseRangedCropDisorderRecordList(in);
    in.close();
    for (RangedCropDisorderRecord rangedCropDisorderRecord : rangedCropDisorderRecordList)
    {
      rangedCropDisorderRecord.resolve(memoryDB);
      if (memoryDB.findUser(rangedCropDisorderRecord.getIsacrodiUserName()) == null)
      {
	// FIXME: this seems to rely on insertUser to silently discard multiple insertions of same user
	IsacrodiUser isacrodiUser = new IsacrodiUser("testsvm", "testsvm", rangedCropDisorderRecord.getIsacrodiUserName(), "", "");
	memoryDB.insertUser(isacrodiUser);
	System.err.println("created user: " + isacrodiUser.toString());
      }
    }
    memoryDB.printSummary(System.err);
    Random rng = new Random(rndseed);
    // train and write diagnosis provider
    File diagnosisProviderFile = new File(diagnosisProviderFileName);
    ObjectInputStream diagnosisProviderIn = new ObjectInputStream(new FileInputStream(diagnosisProviderFile));
    SVMDiagnosisProvider svmDiagnosisProvider = (SVMDiagnosisProvider) diagnosisProviderIn.readObject();
    diagnosisProviderIn.close();
    System.err.println(String.format("diagnosis provider read from %s", diagnosisProviderFileName));
    // FIXME: should distinguish between missing descriptor probabilities for training and testing
    // FIXME: number of disorder scores hard-coded to 3
    SvmDiagnosisProviderTester svmDiagnosisProviderTester = new SvmDiagnosisProviderTester(rangedCropDisorderRecordList, memoryDB, 3, rng, numericProbabilityDistribution, numericRangeMagnifier, categoricalErrorProbability, testResultFileName);
    svmDiagnosisProviderTester.testSamples(svmDiagnosisProvider, numTestSamples, missingNumericDescriptorProbability, missingCategoricalDescriptorProbability, missingImageDescriptorProbability, testName);
    svmDiagnosisProviderTester.close();
    System.err.println(String.format("diagnosis provider test \"%s\" finished", testName));
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
    else if ("rtrain".equals(command))
    {
      trainWithRangedCDRs(args[1]);
    }
    else if ("rtest".equals(command))
    {
      testWithRangedCDRs(args[1]);
    }
    else
    {
      System.err.println(String.format("unknown command \"%s\"", command));
      System.exit(1);
    }
  }
}
