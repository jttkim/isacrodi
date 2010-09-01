package org.isacrodi.ejb.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Version;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import java.util.Set;
import java.util.HashSet;

import org.javamisc.Util;
import org.javamisc.jee.entitycrud.CrudConfig;


@Entity
@CrudConfig(propertyOrder = {"id", "name", "scientificName", "description", "cropDisorderSet", "cropDisorderRecordSet", "*"})
public class Crop implements IsacrodiEntity
{
  private Integer id;
  private int version;
  private String name;
  private String scientificName;
  private String description;
  private Set<CropDisorder> cropDisorderSet;
  private Set<CropDisorderRecord> cropDisorderRecordSet;

  private static final long serialVersionUID = 1;


  public Crop()
  {
    super();
    this.cropDisorderSet = new HashSet<CropDisorder>();
    this.cropDisorderRecordSet = new HashSet<CropDisorderRecord>();
  }


  public Crop(String name, String scientificName)
  {
    this();
    this.name = name;
    this.scientificName = scientificName;
  }


  @Id
  @GeneratedValue
  public Integer getId()
  {
    return this.id;
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


  @Column(length = 4096)
  public String getDescription()
  {
    return this.description;
  }


  public void setDescription(String description)
  {
    this.description = description;
  }


  @OneToMany(mappedBy="crop")
  public Set<CropDisorderRecord> getCropDisorderRecordSet()
  {
    return (this.cropDisorderRecordSet);
  }


  public void setCropDisorderRecordSet(Set<CropDisorderRecord> cropDisorderRecordSet)
  {
    this.cropDisorderRecordSet = cropDisorderRecordSet;
  }


  public void linkCropDisorderRecord(CropDisorderRecord cropDisorderRecord)
  {
    this.cropDisorderRecordSet.add(cropDisorderRecord);
    cropDisorderRecord.setCrop(this);
  }


  public boolean unlinkCropDisorderRecord(CropDisorderRecord cropDisorderRecord)
  {
    if (!this.cropDisorderRecordSet.remove(cropDisorderRecord))
    {
      return (false);
    }
    cropDisorderRecord.setCrop(null);
    return (true);
  }


  public String getName()
  {
    return this.name;
  }


  public void setName(String name)
  {
    this.name = name;
  }

  @Column(unique = true, nullable = false)
  public String getScientificName()
  {
    return scientificName;
  }


  public void setScientificName(String scientificName)
  {
    this.scientificName = scientificName;
  }

  @ManyToMany
  public Set<CropDisorder> getCropDisorderSet()
  {
    return this.cropDisorderSet;
  }


  public void setCropDisorderSet(Set<CropDisorder> cropDisorderSet)
  {
    this.cropDisorderSet = cropDisorderSet;
  }


  @Deprecated
  public void addCropDisorder(CropDisorder cropDisorder)
  {
    this.cropDisorderSet.add(cropDisorder);
    cropDisorder.getCropSet().add(this);
  }


  public void linkCropDisorder(CropDisorder cropDisorder)
  {
    this.cropDisorderSet.add(cropDisorder);
    cropDisorder.getCropSet().add(this);
  }


  public boolean unlinkCropDisorder(CropDisorder cropDisorder)
  {
    if (!this.cropDisorderSet.remove(cropDisorder))
    {
      return (false);
    }
    return (cropDisorder.getCropSet().remove(this));
  }


  public void unlink()
  {
    for (CropDisorder cropDisorder : this.cropDisorderSet)
    {
      cropDisorder.getCropSet().remove(this);
    }
    this.cropDisorderSet.clear();
    for (CropDisorderRecord cropDisorderRecord : this.cropDisorderRecordSet)
    {
      cropDisorderRecord.setCrop(null);
    }
    this.cropDisorderRecordSet.clear();
  }


  public String toString()
  {
    return String.format("Crop(id = %s name = %s scientificName = %s)", Util.safeStr(this.id), Util.safeStr(this.name), Util.safeStr(this.scientificName));
  }


  public String fileRepresentation()
  {
    String s = "crop\n{\n";
    s += String.format("  name: %s\n", this.name);
    s += String.format("  scientificName: %s\n", this.scientificName);
    s += "}\n";
    return (s);
  }
}
