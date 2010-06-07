package org.isacrodi.diagnosis;


import org.isacrodi.ejb.entity.*;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.util.Set;
import java.util.Vector;
import java.util.HashSet;
import java.io.*;
import java.io.ByteArrayOutputStream;
import libsvm.*;


/**
  * Feature classifier
  */
public class FeatureClassifier 
{

  private svm_parameter param; 
  private svm_problem prob;
  private svm_model model;
  private String input_file_name;
  private String model_filename;
  private String error_msg;
  private int cross_validation;
  private int nr_fold;


  public FeatureClassifier()
  {
    super();
  }

  
  public double [][] dummyClassifier(FeatureVector featureVector)
  {
   double [][] score = null;

   model_filename = "src/test/java/org/isacrodi/diagnosis/isacrodi_model";

   try
   { 
     svm_model model = svm.svm_load_model(model_filename);
     int nr_class = svm.svm_get_nr_class(model);
     int[] labels = new int[nr_class];
     svm.svm_get_labels(model, labels);
     createClassifierSettings();
     // loadFeatureVector();
     svm_predict p = new svm_predict();
     score = p.predict(model, featureVector);

   }
   catch(IOException e) 
   { 
     System.out.println("Unable to create "+model_filename+": "+e.getMessage());
   }

    return score;

  }

/*
 * Create classifier settings
*/
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


/**
Create dummy training data

*/
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

