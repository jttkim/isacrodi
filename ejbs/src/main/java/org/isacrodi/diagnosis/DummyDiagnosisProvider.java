package org.isacrodi.diagnosis;

import org.isacrodi.ejb.entity.*;
import org.isacrodi.diagnosis.libsvm.*;
import java.io.IOException;
import java.util.Set;
import java.util.Vector;
import java.util.HashSet;
import java.io.*;
import java.io.ByteArrayOutputStream;

/**
 * Implements Diagnosis Provider Interface
 */
public class DummyDiagnosisProvider implements DiagnosisProvider
{
  private Set<CropDisorder> cropDisorderSet;
  private svm_parameter param; 
  private svm_problem prob;
  private svm_model model;
  private String input_file_name;
  private String model_file_name;
  private String error_msg;
  private int cross_validation;
  private int nr_fold;

  
  public DummyDiagnosisProvider()
  {
    super();
  }


  public void setKnownDisorderSet(Set<CropDisorder> cropDisorderSet)
  {
    this.cropDisorderSet = cropDisorderSet;
  }


  public Diagnosis diagnose(CropDisorderRecord cropDisorderRecord)
  {
    Diagnosis d = new Diagnosis();
    d.setCropDisorderRecord(cropDisorderRecord);
    d.setDisorderScoreSet(new HashSet<DisorderScore>());
    double [] a = new double[2];

    // For testing purposes
    double [] sample = new double[7];
    sample[0] = 1;
    sample[1] = 10;
    sample[2] = 11;
    sample[3] = 12;
    sample[4] = 13;
    sample[5] = 14;
    sample[6] = 15;

    for (CropDisorder disorder : this.cropDisorderSet)
    {
      DisorderScore ds = new DisorderScore();
      if(cropDisorderRecord.getCrop() == null)
        ds.setScore(1.0 / this.cropDisorderSet.size());
      else if (disorder.getCropSet().contains(cropDisorderRecord.getCrop()))
        ds.setScore(1.0 / CountCrop(cropDisorderRecord.getCrop()));
      else
	ds.setScore(0.0); 
      ds.setDiagnosis(d);
      ds.setCropDisorder(disorder);
      d.addDisorderScore(ds);
    }
    
    createClassifierSettings();
    loadFeatureVector();
    model = svm.svm_train(prob, param);

    svm_predict p = new svm_predict();
    a = p.predict(model, sample, 1);

    return (d);
  }


  public int CountCrop(Crop crop)
  {

    int counter = 0;

    for(CropDisorder cdi : this.cropDisorderSet)
    {
      if(cdi.getCropSet().contains(crop))
        counter++;
    }

    return counter;
  }


  public Diagnosis diagnoseOld(CropDisorderRecord cropDisorderRecord)
  {
   
    CDRFeatureExtractor c = new DummyCDRFeatureExtractor();
    FeatureVector dfv = c.extract(cropDisorderRecord);

    ImageFeatureExtractor ie = new DummyImageFeatureExtractor();
    ImageDescriptor imageDescriptorSet = getImageDescriptorSet(cropDisorderRecord);
    FeatureVector ifv = ie.extract(imageDescriptorSet);
    
    FeatureVector featureVector = new FeatureVector();

    for (String k : dfv.keySet())
      featureVector.put(k, dfv.get(k));
    for (String k : ifv.keySet())
      featureVector.put(k, ifv.get(k));
   
    featureVector.put("crop", (double)cropDisorderRecord.getCrop().getId());

    FeatureClassifier cl = new FeatureClassifier();
    cl.DummyClassifier(featureVector, cropDisorderRecord.getDiagnosis().getDisorderScoreSet());

    return (cropDisorderRecord.getDiagnosis());   
  }


  public ImageDescriptor getImageDescriptorSet(CropDisorderRecord cropDisorderRecord)
  {
     ImageDescriptor ides = new ImageDescriptor();

     for (Object o : cropDisorderRecord.getDescriptorSet())
     {
       if (o.getClass().isInstance(new ImageDescriptor()))
       {
         ides = (ImageDescriptor)o;
       }
     }
     return ides;
  }


  public NumericDescriptor getNumericDescriptorSet(CropDisorderRecord cropDisorderRecord)
  {
     NumericDescriptor ndes = new NumericDescriptor();

     for (Object o : cropDisorderRecord.getDescriptorSet())
     {
       if (o.getClass().isInstance(new NumericDescriptor()))
       {
         ndes = (NumericDescriptor)o;
       }
     }
     return ndes;
  }


  public void createClassifierSettings()
  {
    param = new svm_parameter();
    param.svm_type = svm_parameter.C_SVC;
    param.kernel_type = svm_parameter.RBF;
    param.degree = 3;
    param.gamma = 0;
    param.coef0 = 0;
    param.nu = 0.5;
    param.cache_size = 100;
    param.C = 1;
    param.eps = 1e-3;
    param.p = 0.1;
    param.shrinking = 1;
    param.probability = 1;
    param.nr_weight = 0;
    param.weight_label = new int[0];
    param.weight = new double[0];


  }

  public void loadFeatureVector()
  {

    Vector<Double> vy = new Vector<Double>();
    Vector<svm_node[]> vx = new Vector<svm_node[]>();
    int max_index = 0;

    // For testing purposes only

    double [] a = new double[6];
    a[0] = 1;
    a[1] = 1;
    a[2] = 1;
    a[3] = 0;
    a[4] = 0;
    a[5] = 0;

    for(int i=0; i<6; i++)
    {
      vy.addElement(a[i]);
      int m = 6;
      svm_node[] x = new svm_node[m];
      for(int j = 0; j < m; j++)
      {
        x[j] = new svm_node();
        x[j].index = j+1;
        x[j].value = 10+j;
      }
      if(m>0) max_index = Math.max(max_index, x[m-1].index);
      vx.addElement(x);
    }

    prob = new svm_problem();
    prob.l = vy.size();
    prob.x = new svm_node[prob.l][];

    for(int i = 0; i < prob.l; i++)
      prob.x[i] = vx.elementAt(i);
    prob.y = new double[prob.l];
    for(int i = 0; i < prob.l; i++)
      prob.y[i] = vy.elementAt(i);
    if(param.gamma == 0 && max_index > 0)
      param.gamma = 1.0/max_index;

    if(param.kernel_type == svm_parameter.PRECOMPUTED)
      for(int i = 0; i < prob.l; i++)
      {
        if (prob.x[i][0].index != 0)
        {
          System.err.print("Wrong kernel matrix: first column must be 0:sample_serial_number\n");
          System.exit(1);
        }
        if ((int)prob.x[i][0].value <= 0 || (int)prob.x[i][0].value > max_index)
        {
          System.err.print("Wrong input format: sample_serial_number out of range\n");
          System.exit(1);
        }
       }
  }
}

