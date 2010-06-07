package org.isacrodi.diagnosis;

import java.io.*;
import java.util.*;
import libsvm.*;


class svm_predict 
{

  public svm_predict()
  {
    super();
  }
  
  public double[][] predict(svm_model model, svm_node[] x)
  {

    double error = 0;

    int svm_type = svm.svm_get_svm_type(model);
    int nr_class = svm.svm_get_nr_class(model);
    double[] prob_estimates = null;
    double [][] score = new double[nr_class][2]; 
    int[] labels = new int[nr_class];


    svm.svm_get_labels(model,labels);
    prob_estimates = new double[nr_class];
   
    /*
    svm_node[] x = new svm_node[featureVector.size()];
    for(int i = 0; i < featureVector.size(); i++)
    {
      x[i] = new svm_node();
      x[i].index = i+1;
      x[i].value = featureVector.get(Integer.toString(i+1));
    }
    */
    double v;

    v = svm.svm_predict_probability(model, x, prob_estimates);
     
    for(int i=0; i < prob_estimates.length; i++) {
      score[i][0] = labels[i];
      score[i][1] = prob_estimates[i];
    }
   
  return score;
  }


}
