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

  
  public Collection<ScoreTable> dummyClassifier(FeatureVector featureVector) 
  {

   String model_filename;
   Collection<ScoreTable> scoreTable = new ArrayList<ScoreTable>();
   svm_node[] fv = new svm_node[featureVector.size()];

   model_filename = "src/test/java/org/isacrodi/diagnosis/isacrodi_model";

   try
   {
     svm_model model = svm.svm_load_model(model_filename);
     fv = mapFeature(featureVector);
     svm_predict p = new svm_predict();
     scoreTable = p.predict(model, fv);
   }
   catch(IOException e) 
   { 
     System.out.println("Unable to create " + model_filename + ": " + e.getMessage());
   }
    return scoreTable;

  }

  
  public svm_node[] mapFeature(FeatureVector featureVector)
  {

    //Mapper mp = new Mapper();
    //try {
    //  mp.importFile("/home/bkx08wju/Stuff/isacrodi/trunk/sampledata/isacrodi_feature_mapper.txt");
   // }
    //catch (IOException ex) 
    //{
    //  ex.printStackTrace();
    //}

    //MainClass mc = new MainClass();
    //mc.populateSet();
    //mc.readSet();

    int i = 0;

    svm_node[] fv = new svm_node[featureVector.size()];
    for (String k : featureVector.keySet())
    {
      fv[i] = new svm_node();
      //fv[i].index = mc.findFeatureLabel(k);
      fv[i].index = 1;
      //fv[i].value = featureVector.get(k);
      fv[i].value = 2.00;
      i++;
    }
    return fv;
  }

}

