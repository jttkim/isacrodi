package org.isacrodi.ejb.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.Version;
import java.io.Serializable;


@Entity
public class Procedure implements Serializable
{
  private Integer id;
  private int version;

  private static final long serialVersionUID = 1;


  Procedure()
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


}
