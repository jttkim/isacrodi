package org.isacrodi.diagnosis;

import java.util.HashMap;
import java.awt.image.*;
import java.awt.Image;
import java.io.*;
import java.sql.*;
import java.io.File;
import java.util.*;


/**
 * Feature vector.
 *
 * Notice that the order of components is undefined, as feature
 * vectors are implemented by maps -- i.e. remember to match elements
 * by name, not by position.
 */
public class FeatureVector extends HashMap<String, AbstractFeature>
{

  public FeatureVector()
  {
    super();
  }


  public void putFeature(AbstractFeature f)
  {
    // FIXME: should superseding a feature be allowed?
    this.put(f.getName(), f);
  }


  /**
   * Produces a Python dictionary style string of this feature vector.
   *
   * @return a dictionary style string
   */
  public String toString()
  {
    String s = "{";
    String glue = "";
    for (String k : this.keySet())
    {
      s += String.format("%s%s: %s", glue, k, this.get(k).toString());
      glue = ", ";
    }
    s += "}";
    return (s);
  }
}
