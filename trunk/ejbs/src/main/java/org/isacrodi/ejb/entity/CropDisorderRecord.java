package org.isacrodi.ejb.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.Version;
import javax.persistence.OneToMany;
import javax.persistence.ManyToOne;
import java.util.Set;
import java.io.Serializable;
import org.isacrodi.diagnosis.*;


/**
  * Crop disorder record entity.Contains all a crop's current state.
  */

@Entity
public class CropDisorderRecord
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


  public Diagnosis getDiagnosis()
  {
    return diagnosis;
  }


  public void setDiagnosis(Diagnosis diagnosis)
  {
    this.diagnosis = diagnosis;
  }


  public Recommendation getRecommendation()
  {
    return recommendation;
  }


  public void setRecommendation(Recommendation recommendation)
  {
    this.recommendation = recommendation;
  }


  public ImageDescriptor getImageDescriptorSet()
  {
     ImageDescriptor ides = new ImageDescriptor();

     for (Object o : getDescriptorSet())
     {
       if (o.getClass().isInstance(new ImageDescriptor()))
       {
         ides = (ImageDescriptor)o;
       }
     }
     return ides;
    //Set<ImageDescriptor> imageDescriptorSet = cropDisorderRecord.getImageDescriptorSet();
  }


  public NumericDescriptor getNumericDescriptorSet()
  {
     NumericDescriptor ndes = new NumericDescriptor();

     for (Object o : getDescriptorSet())
     {
       if (o.getClass().isInstance(new NumericDescriptor()))
       {
         ndes = (NumericDescriptor)o;
       }
     }
     return ndes;
  }

}
