package org.isacrodi.ejb.entity;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.Version;
import javax.persistence.OneToOne;
import javax.persistence.OneToMany;

import java.util.Set;
import java.util.HashSet;

import org.javamisc.Util;
import org.javamisc.jee.entitycrud.CrudConfig;


@Entity
@CrudConfig(propertyOrder = {"id", "cropDisorderRecord", "*"})
public class Recommendation implements IsacrodiEntity
{
  private Integer id;
  private int version;
  private CropDisorderRecord cropDisorderRecord;
  private Set<ProcedureScore> procedureScoreSet;

  private static final long serialVersionUID = 1;


  public Recommendation()
  {
    super();
    this.procedureScoreSet = new HashSet<ProcedureScore>();
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


  @OneToOne(mappedBy="recommendation", optional = false)
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
    this.setCropDisorderRecord(cropDisorderRecord);
    cropDisorderRecord.setRecommendation(this);
  }


  public boolean unlinkCropDisorderRecord()
  {
    if (this.cropDisorderRecord == null)
    {
      return (false);
    }
    this.cropDisorderRecord.setRecommendation(null);
    this.cropDisorderRecord = null;
    return (true);
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


  @Deprecated
  public void addProcedureScore(ProcedureScore procedureScore)
  {
    this.procedureScoreSet.add(procedureScore);
    procedureScore.setRecommendation(this);
  }


  public void linkProcedureScore(ProcedureScore procedureScore)
  {
    this.procedureScoreSet.add(procedureScore);
    procedureScore.setRecommendation(this);
  }


  public void unlink()
  {
    this.unlinkCropDisorderRecord();
    for (ProcedureScore procedureScore : this.procedureScoreSet)
    {
      procedureScore.setRecommendation(null);
    }
    this.procedureScoreSet.clear();
  }


  public String toString()
  {
    Integer cdrId = null;
    if (this.cropDisorderRecord != null)
    {
      cdrId = this.cropDisorderRecord.getId();
    }
    return (String.format("Recommendation(id = %s, cdrId = %s)", Util.safeStr(this.id), Util.safeStr(cdrId)));
  }
}
