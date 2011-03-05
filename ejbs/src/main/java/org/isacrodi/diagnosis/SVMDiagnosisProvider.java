package org.isacrodi.diagnosis;

import org.isacrodi.ejb.entity.*;
import java.util.Set;
import java.util.Vector;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collection;

import java.io.IOException;
import java.io.*;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;

import libsvm.*;

import org.isacrodi.util.io.NullOutputStream;


/**
 * Implements Diagnosis Provider Interface
 */
public class SVMDiagnosisProvider implements DiagnosisProvider, Serializable
{
  private CDRFeatureExtractor cdrFeatureExtractor;
  private FeatureVectorMapper<svm_node[]> svmNodeFeatureVectorMapper;
  private svm_model model;
  private Map<CropDisorder, Integer> disorderIndexMap;

  private static final long serialVersionUID = 1;

  // from Hsu et.al. "A Practical Guide to Support Vector Classification"
  private static double GAMMA_MIN = Math.pow(2.0, -15);
  private static double GAMMA_MAX = Math.pow(2.0, 4);
  private static double C_MIN = Math.pow(2.0, -5);
  private static double C_MAX = Math.pow(2.0, 16);


  public SVMDiagnosisProvider()
  {
    super();
    this.cdrFeatureExtractor = new DummyCDRFeatureExtractor();
    this.svmNodeFeatureVectorMapper = null;
    this.model = null;
    this.disorderIndexMap = null;
  }

  /*
  public SVMDiagnosisProvider(String modelFileName, String parseFileName) throws IOException
  {
    // FIXME: dangerous design -- modelFileName and parseFileName have to be consistent!!
    this();
    this.model = svm.svm_load_model(modelFileName);
    this.svmNodeFeatureVectorMapper.parseFile(parseFileName);
  }
  */


  /**
   * Provide a map to look up disorder names by svm label (index).
   */
  public Map<Integer, CropDisorder> makeReverseDisorderIndexMap()
  {
    Map<Integer, CropDisorder> reverseDisorderIndexMap = new HashMap<Integer, CropDisorder>();
    for (CropDisorder cropDisorder : this.disorderIndexMap.keySet())
    {
      reverseDisorderIndexMap.put(this.disorderIndexMap.get(cropDisorder), cropDisorder);
    }
    return (reverseDisorderIndexMap);
  }


  public static String sparseVectorString(svm_node[] v)
  {
    String s = "svm_node(";
    String glue = "";
    for (int i = 0; i < v.length; i++)
    {
      s += glue + String.format("%d: %f", v[i].index, v[i].value);
      glue = ", ";
    }
    s += ")";
    return (s);
  }


  public static void dumpSamples(String fileName, svm_node[][] sample, double[] label)
  {
    try
    {
      PrintStream out = new PrintStream(new File(fileName));
      for (int i = 0; i < label.length; i++)
      {
	out.print(label[i]);
	for (svm_node n : sample[i])
	{
	  out.printf(" %d:%f", n.index, n.value);
	}
	out.println();
      }
      out.close();
    }
    catch (Exception e)
    {
      System.err.println("dumpSamples: " + e);
    }
  }


