package org.isacrodi.diagnosis;

import java.io.*;
import java.util.*;
import libsvm.*;


/**
 * SVM feauture class prediction
 */

public class SVMPredict 
{

  public SVMPredict()
  {
    super();
  }
  
  public ScoreTable predict(svm_model model, svm_node[] x)
  {

    int nr_class = svm.svm_get_nr_class(model);
    double[] prob_estimates = null;
    int[] labels = new int[nr_class];
    double v;

    svm.svm_get_labels(model,labels);
    prob_estimates = new double[nr_class];
   
    v = svm.svm_predict_probability(model, x, prob_estimates);
    ScoreTable score = new ScoreTable();

    for(int i = 0; i < prob_estimates.length; i++) 
    {
      score.addScore(Integer.toString(labels[i]),prob_estimates[i]);
    }
   
  return score;
  }
}
