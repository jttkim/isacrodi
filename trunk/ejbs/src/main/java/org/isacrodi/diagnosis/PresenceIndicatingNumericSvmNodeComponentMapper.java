package org.isacrodi.diagnosis;

import java.io.Serializable;

import org.isacrodi.ejb.entity.*;
import org.isacrodi.ejb.session.*;

import libsvm.svm_node;


/**
 * Numeric Feature Vector Mapper Component
 */

public class PresenceIndicatingNumericSvmNodeComponentMapper extends PresenceIndicatingSvmNodeComponentMapper implements Serializable
{
  // FIXME: cannot represent uninitialised state
  private int index;
  private double valueMissing;

  private static final long serialVersionUID = 1;


  public PresenceIndicatingNumericSvmNodeComponentMapper(String featureName)
  {
    super(featureName);
  }


  public PresenceIndicatingNumericSvmNodeComponentMapper(String featureName, double valueMissing)
  {
    this(featureName);
    this.valueMissing = valueMissing;
  }


  public PresenceIndicatingNumericSvmNodeComponentMapper(String featureName, int index, int indexPresence, double valueMissing)
  {
    super(featureName, indexPresence);
    this.index = index;
    this.valueMissing = valueMissing;
  }


  public int getIndex()
  {
    return this.index;
  }


  public void setIndex(int index)
  {
    this.index = index;
  }


  public double getValueMissing()
  {
    return this.valueMissing;
  }


  public void setValueMissing(double valueMissing)
  {
    this.valueMissing = valueMissing;
  }


  public String toString()
  {
    return String.format("PresenceIndicatingNumericSvmNodeComponentMapper for %s, index = %d, indexPresence = %d, valueMissing = %f", this.featureName, this.index, this.indexPresence, this.valueMissing);
  }


  public void designateIndexes(int startIndex)
  {
    int i = startIndex;
    this.index = i++;
    this.indexPresence = i++;
  }


  public int getMaxIndex()
  {
    if (this.index > this.indexPresence)
    {
      return (this.index);
    }
    else
    {
      return (this.indexPresence);
    }
  }


  public svm_node[] map(AbstractFeature feature, svm_node[] node)
  {
    if (feature == null)
    {
      node[this.index] = new svm_node();
      node[this.index].index = this.index;
      node[this.index].value = this.valueMissing;
      node[this.indexPresence] = new svm_node();
      node[this.indexPresence].index = this.indexPresence;
      node[this.indexPresence].value = 0.0;
    }
    else
    {
      if (!(feature instanceof NumericFeature))
      {
	throw new IllegalArgumentException(String.format("feature %s is not numeric", feature.getName()));
      }
      // FIXME: should also verify that feature has expected name -- by method provided by abstract base class?
      NumericFeature numericFeature = (NumericFeature) feature;
      node[this.index] = new svm_node();
      node[this.index].index = this.index;
      node[this.index].value = numericFeature.getValue();
      node[this.indexPresence] = new svm_node();
      node[this.indexPresence].index = this.indexPresence;
      node[this.indexPresence].value = 1.0;
    }
    return (node);
  }
}
