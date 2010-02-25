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
  Image feature extractor.\It extracts features form an image buffered.
  */

public class FeatureVector extends HashMap<String, Double>
{
  
  public double mean;


  public FeatureVector()
  {  
    super();
  }

  
  public FeatureVector(BufferedImage bufferedImage)
  {
    super();
    this.mean = calculateMean(bufferedImage);
  }


  public double calculateMean(BufferedImage bufferedImage)
  {

    double sum = 0.0;
    Raster raster = bufferedImage.getRaster();

    for(int i = 0; i < bufferedImage.getHeight(); ++i)
    {
      for(int j = 0; j < bufferedImage.getWidth(); ++j)
      {
        sum = sum + raster.getSample(j,i, 0);
      }
    }

    return (sum / (bufferedImage.getWidth() * bufferedImage.getHeight()));
  }
}
