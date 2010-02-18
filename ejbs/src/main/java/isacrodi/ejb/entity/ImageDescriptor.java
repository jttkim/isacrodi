package isacrodi.ejb.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.OneToOne;


@Entity
public class ImageDescriptor extends Descriptor implements Serializable
{
  // JTK: redundant recording of class
  public final CategoryType categoryType =  CategoryType.IMAGE;
  private ImageType imageType;
  private String mimeType;
  private byte[] imageData;

  private static final long serialVersionUID = 1;


  ImageDescriptor()
  {
    super();
  }

  // JTK: seems to me this should be ManyToOne (see NumericType)
  @OneToOne
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


  public void setMimetype()
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


  // method to get image as java.awt... object to encapsulate low-level octet stream and MIME type stuff...


  public void getFeatureVector()
  {
    //to implement
  }
}
