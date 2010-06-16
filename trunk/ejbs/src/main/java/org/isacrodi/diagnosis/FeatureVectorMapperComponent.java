package org.isacrodi.diagnosis;

import org.isacrodi.ejb.entity.*;
import org.isacrodi.ejb.session.*;

/**
 ** Abstract Feature Vector Mapper Component 
**/

class FeatureVectorMapperComponent
{

  private String name;
  private int indexpresence;

  public FeatureVectorMapperComponent()
  {
    super();
  }


  public FeatureVectorMapperComponent(String name, int indexpresence)
  {
    this();
    this.name = name;
    this.indexpresence = indexpresence;
  }


  public String getName()
  {
    return this.name = name;
  }


  public void setName(String name)
  {
    this.name = name;
  }


  public int getIndexPresence()
  {
    return this.indexpresence;
  }


  public String toString()
  {
    return String.format("%s %d", this.name, this.indexpresence);
  }

}
