package org.isacrodi.diagnosis;

import java.io.Serializable;

import org.isacrodi.ejb.entity.*;
import org.isacrodi.ejb.session.*;

import libsvm.svm_node;


/**
 * Numeric Feature Vector Mapper Component
 */
public class NumericSvmNodeComponentMapper extends AbstractSvmNodeComponentMapper implements Serializable
{
  // FIXME: cannot represent uninitialised state
  private int index;

  private static final long serialVersionUID = 1;


  public NumericSvmNodeComponentMapper(String featureName)
  {
    super(featureName);
  }


  public NumericSvmNodeComponentMapper(String featureName, int index)
  {
    this(featureName);
    this.index = index;
  }


  public int getIndex()
  {
    return this.index;
  }


  public void setIndex(int index)
  {
    this.index = index;
  }


  public String toString()
  {
    return String.format("NumericSvmNodeComponentMapper for %s, index = %d", this.featureName, this.index);
  }


  public void designateIndexes(int startIndex)
  {
    this.index = startIndex;
  }


  public int getMaxIndex()
  {
    return (this.index);
  }


  public svm_node[] map(AbstractFeature feature, svm_node[] node)
  {
    if (feature != null)
    {
      if (!(feature instanceof NumericFeature))
      {
	throw new IllegalArgumentException(String.format("feature %s is not numeric", feature.getName()));
      }
      // FIXME: should also verify that feature has expected name -- by method provided by abstract base class?
      NumericFeature numericFeature = (NumericFeature) feature;
      svm_node[] newNode = new svm_node[node.length + 1];
      for (int i = 0; i < node.length; i++)
      {
	newNode[i] = node[i];
      }
      node = newNode;
      node[node.length - 1] = new svm_node();
      node[node.length - 1].index = this.index;
      node[node.length - 1].value = numericFeature.getValue();
    }
    return (node);
  }
}
