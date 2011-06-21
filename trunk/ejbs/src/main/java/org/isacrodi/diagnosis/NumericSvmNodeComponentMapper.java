package org.isacrodi.diagnosis;

import java.util.List;
import java.util.ArrayList;

import org.isacrodi.ejb.entity.*;
import org.isacrodi.ejb.session.*;

import libsvm.svm_node;


/**
 * Numeric Feature Vector Mapper Component.
 *
 * <p>This mapper scales and translates values so that the mean of the
 * training data is 0 and the standard deviation is 1.</p>
 *
 * <p>Internally, the mapper maintains a list of training values and
 * it caches the mean and the standard deviation. Caches are cleared
 * when training data is added, and recomputed when values are mapped.
 * This technique is not robust to concurrency.</p>
 */
public class NumericSvmNodeComponentMapper extends AbstractSvmNodeComponentMapper
{
  // FIXME: cannot represent uninitialised state
  private int index;
  private List<Double> trainingValueList;
  private Double mean;
  private Double stddev;

  private static final long serialVersionUID = 1;


  public NumericSvmNodeComponentMapper(String featureName)
  {
    super(featureName);
    this.trainingValueList = new ArrayList<Double>();
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


  /**
   * Add a value to the list of training values.
   *
   * <p>The cached mean and standard deviation are voided by this
   * method, so that they are re-calcuclated (at a cost of O(number of
   * training values)) upon the next invocation of {@link
   * #map}.</p>
   */
  public void addTrainingValue(double value)
  {
    this.trainingValueList.add(new Double(value));
    this.mean = null;
    this.stddev = null;
  }


  public String toString()
  {
    if (this.trainingValueList.size() > 0)
    {
      this.updateStats();
    }
    String meanString = "<null>";
    if (this.mean != null)
    {
      meanString = this.mean.toString();
    }
    String stddevString = "<null>";
    if (this.stddev != null)
    {
      stddevString = this.stddev.toString();
    }
    return String.format("NumericSvmNodeComponentMapper for %s, index = %d, mean = %s, stddev = %s", this.featureName, this.index, meanString, stddevString);
  }


  public void designateIndexes(int startIndex)
  {
    this.index = startIndex;
  }


  public int getMaxIndex()
  {
    return (this.index);
  }


  private void updateStats()
  {
    // FIXME: use proper exceptions
    if (this.trainingValueList.size() == 0)
    {
      throw new RuntimeException("insufficient data");
    }
    double s = 0.0;
    for (Double v : this.trainingValueList)
    {
      s += v.doubleValue();
    }
    this.mean = new Double(s / this.trainingValueList.size());
    double ssd = 0.0;
    for (Double v : this.trainingValueList)
    {
      double d = v.doubleValue() - this.mean.doubleValue();
      ssd += d * d;
    }
    this.stddev = new Double(Math.sqrt(ssd / (this.trainingValueList.size() - 1)));
  }


  private double mappedValue(double value)
  {
    if (this.mean == null)
    {
      this.updateStats();
    }
    // FIXME: should not accept very small values either
    if (this.stddev.doubleValue() == 0.0)
    {
      return (0.0);
    }
    return ((value - this.mean.doubleValue()) / this.stddev.doubleValue());
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
      node[node.length - 1].value = this.mappedValue(numericFeature.getValue());
    }
    return (node);
  }
}
