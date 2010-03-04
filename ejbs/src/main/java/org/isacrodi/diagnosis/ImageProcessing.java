package org.isacrodi.diagnosis;

import java.io.IOException;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;

import org.isacrodi.ejb.entity.*;


public class ImageProcessing 
{

  private BufferedImage bufferedImage;

  public ImageProcessing()
  {
    super();
  }

  
  public ImageProcessing(BufferedImage bufferedImage)
  {
    this();
    this.bufferedImage = bufferedImage;
  }


  public double calculatePixelMean()
  {
    double sum = 0.0;
    Raster raster = bufferedImage.getRaster();

    for(int i = 0; i < bufferedImage.getHeight(); ++i)
    {
      for(int j = 0; j < bufferedImage.getWidth(); ++j)
      {
        sum = sum + raster.getSample(j, i, 0);
      }
    }
    return (sum / (bufferedImage.getWidth() * bufferedImage.getHeight()));
  }

}

