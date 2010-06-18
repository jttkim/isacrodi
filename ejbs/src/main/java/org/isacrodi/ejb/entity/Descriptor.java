package org.isacrodi.ejb.entity;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.Version;
import javax.persistence.ManyToOne;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Descriptor implements IsacrodiEntity
{
  private Integer id;
  private int version;
  private CropDisorderRecord cropDisorderRecord;

  private static final long serialVersionUID = 1;


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


  @ManyToOne(optional = false)
  public CropDisorderRecord getCropDisorderRecord()
  {
    return (this.cropDisorderRecord);
  }


  public void setCropDisorderRecord(CropDisorderRecord cropDisorderRecord)
  {
    this.cropDisorderRecord = cropDisorderRecord;
  }


  public void linkCropDisorderRecord(CropDisorderRecord cropDisorderRecord)
  {
    this.cropDisorderRecord = cropDisorderRecord;
    cropDisorderRecord.getDescriptorSet().add(this);
  }


  public boolean unlinkCropDisorderRecord()
  {
    if (this.cropDisorderRecord == null)
    {
      return (false);
    }
    if (!this.cropDisorderRecord.getDescriptorSet().remove(this))
    {
      return (false);
    }
    this.cropDisorderRecord = null;
    return (true);
  }


  public void unlink()
  {
    this.unlinkCropDisorderRecord();
  }
}
