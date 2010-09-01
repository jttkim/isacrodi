package org.isacrodi.ejb.entity;

import java.io.File;
import java.io.FileInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.javamisc.Util;
import org.javamisc.jee.entitycrud.CrudConfig;


/**
 * Implements image descriptor from Digital Image.
 */
@Entity
@CrudConfig(propertyOrder = {"id", "cropDisorderRecord", "descriptorType", "mimeType", "*"})
public class ImageDescriptor extends Descriptor
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


  public void setImageData(byte[] imageData)
  {
    this.imageData = imageData;
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


  public void unlink()
  {
    super.unlink();
  }


  public void readImageData(String fileName) throws IOException
  {
    File f = new File(fileName);
    long fileLength = f.length();
    FileInputStream fis = new FileInputStream(f);
    this.imageData = new byte[(int) fileLength];
    fis.read(this.imageData);
    fis.close();
  }


  public String makeFileName()
  {
    return (String.format("isacrodi_image_%05d.dat", this.id.intValue()));
  }


  public String toString()
  {
    return (String.format("ImageDescriptor(id = %s, mimeType = %s)", Util.safeStr(this.id), Util.safeStr(this.mimeType)));
  }
}
