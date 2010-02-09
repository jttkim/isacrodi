package isacrodi.ejb.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.*;
import javax.persistence.GeneratedValue;
import java.util.*;

@Entity
public class CropDisorderRecord
{

  @Id
  @Column(name="Id")
  private Integer id;
  private Integer recommendation;
  private Integer diagnosis;
  private Set<Descriptor> descriptor;
  private IsacrodiUser isacrodiuser;
  private Crop crop;


  CropDisorderRecord()
  {
    super();
  }


  public CropDisorderRecord(Integer recommendation, Integer diagnosis)
  {
    this.recommendation = recommendation;
    this.diagnosis = diagnosis;
  }


  @Id @GeneratedValue
  public Integer getId()
  {
    return id;
  }


  public void setId(Integer id) 
  {
    this.id = id;
  }

  
  @OneToMany(mappedBy="cdr")
  public Set<Descriptor> getDescriptor()
  {
  	return descriptor;
  }

  public void setDescriptor(Set<Descriptor> descriptor)
  {
	this.descriptor = descriptor;
  }


  @ManyToOne
  public IsacrodiUser getisacrodiuser()
  {
	return isacrodiuser;
  }


  public void setisacrodiuser(IsacrodiUser isacrodiuser)
  {
	this.isacrodiuser = isacrodiuser;
  }

  @ManyToOne
  public Crop getCrop()
  {
  	return crop;
  }


  public void setCrop(Crop crop)
  {
  	this.crop = crop;
  }

}
