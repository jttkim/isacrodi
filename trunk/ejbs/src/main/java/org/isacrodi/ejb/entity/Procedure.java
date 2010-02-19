package org.isacrodi.ejb.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.Version;
import javax.persistence.ManyToMany;
import java.io.Serializable;
import java.util.Set;


// Entity implements Procedures
@Entity
public class Procedure implements Serializable
{
  private Integer id;
  private int version;
  private Set<Recommendation> recommendationSet;

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


  @ManyToMany
  public Set<Recommendation> getRecommendationSet()
  {
    return recommendationSet;
  }


  public void setRecommendationSet(Set<Recommendation> recommendationSet)
  {
    this.recommendationSet = recommendationSet;
  }

}
