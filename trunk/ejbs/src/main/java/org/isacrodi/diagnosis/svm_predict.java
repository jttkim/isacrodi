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
  
  public double [] predict(svm_model model, double [] sample, int predict_probability)
  {

    int correct = 0;
    int total = 0;
    double error = 0;
    double sumv = 0, sumy = 0, sumvv = 0, sumyy = 0, sumvy = 0;
    double [] resultClass = new double[2];

    int svm_type = svm.svm_get_svm_type(model);
    int nr_class = svm.svm_get_nr_class(model);
    double[] prob_estimates = null;

    if(predict_probability == 1)
    {
       if(svm_type == svm_parameter.EPSILON_SVR || svm_type == svm_parameter.NU_SVR)
       {
          System.out.print("Prob. model for test data: target value = predicted value + z,\nz: Laplace distribution e^(-|z|/sigma)/(2sigma),sigma="+svm.svm_get_svr_probability(model)+"\n");
       }
       else
       {
         int[] labels = new int[nr_class];
         svm.svm_get_labels(model,labels);
         prob_estimates = new double[nr_class];
       }
    }
     
    double target = sample[0];
    svm_node[] x = new svm_node[sample.length-1];
    for(int i = 0; i < sample.length-1; i++)
    {
      x[i] = new svm_node();
      x[i].index = i+1;
      x[i].value = sample[i+1];
    }


    double v;

    if (predict_probability == 1 && (svm_type == svm_parameter.C_SVC || svm_type == svm_parameter.NU_SVC))
    {
      v = svm.svm_predict_probability(model, x, prob_estimates);
      for(int j = 0; j < nr_class; j++)
      {
        System.out.println("CLASS PREDICTED: " + v + "%" + prob_estimates[j]);
	resultClass[0] = v;
	resultClass[1] = prob_estimates[j];
	
      }
    }
    else
    {
      v = svm.svm_predict(model,x);
    }

    if(v == target)
      ++correct;
    error += (v-target)*(v-target);
    sumv += v;
    sumy += target;
    sumvv += v*v;
    sumyy += target*target;
    sumvy += v*target;
    ++total;
     

    if(svm_type == svm_parameter.EPSILON_SVR || svm_type == svm_parameter.NU_SVR)
    {
      System.out.print("Mean squared error = "+error/total+" (regression)\n");
      System.out.print("Squared correlation coefficient = "+
      ((total*sumvy-sumv*sumy)*(total*sumvy-sumv*sumy))/
                              ((total*sumvv-sumv*sumv)*(total*sumyy-sumy*sumy))+
                              " (regression)\n");
    }
    else
      System.out.print("Accuracy = "+(double)correct/total*100+ "% ("+correct+"/"+total+") (classification)\n");
  return resultClass;
  }

}
