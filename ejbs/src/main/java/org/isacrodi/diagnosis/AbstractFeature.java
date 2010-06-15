package org.isacrodi.diagnosis;

import java.util.HashMap;
import java.awt.image.*;
import java.awt.Image;
import java.io.*;
import java.sql.*;
import java.io.File;
import javax.imageio.ImageIO;
import java.util.*;


/**
 * Asbtract Feature
 */
public class AbstractFeature
{
  private String name;


  public AbstractFeature()
  {  
    super();
  }

  public AbstractFeature(String name)
  {
    this.name = name;
  }

  
  public String getName()
  {
    return this.name;
  }


  public void setName(String name)
  {
    this.name = name;
  }

  public String toString()
  {
    return String.format("%s", this.name);
  }

}

