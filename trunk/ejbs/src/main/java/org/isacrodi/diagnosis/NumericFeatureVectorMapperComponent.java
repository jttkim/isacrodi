package org.isacrodi.diagnosis;

import org.isacrodi.ejb.entity.*;
import org.isacrodi.ejb.session.*;


/*
** Numeric Feature Vector Mapper Component
*/

public class NumericFeatureVectorMapperComponent extends FeatureVectorMapperComponent
{

  private int index;
  private int indexpresence;
  private double valuemissing;


  public NumericFeatureVectorMapperComponent()
  {
    super();
  }


  public NumericFeatureVectorMapperComponent(String name, int index, int indexpresence, double valuemissing)
  {
    super(name);
    this.index = index;
    this.indexpresence = indexpresence;
    this.valuemissing = valuemissing;
  }


  public int getIndex()
  {
    return this.index;
  }


  public int getIndexPresence()
  {
    return this.indexpresence;
  }


  public double getValueMissing()
  {
    return this.valuemissing;
  }


  public String toString()
  {
    return String.format("%d %d %f", this.index, this.indexpresence, this.valuemissing);
  }

}