  /**
   * Determine the cross validation error with {@code fold}-fold cross validation.
   */
  private double crossvalidate(int fold, double[] label, svm_node[][] sample, svm_parameter svmparameter)
  {
    int numSamples = label.length;
    double testSetSize = ((double) numSamples) / fold;
    if (testSetSize < 1.0)
    {
      testSetSize = 1.0;
    }
    int numErrors = 0;
    double testSetLower = 0.0;
    double testSetUpper = testSetLower + testSetSize;
    int iMin = (int) Math.floor(testSetLower);
    int iMax = (int) Math.floor(testSetUpper);
    while (iMax <= numSamples)
    {
      int numTestSamples = iMax - iMin;
      int numTrainingSamples = numSamples - numTestSamples;
      double[] trainingLabel = new double[numTrainingSamples];
      svm_node[][] trainingSample = new svm_node[numTrainingSamples][];
      int j = 0;
      for (int i = 0; i < numSamples; i++)
      {
	if ((i < iMin) || (i >= iMax))
	{
	  trainingLabel[j] = label[i];
	  trainingSample[j] = sample[i];
	  j++;
	}
      }
      /*
      for (int i = 0; i < numTrainingSamples; i++)
      {
	System.err.println(String.format("SVMDiagnosisProvider.crossvalidate: trainingsample %d: label: %f, sample: %s", i, trainingLabel[i], sparseVectorString(trainingSample[i])));
      }
      */
      svm_problem svmproblem = new svm_problem();
      svmproblem.x = trainingSample;
      svmproblem.y = trainingLabel;
      svmproblem.l = svmproblem.y.length;
      svm_model m = null;
      PrintStream systemOut = System.out;
      try
      {
	System.setOut(new PrintStream(new NullOutputStream()));
	m = svm.svm_train(svmproblem, svmparameter);
      }
      finally
      {
	System.setOut(systemOut);
      }
      for (int i = iMin; i < iMax; i++)
      {
	// System.err.println(String.format("SVMDiagnosisProvider.crossvalidate: sample %d / %d, label = %f, %s", i, numSamples, label[i], sparseVectorString(sample[i])));
	double predictedLabel = -1.0;
	try
	{
	  System.setOut(new PrintStream(new NullOutputStream()));
	  predictedLabel = svm.svm_predict(m, sample[i]);
	}
	finally
	{
	  System.setOut(systemOut);
	}
	if (predictedLabel != label[i])
	{
	  numErrors++;
	  // System.err.println(String.format("SVMDiagnosisProvider.crossvalidate: sample %d: expected %f, predicted %f: misclassified", i, label[i], predictedLabel));
	}
	else
	{
	  // System.err.println(String.format("SVMDiagnosisProvider.crossvalidate: sample %d: expected %f, predicted %f: correctly classified", i, label[i], predictedLabel));
	}
      }
      testSetLower = testSetUpper;
      testSetUpper += testSetSize;
      iMin = (int) Math.floor(testSetLower);
      iMax = (int) Math.floor(testSetUpper);
    }
    return (((double) numErrors) / ((double) numSamples));
  }


  private svm_model selectModel(double[] label, svm_node[][] sample)
  {
    svm_parameter svmparameter = new svm_parameter();
    // use svm with slack variables
    svmparameter.svm_type = svm_parameter.C_SVC;
    // use radial basis function
    svmparameter.kernel_type = svm_parameter.RBF;
    // unused?
    svmparameter.degree = 3;
    // gamma = 1 / number of features
    // svmparameter.gamma = 1.0 / ((double) this.svmNodeFeatureVectorMapper.targetSpaceDimension());
    // svmparameter.gamma = 1.0 / 5.0;
    svmparameter.coef0 = 0;
    svmparameter.nu = 0.5;
    svmparameter.cache_size = 100;
    // svmparameter.C = 1;
    svmparameter.eps = 1e-3;
    svmparameter.p = 0.1;
    svmparameter.shrinking = 1;
    svmparameter.probability = 0;
    svmparameter.nr_weight = 0;
    svmparameter.weight_label = new int[0];
    svmparameter.weight = new double[0];
    double bestGamma = GAMMA_MIN;
    double bestC = C_MIN;
    double bestCrossvalidationError = 1.0;
    for (double gamma = GAMMA_MIN; gamma <= GAMMA_MAX; gamma *= 2.0)
    {
      svmparameter.gamma = gamma;
      for (double c = C_MIN; c <= C_MAX; c *= 2.0)
      {
	svmparameter.C = c;
	double crossvalidationError = crossvalidate(10, label, sample, svmparameter);
	// System.err.println(String.format("SVMDiagnosisProvider.selectModel: current: gamma = %e, c = %e, xvalError = %e", gamma, c, crossvalidationError));
	// System.err.println(String.format("SVMDiagnosisProvider.selectModel: best: gamma = %e, c = %e, xvalError = %e", bestGamma, bestC, bestCrossvalidationError));
	// FIXME: 5-fold cross validation hard coded
	if (crossvalidationError < bestCrossvalidationError)
	{
	  bestCrossvalidationError = crossvalidationError;
	  bestC = c;
	  bestGamma = gamma;
	}
      }
    }
    // System.err.println(String.format("SvmDiagnosisProvider.selectModel: bestGamma = %f, bestC = %f", bestGamma, bestC));
    svmparameter.probability = 1;
    svmparameter.C = bestC;
    svmparameter.gamma = bestGamma;
    svm_problem svmproblem = new svm_problem();
    svmproblem.x = sample;
    svmproblem.y = label;
    svmproblem.l = label.length;
    svm_model m = null;
    PrintStream systemOut = System.out;
    try
    {
      System.setOut(new PrintStream(new NullOutputStream()));
      m = svm.svm_train(svmproblem, svmparameter);
    }
    finally
    {
      System.setOut(systemOut);
    }
    return (m);
  }


