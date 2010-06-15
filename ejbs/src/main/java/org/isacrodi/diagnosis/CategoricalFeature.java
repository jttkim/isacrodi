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
 * Categorical Feature vector.
 *
 */
public class CategoricalFeature extends AbstractFeature
{
  
  private String value;

  public CategoricalFeature()
  {
    super();
  }


  public CategoricalFeature(String name, String value)
  {
    super(name);
    this.value = value;
  }


  public String getValue()
  {
    return this.value;
  }


  public void setValue(String value)
  {
    this.value = value;
  }


  public String toString()
  {
    return String.format("%s",this.value);
  }

}
