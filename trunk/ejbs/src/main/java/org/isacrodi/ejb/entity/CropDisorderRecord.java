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
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.javamisc.jee.entitycrud.CrudConfig;

import org.isacrodi.diagnosis.*;


/**
  * Crop disorder record entity.Contains all a crop's current state.
  */

@Entity
@CrudConfig(propertyOrder = {"id", "isacrodiUser", "crop", "expertDiagnosedCropDisorder", "descriptorSet", "description", "diagnosis", "recommendation", "*"})
public class CropDisorderRecord implements IsacrodiEntity
{
  private Integer id;
  private int version;
  private String name;
  private String description;
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
    this.description = "";
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


  public String getName()
  {
    return (this.name);
  }


  public void setName(String name)
  {
    this.name = name;
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
    // System.err.println("CropDisorderRecord.getExpertDiagnosedCropDisorder");
    /*
    if ((this.expertDiagnosedCropDisorder != null) && (this.expertDiagnosedCropDisorder.getVersion() > 22))
    {
      throw new RuntimeException("version has gone to 22");
    }
    */
    return (this.expertDiagnosedCropDisorder);
  }


  public void setExpertDiagnosedCropDisorder(CropDisorder expertDiagnosedCropDisorder)
  {
    // System.err.println("CropDisorderRecord.setExpertDiagnosedCropDisorder");
    this.expertDiagnosedCropDisorder = expertDiagnosedCropDisorder;
  }


  public void linkExpertDiagnosedCropDisorder(CropDisorder expertDiagnosedCropDisorder)
  {
    // System.err.println("CropDisorderRecord.linkExpertDiagnosedCropDisorder");
    this.expertDiagnosedCropDisorder = expertDiagnosedCropDisorder;
    expertDiagnosedCropDisorder.getExpertDiagnosedCropDisorderRecordSet().add(this);
  }


  public boolean unlinkExpertDiagnosedCropDisorder()
  {
    // System.err.println("CropDisorderRecord.unlinkExpertDiagnosedCropDisorder");
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


  public Map <String, NumericDescriptor> findNumericDescriptorMap()
  {
    Map<String, NumericDescriptor> numericDescriptorMap = new HashMap<String, NumericDescriptor>();
    for (NumericDescriptor numericDescriptor : this.findNumericDescriptorSet())
    {
      NumericType numericType = (NumericType) numericDescriptor.getDescriptorType();
      numericDescriptorMap.put(numericType.getTypeName(), numericDescriptor);
    }
    return (numericDescriptorMap);
  }


  public Set<CategoricalDescriptor> findCategoricalDescriptorSet()
  {
    HashSet<CategoricalDescriptor> categoricalDescriptorSet = new HashSet<CategoricalDescriptor>();
    for (Descriptor d : this.descriptorSet)
    {
      if (d instanceof CategoricalDescriptor)
      {
	categoricalDescriptorSet.add((CategoricalDescriptor) d);
      }
    }
    return (categoricalDescriptorSet);
  }


  public Map <String, CategoricalDescriptor> findCategoricalDescriptorMap()
  {
    Map<String, CategoricalDescriptor> categoricalDescriptorMap = new HashMap<String, CategoricalDescriptor>();
    for (CategoricalDescriptor categoricalDescriptor : this.findCategoricalDescriptorSet())
    {
      CategoricalType categoricalType = (CategoricalType) categoricalDescriptor.getDescriptorType();
      categoricalDescriptorMap.put(categoricalType.getTypeName(), categoricalDescriptor);
    }
    return (categoricalDescriptorMap);
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
    String cropName = "<null>";
    if (this.crop != null)
    {
      cropName = this.crop.getName();
    }
    String idString = "<null>";
    if (this.id != null)
    {
      idString = this.id.toString();
    }
    String userString = "<null>";
    if (this.isacrodiUser != null)
    {
      if (this.isacrodiUser.getUsername() == null)
      {
	userString = "<user with null name>";
      }
      else
      {
	userString = this.isacrodiUser.getUsername();
      }
    }
    return String.format("id = %s, version = %d, user = %s, crop = %s, description = %s, %d numeric descriptors, %d image descriptors", idString, this.version, userString, cropName, this.description, this.findNumericDescriptorSet().size(), this.findImageDescriptorSet().size());
  }


  private class DescriptorNameComparator implements Comparator<Descriptor>
  {
    public int compare(Descriptor d1, Descriptor d2)
    {
      String typeName1 = d1.getDescriptorType().getTypeName();
      String typeName2 = d2.getDescriptorType().getTypeName();
      return (typeName1.compareTo(typeName2));
    }
  }


  /**
   * Produce a serialised representation of this CDR, compatible with the import format.
   *
   * <p>CDR representations are canonicalised by sorting descriptors
   * alphabetically by descriptor type names. This facilitates
   * comparing descriptors for equality by using a simple diff
   * approach.</p>
   */
  public String fileRepresentation()
  {
    DescriptorNameComparator descriptorNameComparator = new DescriptorNameComparator();
    String x;
    String s = "cdr\n{\n";
    s += String.format("  user: %s\n", this.isacrodiUser.getUsername());
    x = "";
    if (this.crop != null)
    {
      x = this.crop.getScientificName();
    }
    s += String.format("  crop: %s\n", x);
    s += "  numericDescriptors\n  {\n";
    List<NumericDescriptor> numericDescriptorList = new ArrayList<NumericDescriptor>(this.findNumericDescriptorSet());
    Collections.sort(numericDescriptorList, descriptorNameComparator);
    for (NumericDescriptor numericDescriptor : numericDescriptorList)
    {
      s += String.format("    %s\n", numericDescriptor.fileRepresentation());
    }
    s += "  }\n";
    s += "  categoricalDescriptors\n  {\n";
    List<CategoricalDescriptor> categoricalDescriptorList = new ArrayList<CategoricalDescriptor>(this.findCategoricalDescriptorSet());
    Collections.sort(categoricalDescriptorList, descriptorNameComparator);
    for (CategoricalDescriptor categoricalDescriptor : categoricalDescriptorList)
    {
      s += String.format("    %s\n", categoricalDescriptor.fileRepresentation());
    }
    s += "  }\n";
    s += "  imageDescriptors\n  {\n";
    List<ImageDescriptor> imageDescriptorList = new ArrayList<ImageDescriptor>(this.findImageDescriptorSet());
    Collections.sort(imageDescriptorList, descriptorNameComparator);
    for (ImageDescriptor imageDescriptor : imageDescriptorList)
    {
      s += String.format("    %s\n", imageDescriptor.fileRepresentation());
    }
    s += "  }\n";
    x = "";
    if (this.expertDiagnosedCropDisorder != null)
    {
      x = this.expertDiagnosedCropDisorder.getScientificName();
    }
    s += String.format("  expertDiagnosis: %s", x);
    s += "\n}\n";
    return (s);
  }
}
