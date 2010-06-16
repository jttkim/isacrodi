package org.isacrodi.diagnosis;

import org.isacrodi.ejb.entity.*;
import org.isacrodi.ejb.session.*;


/**
 * Categorical Feature Vector Mapper Component
 */
public class CategoricalFeatureStateMapper
{

  private String stateName;
  private int index;


  public CategoricalFeatureStateMapper()
  {
    super();
  }


  public CategoricalFeatureStateMapper(String stateName, int index)
  {
    this();
    this.stateName = stateName;
    this.index = index;
  }


  public String getStateName()
  {
    return this.stateName;
  }


  public int getIndex()
  {
    return this.index;
  }


  public String toString()
  {
    return String.format("state %s, index %d", this.stateName, this.index);
  }
}


