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

import org.isacrodi.util.EjbSupport;

import org.isacrodi.ejb.session.CropDisorderRecordManager;
import org.isacrodi.ejb.session.CropDisorderRecordManagerBean;
import org.isacrodi.ejb.session.Access;
import org.isacrodi.ejb.session.AccessBean;
import org.isacrodi.ejb.session.Kludge;
import org.isacrodi.ejb.session.KludgeBean;
import org.javamisc.jee.entitycrud.EntityAccess;
import org.isacrodi.ejb.session.EntityAccessBean;

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
    this.out.print("testType\texpertDiagnosis\tinformation");
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


  public void testSample(SVMDiagnosisProvider svmDiagnosisProvider, CropDisorderRecord cropDisorderRecord, String testTypeLabel, Collection<CropDisorder> cropDisorderSet)
  {
    Diagnosis diagnosis = svmDiagnosisProvider.diagnose(cropDisorderRecord, cropDisorderSet);
    DisorderScore highestScore = diagnosis.highestDisorderScore();
    List<DisorderScore> disorderScoreList = diagnosis.descendingDisorderScoreList();
    CropDisorder diagnosedCropDisorder = highestScore.getCropDisorder();
    this.out.printf("%s\t%s\t%f", testTypeLabel, cropDisorderRecord.getExpertDiagnosedCropDisorder().getScientificName(), diagnosis.shannonInformation());
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


  public void testSamples(SVMDiagnosisProvider svmDiagnosisProvider, int numTestSamples, double missingNumericDescriptorProbability, double missingCategoricalDescriptorProbability, double missingImageDescriptorProbability, String testTypeLabel, Collection<CropDisorder> cropDisorderSet)
  {
    for (RangedCropDisorderRecord rangedCropDisorderRecord : this.rangedCropDisorderRecordList)
    {
      for (int i = 0; i < numTestSamples; i++)
      {
	CropDisorderRecord cropDisorderRecord = rangedCropDisorderRecord.randomCropDisorderRecord(this.rng, this.numericProbabilityDistribution, this.numericRangeMagnifier, this.categoricalErrorProbability, this.memoryDB, missingNumericDescriptorProbability, missingCategoricalDescriptorProbability, missingImageDescriptorProbability);
	this.testSample(svmDiagnosisProvider, cropDisorderRecord, testTypeLabel, cropDisorderSet);
      }
    }
  }


  public void close()
  {
    this.out.close();
  }
}


