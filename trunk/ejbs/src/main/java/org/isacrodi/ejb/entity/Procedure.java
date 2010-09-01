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

import org.javamisc.Util;
import org.javamisc.jee.entitycrud.CrudConfig;


/*
 * Class Procedure
 */
@Entity
@CrudConfig(propertyOrder = {"id", "name", "toxicologicalClass", "description", "cropDisorderSet", "incompatibleProcedureSet", "*"})
public class Procedure implements IsacrodiEntity
{
  private Integer id;
  private int version;
  private String name;
  private String description;
  private String toxicologicalClass;
  private Set<CropDisorder> cropDisorderSet;
  private Set<ProcedureScore> procedureScoreSet;
  private Set<Procedure> incompatibleProcedureSet;


  private static final long serialVersionUID = 1;


  public Procedure()
  {
    super();
    this.cropDisorderSet = new HashSet<CropDisorder>();
    this.procedureScoreSet = new HashSet<ProcedureScore>();
    this.incompatibleProcedureSet = new HashSet<Procedure>();
  }


  public Procedure(String name)
  {
    this();
    this.name = name;
  }


  public Procedure(String name, String description)
  {
    this(name);
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


  @Column(unique = true, nullable = false)
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
    return this.description;
  }


  public void setDescription(String description)
  {
    this.description = description;
  }


  public String getToxicologicalClass()
  {
    return (this.toxicologicalClass);
  }


  public void setToxicologicalClass(String toxicologicalClass)
  {
    this.toxicologicalClass = toxicologicalClass;
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


  /*
  public boolean equals(Object other)
  {
    if (other instanceof Procedure)
    {
      Procedure otherProcedure = (Procedure) other;
      if ((this.id == null) || (otherProcedure.id == null))
      {
	return (false);
      }
      return (this.id.equals(otherProcedure.id));
    }
    return (false);
  }


  public int hashCode()
  {
    if (this.id == null)
    {
      return (0);
    }
    return (this.id.intValue());
  }
  */


  public boolean compatibleWith(Procedure otherProcedure)
  {
    /*
    System.err.println(String.format("Procedure.compatibleWith: checking compatibility of %s with %s", this.name, otherProcedure.name));
    for (Procedure incompatibleProcedure : this.incompatibleProcedureSet)
    {
      System.err.println(String.format("Procedure.compatibleWith:   incompatible with %s: %b", incompatibleProcedure.getName(), incompatibleProcedure.equals(otherProcedure)));
    }
    */
    return (!this.incompatibleProcedureSet.contains(otherProcedure));
  }


  public String toString()
  {
    return (String.format("Procedure(id = %s, name = %s, description = %s, toxicologicalClass = %s)", Util.safeStr(this.id), Util.safeStr(this.name), Util.safeStr(this.description), Util.safeStr(this.toxicologicalClass)));
  }
}
