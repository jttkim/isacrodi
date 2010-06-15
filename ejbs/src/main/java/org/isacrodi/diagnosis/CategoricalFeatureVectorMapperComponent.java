package org.isacrodi.diagnosis;

import org.isacrodi.ejb.entity.*;
import org.isacrodi.ejb.session.*;


/*
** Categorical Feature Vector Mapper Component
*/

public class CategoricalFeatureVectorMapperComponent extends FeatureVectorMapperComponent
{

  private String catname;
  private int value;


  public CategoricalFeatureVectorMapperComponent()
  {
    super();
  }


  public CategoricalFeatureVectorMapperComponent(String name, String catname, int value)
  {
    super(name);
    this.catname = catname;
    this.value = value;
  }


  public String getCatName()
  {
    return this.catname;
  }


  public int getValue()
  {
    return this.value;
  }


  public String toString()
  {
    return String.format("%s %d", this.catname, this.value);
  }



}