@SuppressWarnings("deprecation")
public class Main
{
  private static void diagnosischeck() throws NamingException
  {
    InitialContext context = new InitialContext();
    CropDisorderRecordManager cropDisorderRecordManager = (CropDisorderRecordManager) context.lookup(EjbSupport.sessionJndiName(CropDisorderRecordManagerBean.class, CropDisorderRecordManager.class));
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


  private static void trainDiagnosisProviderFromDatabase() throws IOException, NamingException
  {
    InitialContext context = new InitialContext();
    CropDisorderRecordManager cropDisorderRecordManager = (CropDisorderRecordManager) context.lookup(EjbSupport.sessionJndiName(CropDisorderRecordManagerBean.class, CropDisorderRecordManager.class));
    Kludge kludge = (Kludge) context.lookup(EjbSupport.sessionJndiName(KludgeBean.class, Kludge.class));
    SVMDiagnosisProvider svmDiagnosisProvider = new SVMDiagnosisProvider();
    svmDiagnosisProvider.train(cropDisorderRecordManager.findExpertDiagnosedCropDisorderRecordList());
    kludge.storeDiagnosisProvider(svmDiagnosisProvider);
  }


  private static void dumpEntities(String basename) throws NamingException, IOException
  {
    InitialContext context = new InitialContext();
    Access access = (Access) context.lookup(EjbSupport.sessionJndiName(AccessBean.class, Access.class));
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


  private static DiagnosisProvider readDiagnosisProvider(String diagnosisProviderFileName) throws IOException, ClassNotFoundException
  {
    File diagnosisProviderFile = new File(diagnosisProviderFileName);
    ObjectInputStream diagnosisProviderIn = new ObjectInputStream(new FileInputStream(diagnosisProviderFile));
    DiagnosisProvider diagnosisProvider = (DiagnosisProvider) diagnosisProviderIn.readObject();
    diagnosisProviderIn.close();
    System.err.println(String.format("diagnosis provider read from %s", diagnosisProviderFileName));
    return (diagnosisProvider);
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
    Collection<CropDisorder> diagnosableDisorderSet = memoryDB.findCropDisorderList();
    for (CropDisorderRecord cropDisorderRecord : trainingList)
    {
      svmDiagnosisProviderTester.testSample(svmDiagnosisProvider, cropDisorderRecord, "training", diagnosableDisorderSet);
    }
    System.err.println("diagnosis provider tested on training data");
    svmDiagnosisProviderTester.testSamples(svmDiagnosisProvider, numTestSamples, missingDescriptorProbability, missingDescriptorProbability, missingDescriptorProbability, "standard", diagnosableDisorderSet);
    System.err.println("diagnosis provider tested on standard test data");
    svmDiagnosisProviderTester.testSamples(svmDiagnosisProvider, numNumericOnlyTestSamples, missingDescriptorProbability, 1.0, 1.0, "numericOnly", diagnosableDisorderSet);
    System.err.println("diagnosis provider tested on numeric only test data");
    svmDiagnosisProviderTester.testSamples(svmDiagnosisProvider, numCategoricalOnlyTestSamples, 1.0, missingDescriptorProbability, 1.0, "categoricalOnly", diagnosableDisorderSet);
    System.err.println("diagnosis provider tested on categorical only test data");
    svmDiagnosisProviderTester.testSamples(svmDiagnosisProvider, numImageOnlyTestSamples, 1.0, 1.0, missingDescriptorProbability, "imageOnly", diagnosableDisorderSet);
    System.err.println("diagnosis provider tested on image only test data");
    svmDiagnosisProviderTester.close();
  }


  private static void trainWithRangedCDRs(String configFileName, boolean doTraining) throws IOException, ClassNotFoundException
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
    if (doTraining)
    {
      SVMDiagnosisProvider svmDiagnosisProvider = new SVMDiagnosisProvider();
      svmDiagnosisProvider.train(trainingList, svmRndseed);
      File diagnosisProviderFile = new File(diagnosisProviderFileName);
      ObjectOutputStream diagnosisProviderOut = new ObjectOutputStream(new FileOutputStream(diagnosisProviderFile));
      diagnosisProviderOut.writeObject(svmDiagnosisProvider);
      diagnosisProviderOut.close();
      System.err.println(String.format("diagnosis provider trained and saved to %s", diagnosisProviderFileName));
    }
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
    // FIXME: unnecessary hardwiring to SVMDiagnosisProvider
    SVMDiagnosisProvider svmDiagnosisProvider = (SVMDiagnosisProvider) readDiagnosisProvider(diagnosisProviderFileName);
    // FIXME: should distinguish between missing descriptor probabilities for training and testing
    // FIXME: number of disorder scores hard-coded to 3
    SvmDiagnosisProviderTester svmDiagnosisProviderTester = new SvmDiagnosisProviderTester(rangedCropDisorderRecordList, memoryDB, 3, rng, numericProbabilityDistribution, numericRangeMagnifier, categoricalErrorProbability, testResultFileName);
    svmDiagnosisProviderTester.testSamples(svmDiagnosisProvider, numTestSamples, missingNumericDescriptorProbability, missingCategoricalDescriptorProbability, missingImageDescriptorProbability, testName, memoryDB.findCropDisorderList());
    svmDiagnosisProviderTester.close();
    System.err.println(String.format("diagnosis provider test \"%s\" finished", testName));
  }


  private static void importDiagnosisProvider(String diagnosisProviderFileName) throws NamingException, IOException, ClassNotFoundException
  {
    InitialContext context = new InitialContext();
    Kludge kludge = (Kludge) context.lookup(EjbSupport.sessionJndiName(KludgeBean.class, Kludge.class));
    DiagnosisProvider diagnosisProvider = readDiagnosisProvider(diagnosisProviderFileName);
    kludge.storeDiagnosisProvider(diagnosisProvider);
  }


  private static void rangedCdrsToLatex(String rangedCdrFileName, String latexFileName) throws IOException, ClassNotFoundException
  {
    File rangedCdrFile = new File(rangedCdrFileName);
    BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(rangedCdrFile)));
    List<RangedCropDisorderRecord> rangedCropDisorderRecordList = RangedCropDisorderRecord.parseRangedCropDisorderRecordList(in);
    in.close();
    PrintStream out = new PrintStream(new File(latexFileName));
    for (RangedCropDisorderRecord r : rangedCropDisorderRecordList)
    {
      out.println(r.toLatexTable());
    }
    out.close();
  }


  private static void overlapMatrix(String rangedCdrFileName, String overlapMatrixFileName, String numericOverlapMatrixFileName, String categoricalOverlapMatrixFileName) throws IOException, ClassNotFoundException
  {
    File rangedCdrFile = new File(rangedCdrFileName);
    BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(rangedCdrFile)));
    List<RangedCropDisorderRecord> rangedCropDisorderRecordList = RangedCropDisorderRecord.parseRangedCropDisorderRecordList(in);
    in.close();
    PrintStream out = new PrintStream(new File(overlapMatrixFileName));
    String glue = "";
    for (RangedCropDisorderRecord r : rangedCropDisorderRecordList)
    {
      out.printf("%s%s.%s", glue, r.getCropScientificName(), r.getExpertDiagnosedCropDisorderName());
      glue = "\t";
    }
    out.println();
    for (RangedCropDisorderRecord r1 : rangedCropDisorderRecordList)
    {
      out.printf("%s.%s", r1.getCropScientificName(), r1.getExpertDiagnosedCropDisorderName());
      for (RangedCropDisorderRecord r2 : rangedCropDisorderRecordList)
      {
	out.printf("\t%f", r1.overlap(r2));
      }
      out.println();
    }
    out.close();
    out = new PrintStream(new File(numericOverlapMatrixFileName));
    glue = "";
    for (RangedCropDisorderRecord r : rangedCropDisorderRecordList)
    {
      out.printf("%s%s.%s", glue, r.getCropScientificName(), r.getExpertDiagnosedCropDisorderName());
      glue = "\t";
    }
    out.println();
    for (RangedCropDisorderRecord r1 : rangedCropDisorderRecordList)
    {
      out.printf("%s.%s", r1.getCropScientificName(), r1.getExpertDiagnosedCropDisorderName());
      for (RangedCropDisorderRecord r2 : rangedCropDisorderRecordList)
      {
	out.printf("\t%f", r1.numericOverlap(r2));
      }
      out.println();
    }
    out.close();
    out = new PrintStream(new File(categoricalOverlapMatrixFileName));
    glue = "";
    for (RangedCropDisorderRecord r : rangedCropDisorderRecordList)
    {
      out.printf("%s%s.%s", glue, r.getCropScientificName(), r.getExpertDiagnosedCropDisorderName());
      glue = "\t";
    }
    out.println();
    for (RangedCropDisorderRecord r1 : rangedCropDisorderRecordList)
    {
      out.printf("%s.%s", r1.getCropScientificName(), r1.getExpertDiagnosedCropDisorderName());
      for (RangedCropDisorderRecord r2 : rangedCropDisorderRecordList)
      {
	out.printf("\t%f", r1.categoricalOverlap(r2));
      }
      out.println();
    }
    out.close();
  }


  private static void usage()
  {
    System.out.println("usage: cmdtool <command> [parameters ...]");
    System.out.println("commands are:");
    System.out.println("  diagnosischeck");
    System.out.println("  featuremappercheck");
    System.out.println("  dump <basename>");
    System.out.println("  testsvm <filename> (obsolete, use rtrain and rtest)");
    System.out.println("  rtrain <filename> -- train and serialise diagnosis provider");
    System.out.println("  rtest <filename> -- read serialised diagnosis provider and test it");
    System.out.println("  cdrgen <filename> -- generate CDRs from training (uses same file format as rtrain)");
    System.out.println("  rcdrtex <filename> -- render ranged CDRs in LaTeX");
    System.out.println("  traindb -- train diagnosis provider from database");
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
      trainWithRangedCDRs(args[1], true);
    }
    else if ("cdrgen".equals(command))
    {
      trainWithRangedCDRs(args[1], false);
    }
    else if ("rtest".equals(command))
    {
      testWithRangedCDRs(args[1]);
    }
    else if ("overlap".equals(command))
    {
      overlapMatrix(args[1], args[2], args[3], args[4]);
    }
    else if ("rcdrtex".equals(command))
    {
      rangedCdrsToLatex(args[1], args[2]);
    }
    else if ("traindb".equals(command))
    {
      trainDiagnosisProviderFromDatabase();
    }
    else if ("importdp".equals(command))
    {
      importDiagnosisProvider(args[1]);
    }
    else
    {
      System.err.println(String.format("unknown command \"%s\"", command));
      System.exit(1);
    }
  }
}
