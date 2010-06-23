package org.isacrodi.diagnosis;


import org.isacrodi.ejb.entity.*;
import java.io.*;
import libsvm.*;
import java.io.Serializable;

/**
  * Feature classifier
  */
public class FeatureClassifier implements Serializable
{

  public FeatureClassifier()
  {
    super();
  }

  
  public ScoreTable dummyClassifier(svm_node [] node) 
  {

   String model_filename;
   ScoreTable score = new ScoreTable();


   model_filename = "src/test/java/org/isacrodi/diagnosis/isacrodi_model";

   try
   {
     svm_model model = svm.svm_load_model(model_filename);
     SVMPredict p = new SVMPredict();
     score = p.predict(model, node);
   }
   catch(IOException e) 
   { 
     System.out.println("Unable to create " + model_filename + ": " + e.getMessage());
   }
    return score;

  }
}

