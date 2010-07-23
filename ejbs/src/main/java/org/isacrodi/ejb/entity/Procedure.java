package org.isacrodi.ejb.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Version;
import javax.persistence.OneToMany;
import javax.persistence.ManyToMany;


/*
 * Class Procedure
 */
@Entity
public class Procedure implements IsacrodiEntity
{
  private Integer id;
  private int version;
  private String description;
  private Set<CropDisorder> cropDisorderSet;
  private Set<ProcedureScore> procedureScoreSet;
  private Set<Procedure> incompatibleProcedureSet;


  private static final long serialVersionUID = 1;


  public Procedure()
  {
    super();
    this.cropDisorderSet = new HashSet<CropDisorder>();
    this.procedureScoreSet = new HashSet<ProcedureScore>();
  }


  public Procedure(String description)
  {
    this();
    this.description = description;
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


  @Column(length = 4096)
  public String getDescription()
  {
    return this.description;
  }


  public void setDescription(String description)
  {
    this.description = description;
  }


  @OneToMany(mappedBy="procedure")
  public Set<ProcedureScore> getProcedureScoreSet()
  {
    return procedureScoreSet;
  }


  public void setProcedureScoreSet(Set<ProcedureScore> procedureScoreSet)
  {
    this.procedureScoreSet = procedureScoreSet;
  }


  public void linkProcedureScore(ProcedureScore procedureScore)
  {
    this.procedureScoreSet.add(procedureScore);
    procedureScore.setProcedure(this);
  }


  public boolean unlinkProcedureScore(ProcedureScore procedureScore)
  {
    if (!this.procedureScoreSet.remove(procedureScore))
    {
      return (false);
    }
    procedureScore.setProcedure(null);
    return (true);
  }


  @ManyToMany
  public Set<CropDisorder> getCropDisorderSet()
  {
    return this.cropDisorderSet;
  }


  public void setCropDisorderSet(Set<CropDisorder> cropDisoderSet)
  {
    this.cropDisorderSet = cropDisorderSet;
  }


  public void linkCropDisorder(CropDisorder cropDisorder)
  {
    this.cropDisorderSet.add(cropDisorder);
    cropDisorder.getProcedureSet().add(this);
  }


  public boolean unlinkCropDisorder(CropDisorder cropDisorder)
  {
    if (!this.cropDisorderSet.remove(cropDisorder))
    {
      return (false);
    }
    return (cropDisorder.getProcedureSet().remove(this));
  }


  @ManyToMany
  public Set<Procedure> getIncompatibleProcedureSet()
  {
    return (this.incompatibleProcedureSet);
  }


  public void setIncompatibleProcedureSet(Set<Procedure> incompatibleProcedureSet)
  {
    this.incompatibleProcedureSet = incompatibleProcedureSet;
  }


  public void linkIncompatibleProcedure(Procedure incompatibleProcedure)
  {
    this.incompatibleProcedureSet.add(incompatibleProcedure);
    incompatibleProcedure.getIncompatibleProcedureSet().add(this);
  }


  public boolean unlinkIncompatibleProcedure(Procedure incompatibleProcedure)
  {
    if (!this.incompatibleProcedureSet.remove(incompatibleProcedure))
    {
      return (false);
    }
    if (!incompatibleProcedure.getIncompatibleProcedureSet().remove(this))
    {
      return (false);
    }
    return (true);
  }


  public String toString()
  {
    return String.format("%s %s %s", getId(), getVersion(), getDescription());
  }


  public void unlink()
  {
    for (CropDisorder cropDisorder : this.cropDisorderSet)
    {
      cropDisorder.getProcedureSet().remove(this);
    }
    this.cropDisorderSet.clear();
    for (ProcedureScore procedureScore: this.procedureScoreSet)
    {
      procedureScore.setProcedure(null);
    }
    this.procedureScoreSet.clear();
    for (Procedure incompatibleProcedure : this.getIncompatibleProcedureSet())
    {
      incompatibleProcedure.getIncompatibleProcedureSet().remove(this);
    }
    this.incompatibleProcedureSet.clear();
  }
}
