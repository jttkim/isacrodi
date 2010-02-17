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


  public void getFeatureVector()
  {
    //to implement
  }
}
