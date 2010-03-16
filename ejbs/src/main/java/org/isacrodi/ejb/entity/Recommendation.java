package org.isacrodi.ejb.entity;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.Version;
import javax.persistence.OneToOne;
import javax.persistence.OneToMany;
import java.io.Serializable;
import java.util.Set;


@Entity
public class Recommendation implements Serializable
{
  private Integer id;
  private int version;
  private CropDisorderRecord cropDisorderRecord;
  private Set<ProcedureScore> procedureScoreSet;

  private static final long serialVersionUID = 1;


  public Recommendation()
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


  @OneToOne(mappedBy="recommendation")
  public CropDisorderRecord getCropDisorderRecord()
  {
    return (this.cropDisorderRecord);
  }


  public void setCropDisorderRecord(CropDisorderRecord cropDisorderRecord)
  {
    this.cropDisorderRecord = cropDisorderRecord;
  }

 
  @OneToMany(mappedBy="recommendation")
  public Set<ProcedureScore> getProcedureScoreSet()
  {
    return this.procedureScoreSet;
  }

 
  public void setProcedureScoreSet(Set<ProcedureScore> procedureScoreSet)
  {
    this.procedureScoreSet = procedureScoreSet;
  }


  public void addProcedureScore(ProcedureScore procedureScore)
  { 
    this.procedureScoreSet.add(procedureScore);
  }

}
