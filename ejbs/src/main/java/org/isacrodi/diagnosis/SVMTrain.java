package org.isacrodi.diagnosis;


import org.isacrodi.ejb.entity.*;
import java.io.*;
import libsvm.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
  * SVM Feature classifier
**/ 
public class SVMTrain
{

  private svm_parameter svmparameter;
  private svm_problem svmproblem;


  public SVMTrain()
  {
    this.svmparameter = new svm_parameter();
    this.svmproblem = new svm_problem();
  }

  
  public svm_model classify(HashMap hm, int max)
  {
   
    svm_model svmmodel = new svm_model();
    setClassifierParameter();
    loadTrainingData(hm, max);
    svmmodel = svm.svm_train(this.svmproblem, this.svmparameter);
    return svmmodel;

  }

  public void setClassifierParameter()
  {

  // default values
    this.svmparameter.svm_type = svm_parameter.C_SVC;
    this.svmparameter.kernel_type = svm_parameter.RBF;
    this.svmparameter.degree = 3;
    this.svmparameter.gamma = 0;
    this.svmparameter.coef0 = 0;
    this.svmparameter.nu = 0.5;
    this.svmparameter.cache_size = 100;
    this.svmparameter.C = 1;
    this.svmparameter.eps = 1e-3;
    this.svmparameter.p = 0.1;
    this.svmparameter.shrinking = 1;
    this.svmparameter.probability = 1;
    this.svmparameter.nr_weight = 0;
    this.svmparameter.weight_label = new int[0];
    this.svmparameter.weight = new double[0];

  }


  public void loadTrainingData(HashMap hm, int max)
  {
   
    this.svmproblem = new svm_problem();
    this.svmproblem.l = hm.size();
    this.svmproblem.x = new svm_node[this.svmproblem.l][];
    this.svmproblem.y = new double[this.svmproblem.l];

    int i = 0;
    for(Object k : hm.keySet())
    {
      this.svmproblem.x[i] = (svm_node[])hm.get(k);
      this.svmproblem.y[i] = (Double)k;
      i++;
    }

    if(this.svmparameter.gamma == 0 && max > 0)
      this.svmparameter.gamma = 1.0 / max;

  }
}

