package org.isacrodi.ejb.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.awt.image.BufferedImage;

/**
  * Implements image descriptor from Digital Image
 */
@Entity
public class ImageDescriptor extends Descriptor implements Serializable
{
  private ImageType imageType;
  private String mimeType;
  private byte[] imageData;
  private BufferedImage bufferedImage;

  private static final long serialVersionUID = 1;


  public ImageDescriptor()
  {
    super();
  }


  public ImageDescriptor(String mimeType, byte[] imageData)
  {
    super();
    this.mimeType = mimeType;
    this.imageData = imageData;
  }


  @ManyToOne
  public ImageType getImageType()
  {
    return imageType;
  }


  public void setImageType(ImageType imageType)
  {
    this.imageType = imageType;
  }


  public String getMimeType()
  {
    return (this.mimeType);
  }


  public void setMimeType(String mimeType)
  {
    this.mimeType = mimeType;
  }


  public byte[] getImageData()
  {
    return (this.imageData);
  }


  public void setBufferedImage(BufferedImage bufferedImage)
  {
    this.bufferedImage = bufferedImage;
  }


  public BufferedImage getBufferedImage()
  {
    return (this.bufferedImage);
  }


  public void setImageData(byte[] imageData)
  {
    this.imageData = imageData;
  }

  // method to get image as java.awt... object to encapsulate low-level octet stream and MIME type stuff...


  public void getFeatureVector()
  {
    //to implement
  }
}
