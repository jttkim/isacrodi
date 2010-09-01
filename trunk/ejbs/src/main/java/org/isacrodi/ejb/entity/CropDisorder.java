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

import org.javamisc.Util;
import org.javamisc.jee.entitycrud.CrudConfig;


@Entity
@CrudConfig(propertyOrder = {"id", "name", "scientificName", "description", "cropSet", "procedureSet", "expertDiagnosedCropDisorderRecordSet", "disorderScoreSet", "*"})
public class CropDisorder implements IsacrodiEntity
{
  private Integer id;
  private int version;
  private String name;
  private String scientificName;
  private String description;
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
    return (this.id);
  }


  public void setId(Integer id)
  {
    // System.err.println(String.format("CropDisorder.setId(%s) on %s", Util.safeStr(id), this.toString()));
    this.id = id;
  }


  @Version
  public int getVersion()
  {
    // System.err.printf(String.format("CropDisorder.getVersion: returning %d, scientificName = %s", this.version, Util.safeStr(this.scientificName)));
    return (this.version);
  }


  public void setVersion(int version)
  {
    this.version = version;
    // System.err.printf(String.format("CropDisorder.setVersion: version set to %d, scientificName = %s", this.version, Util.safeStr(this.scientificName)));
  }


  public String getName()
  {
    return (this.name);
  }


  public void setName(String name)
  {
    // System.err.println(String.format("CropDisorder.setName(%s) on %s", Util.safeStr(name), this.toString()));
    this.name = name;
  }


  @Column(unique = true, nullable = false)
  public String getScientificName()
  {
    return (this.scientificName);
  }


  public void setScientificName(String scientificName)
  {
    // System.err.println(String.format("CropDisorder.setScientificName(%s) on %s", Util.safeStr(scientificName), this.toString()));
    this.scientificName = scientificName;
  }


  @Column(length = 4096)
  public String getDescription()
  {
    return (this.description);
  }


  public void setDescription(String description)
  {
    // System.err.println(String.format("CropDisorder.setDescription(%s) on %s", Util.safeStr(description), this.toString()));
    this.description = description;
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
    this.disorderScoreSet = disorderScoreSet;
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
    // System.err.println("CropDisorder.getExpertDiagnosedCropDisorderRecordSet");
    return (this.expertDiagnosedCropDisorderRecordSet);
  }


  public void setExpertDiagnosedCropDisorderRecordSet(Set<CropDisorderRecord> expertDiagnosedCropDisorderRecordSet)
  {
    // System.err.println("CropDisorder.setExpertDiagnosedCropDisorderRecordSet");
    this.expertDiagnosedCropDisorderRecordSet = expertDiagnosedCropDisorderRecordSet;
  }


  public void linkExpertDiagnosedCropDisorderRecordSet(CropDisorderRecord expertDiagnosedCropDisorderRecord)
  {
    // System.err.println("CropDisorder.linkExpertDiagnosedCropDisorderRecordSet");
    this.expertDiagnosedCropDisorderRecordSet.add(expertDiagnosedCropDisorderRecord);
    expertDiagnosedCropDisorderRecord.setExpertDiagnosedCropDisorder(this);
  }


  public boolean unlinkExpertDiagnosedCropDisorderRecordSet(CropDisorderRecord expertDiagnosedCropDisorderRecord)
  {
    // System.err.println("CropDisorder.unlinkExpertDiagnosedCropDisorderRecordSet");
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

  /*
  public int hashCode()
  {
    System.err.println(String.format("CropDisorder.hashCode: %s", this.toString()));
    if (this.id == null)
    {
      return (0);
    }
    return (this.id.intValue());
  }


  public boolean equals(Object other)
  {
    System.err.println(String.format("CropDisorder.equals(%s)", other.toString()));
    if (other instanceof CropDisorder)
    {
      CropDisorder otherCropDisorder = (CropDisorder) other;
      System.err.println(String.format("CropDisorder.equals(%s, %s)", this.toString(), other.toString()));
      if ((this.id == null) || (otherCropDisorder.id == null))
      {
	if ((this.scientificName != null) && (otherCropDisorder.scientificName != null))
	{
	  return (this.scientificName.equals(otherCropDisorder.scientificName));
	}
	else
	{
	  return (false);
	}
      }
      else
      {
	return (this.id.equals(otherCropDisorder.id));
      }
    }
    return (false);
  }
  */


  public String toString()
  {
    // FIXME: not safe outside transaction context (size() may then fail)
    return (String.format("CropDisorder(id = %s, name = %s, scientificName = %s, description = %s, %d crops, %d procedures, %d expert diagnoses, %d scores", Util.safeStr(this.id), Util.safeStr(this.name), Util.safeStr(this.scientificName), Util.safeStr(this.description), this.cropSet.size(), this.procedureSet.size(), this.expertDiagnosedCropDisorderRecordSet.size(), this.disorderScoreSet.size()));
  }


  public String fileRepresentation()
  {
    String s = "disorder\n{\n";
    s += String.format("  name: %s", this.name);
    s += String.format("  scientificName: %s", this.scientificName);
    s += "  cropSet: ";
    String glue = "";
    for (Crop crop : this.cropSet)
    {
      s += glue + crop.getScientificName();
    }
    s += "\n}\n";
    return (s);
  }
}
