package org.isacrodi.diagnosis;

import java.util.HashMap;
import java.awt.image.*;
import java.awt.Image;
import java.io.*;
import java.sql.*;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.*;
import org.isacrodi.ejb.entity.*;


/**
 * Categorical Feature.
 */
public class CategoricalFeature extends AbstractFeature
{

  private Set<String> stateSet;


  public CategoricalFeature()
  {
    super();
    this.stateSet = new HashSet<String>();
  }


  public CategoricalFeature(String name, Set<CategoricalTypeValue> categoricalTypeValue)
  {
    super(name);
    this.stateSet = new HashSet<String>();
    for(CategoricalTypeValue ctp : categoricalTypeValue)
    {
      this.stateSet.add(ctp.getValueType());
    }
  }


  public Set<String> getStateSet()
  {
    return this.stateSet;
  }


  public void setStateSet(Set<String> stateSet)
  {
    this.stateSet = stateSet;
  }


  public boolean containsState(String stateName)
  {
    return (this.stateSet.contains(stateName));
  }


  public String toString()
  {
    String s = "CategoricalFeature(stateSet = {";
    String glue = "";
    for (String state : this.stateSet)
    {
      s += glue + state;
      glue = ", ";
    }
    return (s + "}");
  }
}
