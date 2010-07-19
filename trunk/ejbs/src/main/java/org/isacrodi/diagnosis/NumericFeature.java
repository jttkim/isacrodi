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
 * Numeric feature vector component.
 *
 * Notice that the order of components is undefined, as feature
 * vectors are implemented by maps -- i.e. remember to match elements
 * by name, not by position.
 */
public class NumericFeature extends AbstractFeature
{

  private Double value;

  public NumericFeature()
  {
    super();
  }


  public NumericFeature(String name, Double value)
  {
    super(name);
    this.value = value;
  }


  public Double getValue()
  {
    return this.value;
  }


  public void setValue(Double value)
  {
    this.value = value;
  }

  public String toString()
  {
    return String.format("%f", this.value);
  }
}
