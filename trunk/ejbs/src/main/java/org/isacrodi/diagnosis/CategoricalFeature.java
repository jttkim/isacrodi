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
 * Categorical Feature.
 *
 */
public class CategoricalFeature extends AbstractFeature
{
  
  private String state;

  public CategoricalFeature()
  {
    super();
  }


  public CategoricalFeature(String name, String state)
  {
    super(name);
    this.state = state;
  }


  public String getState()
  {
    return this.state;
  }


  public void setState(String state)
  {
    this.state = state;
  }


  public String toString()
  {
    return String.format("%s", this.state);
  }

}
