package org.isacrodi.ejb.entity;

import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

/**
  * Implements image descriptor from Digital Image.
  */
@Entity
public class ImageDescriptor extends Descriptor implements Serializable
{
  private ImageType imageType;
  private String mimeType;
  private byte[] imageData;

  private static final long serialVersionUID = 1;


  public ImageDescriptor()
  {
    super();
    this.mimeType = "unkown";
  }


  public ImageDescriptor(String mimeType, byte[] imageData)
  {
    this();
    this.mimeType = mimeType;
    this.imageData = imageData;
  }


  public ImageDescriptor(ImageType imageType, String mimeType, String imageFileName) throws IOException
  {
    this();
    // FIXME: should make sure that relationships are recorded bidirectionally
    this.imageType = imageType;
    this.mimeType = mimeType;
    this.readImageData(imageFileName);
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


  /* JTK: bad name as this is not an accessor (bad return type as well)
  public void getFeatureVector()
  {
    //to implement
  }
  */


  public void readImageData(String fileName) throws IOException
  {
    File f = new File(fileName);
    long fileLength = f.length();
    FileInputStream fis = new FileInputStream(f);
    this.imageData = new byte[(int) fileLength];
    fis.read(this.imageData);
    fis.close();
  }
}
