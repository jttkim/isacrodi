package org.isacrodi.diagnosis;

import org.isacrodi.ejb.entity.*;
import org.isacrodi.ejb.session.*;


/**
 * Categorical Feature Vector Mapper Component
 */
public class CategoricalFeatureVectorMapperComponent
{

  private String catname;
  private int index;


  public CategoricalFeatureVectorMapperComponent()
  {
    super();
  }


  public CategoricalFeatureVectorMapperComponent(String catname, int index)
  {
    this();
    this.catname = catname;
    this.index = index;
  }


  public String getCatName()
  {
    return this.catname;
  }


  public int getIndex()
  {
    return this.index;
  }


  public String toString()
  {
    return String.format("%s %d", this.catname, this.index);
  }
}


