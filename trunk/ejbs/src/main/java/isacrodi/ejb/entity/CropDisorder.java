package isacrodi.ejb.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;

@Entity
public class CropDisorder
{
  private Integer id;
  private String name;
  private String scientificName;


  CropDisorder() 
  {
    super();
  }


  public CropDisorder(Integer id, String name, String scientificName) 
  {
    //this.id = id;
    this.name = name;
    this.scientificName = scientificName;
  }

  @Id @GeneratedValue
  public Integer getId()
  {
    return (this.id);
  }


  public void setId(Integer id)
  {
    this.id = id;
  }


  public String getName() 
  {
    return name;
  }


  public void setName(String name)
  {
    this.name = name;
  }


  public String getScientificName() 
  {
    return scientificName;
  }


  public void setScientificName(String scientificName)
  {
    this.name = scientificName;
  }
}
