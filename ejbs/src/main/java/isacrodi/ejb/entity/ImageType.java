package isacrodi.ejb.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.OneToOne;
import javax.persistence.Version;
import java.io.Serializable;


@Entity
public class ImageType implements Serializable
{
  private Integer id;
  private int version;
  private String typeName;
  private ImageDescriptor imageDescriptor;

  private static final long serialVersionUID = 1;


  ImageType()
  {
    super();
  }
 

  @Id
  @GeneratedValue
  public Integer getId()
  {
    return id;
  }


  public void setId(Integer id)
  {
    this.id = id;
  }


  @OneToOne(mappedBy="imageType")
  public ImageDescriptor getImageDescriptor()
  {
    return imageDescriptor;
  }


  public void setImageDescriptor(ImageDescriptor imageDescriptor)
  {
    this.imageDescriptor = imageDescriptor;
  }



  public String getTypeName()
  {
    return typeName;
  }


  public void setImageType(String typeName)
  {
    this.typeName = typeName;
  }


  @Version
  public int getVersion()
  {
    return (this.version);
  }


  public void setVersion(int version)
  {
    this.version = version;
  }


}
