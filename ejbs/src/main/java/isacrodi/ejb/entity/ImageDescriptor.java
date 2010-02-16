package isacrodi.ejb.entity;


import javax.persistence.Entity;
import javax.persistence.OneToOne;
import java.io.Serializable;



@Entity
public class ImageDescriptor extends Descriptor implements Serializable
{

  public final CategoryType categoryType =  CategoryType.IMAGE;
  private ImageType imageType;


  ImageDescriptor()
  {
    super();
  }

  @OneToOne(mappedBy="imageDescriptor")  
  public ImageType setImageType()
  {
    return imageType;
  }


  public void getImageType(ImageType imageType)
  {
    this.imageType = imageType;
  }


  public void getFeatureVector()
  {
    //to implement
  }
}
