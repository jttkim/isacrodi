package org.isacrodi.diagnosis;


import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.FileNotFoundException;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.util.ArrayList;
import java.util.Collection;


import org.isacrodi.ejb.entity.*;


public class MainClass implements Serializable
{

  public Collection<FeatureMapper> featureMapper = new ArrayList<FeatureMapper>();


  public void populateSet()
  {
  
    FeatureMapper fm00 = new FeatureMapper(1, "visual_symptom");
    FeatureMapper fm01 = new FeatureMapper(2, "crop");
    FeatureMapper fm02 = new FeatureMapper(3, "temperature");
  
    ObjectOutputStream objectOut = null;

    try 
    {

      objectOut = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream("FeatureMapperObjects.bin")));
      objectOut.writeObject(fm00);
      objectOut.writeObject(fm01);
      objectOut.writeObject(fm02);
      objectOut.close();
    
    } 
    catch (FileNotFoundException ex) 
    {
       ex.printStackTrace();
    }
    catch (IOException ex) 
    {
       ex.printStackTrace();
    }

  }

  public void readSet()
  {
    ObjectInputStream objectIn = null;
  
    try 
    {
      objectIn = new ObjectInputStream(new BufferedInputStream(new FileInputStream("FeatureMapperObjects.bin")));
      Object obj = null;
      while ((obj = objectIn.readObject()) != null) 
      {
        if (obj instanceof FeatureMapper) 
        {
	   this.featureMapper.add((FeatureMapper)obj); 
        }
      }

    objectIn.close();
    }
    catch (EOFException ex) 
    {
    }
    catch (FileNotFoundException ex) 
    {
       ex.printStackTrace();
    }
    catch (IOException ex) 
    {
       ex.printStackTrace();
    }
    catch (ClassNotFoundException ex) 
    {
       ex.printStackTrace();
    }
  }


  public int findFeatureLabel(String feature_name)
  {
    int featureLabel = 0;

    for(FeatureMapper fm : this.featureMapper) 
    {
      if (fm.getFeatureName().equalsIgnoreCase(feature_name)) 
      {
         featureLabel = fm.getFeatureLabel();
	 break;
      }
    }     
    return featureLabel;
  }

}

