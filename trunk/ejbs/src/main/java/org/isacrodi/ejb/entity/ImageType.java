package org.isacrodi.ejb.entity;

import javax.persistence.Entity;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.OneToMany;
import javax.persistence.Version;

import java.util.Set;
import java.util.HashSet;


@Entity
public class ImageType implements IsacrodiEntity
{
  private Integer id;
  private int version;
  private String typeName;
  private Set<ImageDescriptor> imageDescriptorSet;

  private static final long serialVersionUID = 1;


  public ImageType()
  {
    super();
    this.imageDescriptorSet = new HashSet<ImageDescriptor>();
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
  public Set<ImageDescriptor> getImageDescriptorSet()
  {
    return imageDescriptorSet;
  }


  public void setImageDescriptorSet(Set<ImageDescriptor> imageDescriptorSet)
  {
    this.imageDescriptorSet = imageDescriptorSet;
  }


  @Deprecated
  public void addImageDescriptor(ImageDescriptor imageDescriptor)
  {
    this.imageDescriptorSet.add(imageDescriptor);
    imageDescriptor.setImageType(this);
  }


  public void linkImageDescriptor(ImageDescriptor imageDescriptor)
  {
    this.imageDescriptorSet.add(imageDescriptor);
    imageDescriptor.setImageType(this);
  }


  @Column(unique = true, nullable = false)
  public String getTypeName()
  {
    return typeName;
  }


  public void setTypeName(String typeName)
  {
    this.typeName = typeName;
  }


  public void unlink()
  {
    for (ImageDescriptor imageDescriptor : this.imageDescriptorSet)
    {
      imageDescriptor.setImageType(null);
    }
    this.imageDescriptorSet.clear();
  }
}
