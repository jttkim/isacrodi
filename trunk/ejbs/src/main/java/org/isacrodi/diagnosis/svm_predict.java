package org.isacrodi.diagnosis;

import java.io.*;
import java.util.*;
import libsvm.*;
import java.util.ArrayList;
import java.util.Collection;


class svm_predict 
{

  public svm_predict()
  {
    super();
  }
  
  public Collection<ScoreTable> predict(svm_model model, svm_node[] x)
  {

    Collection<ScoreTable> scoreTable = new ArrayList<ScoreTable>();

    int nr_class = svm.svm_get_nr_class(model);
    double[] prob_estimates = null;
    int[] labels = new int[nr_class];
    double v;

    svm.svm_get_labels(model,labels);
    prob_estimates = new double[nr_class];
   
    v = svm.svm_predict_probability(model, x, prob_estimates);
    for(int i = 0; i < prob_estimates.length; i++) 
    {
      ScoreTable st = new ScoreTable();
      st.setLabel(Integer.toString(labels[i]));
      st.setScore(prob_estimates[i]);
      scoreTable.add(st);
    }
   
  return scoreTable;
  }


}
