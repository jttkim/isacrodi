package org.isacrodi.diagnosis;

import org.isacrodi.ejb.entity.*;
import org.isacrodi.ejb.session.*;


/*
** Numeric Feature Vector Mapper Component
*/

public class NumericFeatureVectorMapperComponent extends FeatureVectorMapperComponent
{

  private int index;
  private double valuemissing;


  public NumericFeatureVectorMapperComponent()
  {
    super();
  }


  public NumericFeatureVectorMapperComponent(String name, int index, int indexpresence, double valuemissing)
  {
    super(name, indexpresence);
    this.index = index;
    this.valuemissing = valuemissing;
  }


  public int getIndex()
  {
    return this.index;
  }


  public double getValueMissing()
  {
    return this.valuemissing;
  }


  public String toString()
  {
    return String.format("%d %f", this.index, this.valuemissing);
  }

}

