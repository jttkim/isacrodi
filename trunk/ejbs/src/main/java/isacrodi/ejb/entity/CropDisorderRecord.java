package isacrodi.ejb.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;


@Entity
public class CropDisorderRecord
{

  private Integer id;
  private Integer recommendation;
  private Integer diagnosis;


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

}
