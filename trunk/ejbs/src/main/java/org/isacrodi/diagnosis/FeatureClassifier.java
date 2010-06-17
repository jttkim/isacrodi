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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

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

   Collection<ScoreTable> scoreTable = new ArrayList<ScoreTable>();

   model_filename = "src/test/java/org/isacrodi/diagnosis/isacrodi_model";

   try
   {
     svm_model model = svm.svm_load_model(model_filename);
     svm_predict p = new svm_predict();
     score = p.predict(model, node);
   }
   catch(IOException e) 
   { 
     System.out.println("Unable to create " + model_filename + ": " + e.getMessage());
   }
    return score;

  }
}

