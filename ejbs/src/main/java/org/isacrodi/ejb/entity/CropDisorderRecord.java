package org.isacrodi.ejb.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.Version;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.ManyToOne;
import java.util.Set;
import java.util.HashSet;
import java.io.Serializable;
import org.isacrodi.diagnosis.*;


/**
  * Crop disorder record entity.Contains all a crop's current state.
  */

@Entity
public class CropDisorderRecord implements Serializable
{
  private Integer id;
  private int version;
  private Recommendation recommendation;
  private Diagnosis diagnosis;
  private Set<Descriptor> descriptorSet;
  private IsacrodiUser isacrodiUser;
  private Crop crop;

  private static final long serialVersionUID = 1;


  public CropDisorderRecord()
  {
    super();
    this.descriptorSet = new HashSet<Descriptor>();
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


  @OneToMany(mappedBy="cropDisorderRecord")
  public Set<Descriptor> getDescriptorSet()
  {
    return (this.descriptorSet);
  }


  public void setDescriptorSet(Set<Descriptor> descriptorSet)
  {
    this.descriptorSet = descriptorSet;
  }


  public void addDescriptor(Descriptor descriptor)
  {
    this.descriptorSet.add(descriptor);
    descriptor.setCropDisorderRecord(this);
  }


  @ManyToOne
  public IsacrodiUser getIsacrodiUser()
  {
    return (this.isacrodiUser);
  }


  public void setIsacrodiUser(IsacrodiUser isacrodiUser)
  {
    this.isacrodiUser = isacrodiUser;
  }


  @ManyToOne
  public Crop getCrop()
  {
    return (this.crop);
  }


  public void setCrop(Crop crop)
  {
    this.crop = crop;
  }


  @OneToOne
  public Diagnosis getDiagnosis()
  {
    return diagnosis;
  }


  public void setDiagnosis(Diagnosis diagnosis)
  {
    this.diagnosis = diagnosis;
  }


  public void linkDiagnosis(Diagnosis diagnosis)
  {
    this.setDiagnosis(diagnosis);
    diagnosis.setCropDisorderRecord(this);
  }


  @OneToOne
  public Recommendation getRecommendation()
  {
    return recommendation;
  }


  public void setRecommendation(Recommendation recommendation)
  {
    this.recommendation = recommendation;
  }


  public Set<NumericDescriptor> findNumericDescriptorSet()
  {
    HashSet<NumericDescriptor> numericDescriptorSet = new HashSet<NumericDescriptor>();
    for (Descriptor d : this.descriptorSet)
    {
      if (d instanceof NumericDescriptor)
      {
	numericDescriptorSet.add((NumericDescriptor) d);
      }
    }
    return (numericDescriptorSet);
  }


  public Set<ImageDescriptor> findImageDescriptorSet()
  {
    HashSet<ImageDescriptor> imageDescriptorSet = new HashSet<ImageDescriptor>();
    for (Descriptor d : this.descriptorSet)
    {
      if (d instanceof ImageDescriptor)
      {
	imageDescriptorSet.add((ImageDescriptor) d);
      }
    }
    return (imageDescriptorSet);
  }


  public String toString()
  {
    return String.format("%s %s %s", getId(), getVersion(), getCrop().getName());
  }
}
