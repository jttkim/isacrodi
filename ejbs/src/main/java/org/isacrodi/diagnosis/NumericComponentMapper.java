package org.isacrodi.diagnosis;

import org.isacrodi.ejb.entity.*;
import org.isacrodi.ejb.session.*;

import libsvm.svm_node;


/**
 * Numeric Feature Vector Mapper Component
 */

public class NumericComponentMapper extends AbstractComponentMapper
{

  private int index;
  private double valueMissing;


  public NumericComponentMapper()
  {
    super();
  }


  public NumericComponentMapper(String name, int index, int indexPresence, double valuemissing)
  {
    super(name, indexPresence);
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


  svm_node map(AbstractFeature feature, svm_node node)
  {
    if (feature == null)
    {
      node[this.index].index = this.index;
      node[this.index].value = this.valueMissing;
      node[this.indexPresence].index = this.indexPresence;
      node[this.indexPresence].value = 0.0;
    }
    else
    {
      if (!(feature instanceof NumericFeature))
      {
	throw new IllegalArgumentException(String.format("feature %s is not numeric", feature.getName()));
      }
      NumericFeature numericFeature = (NumericFeature) feature;
      node[this.index].index = this.index;
      node[this.index].value = numericFeature.getValue();
      node[this.indexPresence].index = this.indexPresence;
      node[this.indexPresence].value = 1.0;
    }
    return (node);
  }
}