  public void train(Collection<CropDisorderRecord> labelledCropDisorderRecordSet)
  {
    // System.err.println(String.format("SVMDiagnosisProvider.train: starting, %d labelled CDRs", labelledCropDisorderRecordSet.size()));
    // FIXME: consider defining this mapping as part of the feature vector mappers' responsibilities
    this.disorderIndexMap = new HashMap<CropDisorder, Integer>();
    int maxDisorderIndex = 0;
    Collection<FeatureVector> featureVectorCollection = new HashSet<FeatureVector>();
    for (CropDisorderRecord cropDisorderRecord : labelledCropDisorderRecordSet)
    {
      CropDisorder edd = cropDisorderRecord.getExpertDiagnosedCropDisorder();
      if (edd == null)
      {
	throw new RuntimeException("CDR with no expert diagnosis in labelled set");
      }
      if (!this.disorderIndexMap.containsKey(edd))
      {
	this.disorderIndexMap.put(edd, new Integer(maxDisorderIndex++));
      }
      FeatureVector featureVector = this.cdrFeatureExtractor.extract(cropDisorderRecord);
      featureVectorCollection.add(featureVector);
    }
    this.svmNodeFeatureVectorMapper = new SvmNodeFeatureVectorMapper(featureVectorCollection);
    svm_node[][] sample = new svm_node[labelledCropDisorderRecordSet.size()][];
    double label[] = new double[labelledCropDisorderRecordSet.size()];
    int i = 0;
    for (CropDisorderRecord cropDisorderRecord : labelledCropDisorderRecordSet)
    {
      CropDisorder edd = cropDisorderRecord.getExpertDiagnosedCropDisorder();
      int disorderIndex = this.disorderIndexMap.get(edd).intValue();
      // FIXME: clumsy programming -- duplicate feature vector extraction
      FeatureVector featureVector = this.cdrFeatureExtractor.extract(cropDisorderRecord);
      featureVectorCollection.add(featureVector);
      label[i] = (double) disorderIndex;
      sample[i] = this.svmNodeFeatureVectorMapper.map(featureVector);
      // System.err.println(String.format("SVMDiagnosisProvider.train: featureVector = %s", featureVector.toString()));
      // System.err.println(String.format("SVMDiagnosisProvider.train: svm_node vector = %s", sparseVectorString(sample[i])));
      // System.err.println(String.format("SVMDiagnosisProvider.train: svn_node vector length: %d", sample[i].length));
      i++;
    }
    // System.err.println(this.svmNodeFeatureVectorMapper.toString());
    dumpSamples("sampledump.txt", sample, label);
    this.model = this.selectModel(label, sample);
    try
    {
      svm.svm_save_model("modeldummycdr.txt", this.model);
    }
    catch (Exception e)
    {
      System.err.println("savemodel: " + e);
    }

  }


  public Diagnosis diagnose(CropDisorderRecord cropDisorderRecord)
  {
    if (this.model == null)
    {
      throw new RuntimeException("attempt to diagnose without training first");
    }
    Diagnosis diagnosis = new Diagnosis();
    int[] svmLabels = new int[this.disorderIndexMap.size()];
    svm.svm_get_labels(this.model, svmLabels);
    FeatureVector featureVector = this.cdrFeatureExtractor.extract(cropDisorderRecord);
    // System.err.println("SVMDiagnosisProvider.diagnose: feature vector = " + featureVector.toString());
    svm_node[] fv = this.svmNodeFeatureVectorMapper.map(featureVector);
    // System.err.println(String.format("SVMDiagnosisProvider.diagnose: fv = %s", sparseVectorString(fv)));
    double[] probability = new double[this.disorderIndexMap.size()];
    double predictedLabel = -1.0;
    PrintStream systemOut = System.out;
    try
    {
      System.setOut(new PrintStream(new NullOutputStream()));
      predictedLabel = svm.svm_predict_probability(this.model, fv, probability);
    }
    finally
    {
      System.setOut(systemOut);
    }
    // System.err.println(String.format("SVMDiagnosisProvider.diagnose: predicted label is %f", predictedLabel));
    Map<Integer, CropDisorder> reverseDisorderIndexMap = this.makeReverseDisorderIndexMap();
    for (int i = 0; i < probability.length; i++)
    {
      int disorderIndex = svmLabels[i];
      CropDisorder cropDisorder = reverseDisorderIndexMap.get(new Integer(disorderIndex));
      // FIXME: problem: how to reattach disorders???
      DisorderScore disorderScore = new DisorderScore(probability[i], cropDisorder);
      // System.err.println(String.format("SVMDiagnosisProvider.diagnose: P(%s) = %f", cropDisorder.getScientificName(), probability[i]));
      diagnosis.linkDisorderScore(disorderScore);
    }
    diagnosis.linkCropDisorderRecord(cropDisorderRecord);
    return diagnosis;
  }
}
