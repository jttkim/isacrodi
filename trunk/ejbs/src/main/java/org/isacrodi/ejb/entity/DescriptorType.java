package org.isacrodi.ejb.entity;

import java.util.Set;
import java.util.HashSet;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.Version;
import javax.persistence.Column;
import javax.persistence.OneToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class DescriptorType implements IsacrodiEntity
{
  protected Integer id;
  protected int version;
  protected String typeName;
  protected String description;
  protected Set<Descriptor> descriptorSet;

  private static final long serialVersionUID = 1;


  public DescriptorType()
  {
    super();
    this.descriptorSet = new HashSet<Descriptor>();
  }


  public DescriptorType(String typeName)
  {
    this();
    this.typeName = typeName;
  }


  @Id
  @GeneratedValue
  public Integer getId()
  {
    return (this.id);
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


  @Column(unique = true, nullable = false)
  public String getTypeName()
  {
    return typeName;
  }


  public void setTypeName(String typeName)
  {
    this.typeName = typeName;
  }


  @Column(length = 4096)
  public String getDescription()
  {
    return (this.description);
  }


  public void setDescription(String description)
  {
    this.description = description;
  }


  @OneToMany(mappedBy = "descriptorType")
  public Set<Descriptor> getDescriptorSet()
  {
    return (this.descriptorSet);
  }


  public void setDescriptorSet(Set<Descriptor> descriptorSet)
  {
    this.descriptorSet = descriptorSet;
  }


  public void linkDescriptor(Descriptor descriptor)
  {
    this.descriptorSet.add(descriptor);
    // FIXME: this typecast should not be necessary
    descriptor.setDescriptorType(this);
  }


  public boolean unlinkDescriptor(Descriptor descriptor)
  {
    if (!this.descriptorSet.remove(descriptor))
    {
      return (false);
    }
    descriptor.setDescriptorType(null);
    return (true);
  }


  public void unlink()
  {
    for (Descriptor descriptor : this.descriptorSet)
    {
      descriptor.setDescriptorType(null);
    }
    this.descriptorSet.clear();
  }
}
