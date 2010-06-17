package org.isacrodi.diagnosis;


import org.isacrodi.ejb.entity.*;
import java.io.*;
import libsvm.*;
import java.util.HashMap;

/**
  * Score Table
  */

public class ScoreTable
{

  private HashMap<String, Double> scoreMap;


  public ScoreTable()
  {
    this.scoreMap = new HashMap<String, Double>();
  }


  public void addScore(String label, double score)
  {
    if (this.scoreMap.containsKey(label))
    {
      throw new IllegalArgumentException(String.format("score \"%s\" already mapped", label));
    }
    this.scoreMap.put(label, new Double(score));
  }


  public double getScore(String label)
  {
    double score = 0.0;

    for(String s : this.scoreMap.keySet())
    {
      if(s.equals(label))
      {
        score = this.scoreMap.get(s);
	break;
      }
    }
    return score;
  }


  public String toString()
  {
    String s = String.format("ScoreTable (");
    for (String label : scoreMap.keySet())
    {
      s += String.format("  Label %s -> Score  %f",  label, this.scoreMap.get(label));
    }
    return (s + ")");
  }

}
