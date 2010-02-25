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


/**
 * Feature vector.
 *
 * Notice that the order of components is undefined, as feature
 * vectors are implemented by maps -- i.e. remember to match elements
 * by name, not by position.
 */
public class FeatureVector extends HashMap<String, Double>
{
  public FeatureVector()
  {  
    super();
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
      s += String.format("%s%s: %f", glue, k, this.get(k));
      glue = ", ";
    }
    s += "}";
    return (s);
  }
}
