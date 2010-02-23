package org.isacrodi.ejb.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.OneToMany;
import javax.persistence.Version;
import java.util.Set;


@Entity
public class ImageType implements Serializable
{
  private Integer id;
  private int version;
  private String typeName;
  private Set<ImageDescriptor> imageDescriptor;

  private static final long serialVersionUID = 1;


  public ImageType()
  {
    super();
  }
 

  public ImageType(String typeName)
  {
    super();
    this.typeName = typeName;
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


  @OneToMany(mappedBy="imageType")
  public Set<ImageDescriptor> getImageDescriptor()
  {
    return imageDescriptor;
  }


  public void setImageDescriptor(Set<ImageDescriptor> imageDescriptor)
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
