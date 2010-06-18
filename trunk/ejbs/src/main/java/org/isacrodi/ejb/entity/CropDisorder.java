package org.isacrodi.ejb.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.Version;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Column;

import java.util.Set;
import java.util.HashSet;


@Entity
public class CropDisorder implements IsacrodiEntity
{
  private Integer Id;
  private int version;
  private String name;
  private String scientificName;
  private Set<Crop> cropSet;
  private Set<DisorderScore> disorderScoreSet;
  private Set<Procedure> procedureSet;
  private Set<CropDisorderRecord> expertDiagnosedCropDisorderRecordSet;

  private static final long serialVersionUID = 1;


  public CropDisorder()
  {
    super();
    this.cropSet = new HashSet<Crop>();
    this.disorderScoreSet = new HashSet<DisorderScore>();
    this.procedureSet = new HashSet<Procedure>();
    this.expertDiagnosedCropDisorderRecordSet = new HashSet<CropDisorderRecord>();
  }


  public CropDisorder(String name, String scientificName)
  {
    this();
    this.name = name;
    this.scientificName = scientificName;
  }


  @Id
  @GeneratedValue
  public Integer getId()
  {
    return (this.Id);
  }


  public void setId(Integer Id)
  {
    this.Id = Id;
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


  @Column(unique = true, nullable = false)
  public String getScientificName()
  {
    return (this.scientificName);
  }


  public void setScientificName(String scientificName)
  {
    this.scientificName = scientificName;
  }


  @ManyToMany
  public Set<Crop> getCropSet()
  {
    return this.cropSet;
  }


  public void setCropSet(Set<Crop> cropSet)
  {
    this.cropSet = cropSet;
  }


  @Deprecated
  public void addCrop(Crop crop)
  {
    this.cropSet.add(crop);
    crop.getCropDisorderSet().add(this);
  }


  public void linkCrop(Crop crop)
  {
    this.cropSet.add(crop);
    crop.getCropDisorderSet().add(this);
  }


  public boolean unlinkCrop(Crop crop)
  {
    if (!this.cropSet.remove(crop))
    {
      return (false);
    }
    // FIXME: notice that false may indicate that the crop was removed but this disorder was not
    return (crop.getCropDisorderSet().remove(this));
  }


  @OneToMany(mappedBy="cropDisorder")
  public Set<DisorderScore> getDisorderScoreSet()
  {
    return this.disorderScoreSet;
  }


  public void setDisorderScoreSet(Set<DisorderScore> disorderScoreSet)
  {
    this.disorderScoreSet = this.disorderScoreSet;
  }


  @Deprecated
  public void addDisorderScore(DisorderScore disorderScore)
  {
    this.disorderScoreSet.add(disorderScore);
    disorderScore.setCropDisorder(this);
  }


  public void linkDisorderScore(DisorderScore disorderScore)
  {
    this.disorderScoreSet.add(disorderScore);
    disorderScore.setCropDisorder(this);
  }


  public boolean unlinkDisorderScore(DisorderScore disorderScore)
  {
    if (!this.disorderScoreSet.remove(disorderScore))
    {
      return (false);
    }
    disorderScore.setCropDisorder(null);
    return (true);
  }


  @ManyToMany
  public Set<Procedure> getProcedureSet()
  {
    return this.procedureSet;
  }


  public void setProcedureSet(Set<Procedure> procedureSet)
  {
    this.procedureSet = procedureSet;
  }


  @Deprecated
  public void addProcedure(Procedure procedure)
  {
    this.procedureSet.add(procedure);
    procedure.getCropDisorderSet().add(this);
  }


  public void linkProcedure(Procedure procedure)
  {
    this.procedureSet.add(procedure);
    procedure.getCropDisorderSet().add(this);
  }


  public boolean unlinkProcedure(Procedure procedure)
  {
    if (!this.procedureSet.remove(procedure))
    {
      return (false);
    }
    // FIXME: notice that a return value of false may indicate that a procedure was removed but this disorder was not
    return (procedure.getCropDisorderSet().remove(this));
  }


  @OneToMany(mappedBy = "expertDiagnosedCropDisorder")
  public Set<CropDisorderRecord> getExpertDiagnosedCropDisorderRecordSet()
  {
    return (this.expertDiagnosedCropDisorderRecordSet);
  }


  public void setExpertDiagnosedCropDisorderRecordSet(Set<CropDisorderRecord> expertDiagnosedCropDisorderRecordSet)
  {
    this.expertDiagnosedCropDisorderRecordSet = expertDiagnosedCropDisorderRecordSet;
  }


  public void linkExpertDiagnosedCropDisorderRecordSet(CropDisorderRecord expertDiagnosedCropDisorderRecord)
  {
    this.expertDiagnosedCropDisorderRecordSet.add(expertDiagnosedCropDisorderRecord);
    expertDiagnosedCropDisorderRecord.setExpertDiagnosedCropDisorder(this);
  }


  public boolean unlinkExpertDiagnosedCropDisorderRecordSet(CropDisorderRecord expertDiagnosedCropDisorderRecord)
  {
    if (!this.expertDiagnosedCropDisorderRecordSet.remove(expertDiagnosedCropDisorderRecord))
    {
      return (false);
    }
    expertDiagnosedCropDisorderRecord.setExpertDiagnosedCropDisorder(null);
    return (true);
  }


  public void unlink()
  {
    for (Crop crop : this.cropSet)
    {
      crop.getCropDisorderSet().remove(this);
    }
    this.cropSet.clear();
    for (DisorderScore disorderScore : this.disorderScoreSet)
    {
      disorderScore.setCropDisorder(null);
    }
    this.disorderScoreSet.clear();
    for (Procedure procedure : this.procedureSet)
    {
      procedure.getCropDisorderSet().remove(this);
    }
    this.procedureSet.clear();
    for (CropDisorderRecord expertDiagnosedCropDisorderRecord : this.expertDiagnosedCropDisorderRecordSet)
    {
      expertDiagnosedCropDisorderRecord.setExpertDiagnosedCropDisorder(null);
    }
    this.expertDiagnosedCropDisorderRecordSet.clear();
  }


  public String toString()
  {
    return String.format("%s %s %s %s", getId(), getVersion(), getName(), getScientificName());
  }
}
