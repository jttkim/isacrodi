package org.isacrodi.ejb.entity;

import java.io.Serializable;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

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
    this();
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


  public BufferedImage bufferedImage() throws IOException
  {
    boolean mimeSupported = false;
    for (String supportedMimeType : ImageIO.getReaderMIMETypes())
    {
      mimeSupported |= this.mimeType.equals(supportedMimeType);
    }
    if (mimeSupported)
    {
      ByteArrayInputStream b = new ByteArrayInputStream(this.imageData);
      return(ImageIO.read(b));
    }
    else
    {
      throw new IllegalStateException(String.format("cannot construct image for MIME type %s", this.mimeType));
    }
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
