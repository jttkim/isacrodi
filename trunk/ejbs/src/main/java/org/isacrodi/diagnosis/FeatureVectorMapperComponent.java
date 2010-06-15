package org.isacrodi.diagnosis;

import org.isacrodi.ejb.entity.*;
import org.isacrodi.ejb.session.*;

/**
 ** Abstract Feature Vector Mapper Component 
**/

class FeatureVectorMapperComponent
{

  private String name;


  public FeatureVectorMapperComponent()
  {
    super();
  }


  public FeatureVectorMapperComponent(String name)
  {
    this();
    this.name = name;
  }


  public String getName()
  {
    return this.name = name;
  }


  public void setName(String name)
  {
    this.name = name;
  }


  public String toString()
  {
    return String.format("%s", this.name);
  }

}
