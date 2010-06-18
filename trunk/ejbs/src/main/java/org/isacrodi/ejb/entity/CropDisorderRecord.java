package org.isacrodi.ejb.entity;

import javax.persistence.Entity;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.Version;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.ManyToOne;

import java.util.Set;
import java.util.HashSet;

import org.isacrodi.diagnosis.*;


/**
  * Crop disorder record entity.Contains all a crop's current state.
  */

@Entity
public class CropDisorderRecord implements IsacrodiEntity
{
  private Integer id;
  private int version;
  private Recommendation recommendation;
  private Diagnosis diagnosis;
  private Set<Descriptor> descriptorSet;
  private IsacrodiUser isacrodiUser;
  private Crop crop;
  private CropDisorder expertDiagnosedCropDisorder;

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


  @Deprecated
  public void addDescriptor(Descriptor descriptor)
  {
    this.descriptorSet.add(descriptor);
    descriptor.setCropDisorderRecord(this);
  }


  public void linkDescriptor(Descriptor descriptor)
  {
    this.descriptorSet.add(descriptor);
    descriptor.setCropDisorderRecord(this);
  }


  public boolean unlinkDescriptor(Descriptor descriptor)
  {
    if (this.descriptorSet.remove(descriptor))
    {
      descriptor.setCropDisorderRecord(null);
      return (true);
    }
    else
    {
      return (false);
    }
  }


  @ManyToOne(optional = false)
  public IsacrodiUser getIsacrodiUser()
  {
    return (this.isacrodiUser);
  }


  public void setIsacrodiUser(IsacrodiUser isacrodiUser)
  {
    this.isacrodiUser = isacrodiUser;
  }


  public void linkIsacrodiUser(IsacrodiUser isacrodiUser)
  {
    this.isacrodiUser = isacrodiUser;
    isacrodiUser.getCropDisorderRecordSet().add(this);
  }


  public boolean unlinkIsacrodiUser()
  {
    if (this.isacrodiUser == null)
    {
      return (false);
    }
    if (!this.isacrodiUser.getCropDisorderRecordSet().remove(this))
    {
      return (false);
    }
    this.isacrodiUser = null;
    return (true);
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


  public void linkCrop(Crop crop)
  {
    this.crop = crop;
    crop.getCropDisorderRecordSet().add(this);
  }


  public boolean unlinkCrop()
  {
    if (this.crop == null)
    {
      return (false);
    }
    if (!this.crop.getCropDisorderRecordSet().remove(this))
    {
      return (false);
    }
    this.crop = null;
    return (true);
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
    this.diagnosis = diagnosis;
    diagnosis.setCropDisorderRecord(this);
  }


  public boolean unlinkDiagnosis()
  {
    if (this.diagnosis == null)
    {
      return (false);
    }
    this.diagnosis.setCropDisorderRecord(null);
    this.diagnosis = null;
    return (true);
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


  public void linkRecommendation(Recommendation recommendation)
  {
    this.recommendation = recommendation;
    recommendation.setCropDisorderRecord(this);
  }


  public boolean unlinkRecommendation()
  {
    if (this.recommendation == null)
    {
      return (false);
    }
    this.recommendation.setCropDisorderRecord(null);
    this.recommendation = null;
    return (true);
  }


  @ManyToOne
  public CropDisorder getExpertDiagnosedCropDisorder()
  {
    return (this.expertDiagnosedCropDisorder);
  }


  public void setExpertDiagnosedCropDisorder(CropDisorder expertDiagnosedCropDisorder)
  {
    this.expertDiagnosedCropDisorder = expertDiagnosedCropDisorder;
  }


  public void linkExpertDiagnosedCropDisorder(CropDisorder expertDiagnosedCropDisorder)
  {
    this.expertDiagnosedCropDisorder = expertDiagnosedCropDisorder;
    expertDiagnosedCropDisorder.getExpertDiagnosedCropDisorderRecordSet().add(this);
  }


  public boolean unlinkExpertDiagnosedCropDisorder()
  {
    if (this.expertDiagnosedCropDisorder == null)
    {
      return (false);
    }

    if (!this.expertDiagnosedCropDisorder.getExpertDiagnosedCropDisorderRecordSet().remove(this))
    {
      return (false);
    }
    this.expertDiagnosedCropDisorder = null;
    return (true);
  }


  public void unlink()
  {
    this.unlinkRecommendation();
    this.unlinkDiagnosis();
    for (Descriptor descriptor : this.descriptorSet)
    {
      descriptor.setCropDisorderRecord(null);
    }
    this.descriptorSet.clear();
    this.unlinkIsacrodiUser();
    this.unlinkCrop();
    this.unlinkExpertDiagnosedCropDisorder();
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
