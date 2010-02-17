package isacrodi.ejb.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.OneToOne;
import javax.persistence.Version;


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


  @Version
  public int getVersion()
  {
    return (this.version);
  }


  public void setVersion(int version)
  {
    this.version = version;
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


  public void setTypeName(String typeName)
  {
    this.typeName = typeName;
  }
}
