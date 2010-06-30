package org.isacrodi.diagnosis;

import org.isacrodi.ejb.entity.*;
import org.isacrodi.ejb.session.*;

import libsvm.svm_node;

/**
 * Abstract Feature Vector Mapper Component 
 */
public abstract class AbstractComponentMapper
{
  protected String featureName;
  protected int indexPresence;


  public AbstractComponentMapper()
  {
    super();
  }


  public AbstractComponentMapper(String featureName, int indexPresence)
  {
    this();
    this.featureName = featureName;
    this.indexPresence = indexPresence;
  }


  public String getFeatureName()
  {
    return this.featureName = featureName;
  }


  public void setFeatureName(String featureName)
  {
    this.featureName = featureName;
  }


  public int getIndexPresence()
  {
    return (this.indexPresence);
  }


  public void setIndexPresence(int indexPresence)
  {
    this.indexPresence = indexPresence;
  }


  public String toString()
  {
    return String.format("%s %d", this.featureName, this.indexPresence);
  }


  abstract int getMaxIndex();


  /**
   * Set components in the SVM feature vector based on an Isacrodi
   * level feature.
   *
   * <p><strong>Notice:</strong> Implementations of this method may
   * modify {@code node}.</p>
   *
   * @param feature the feature, a {@code null} value indicates a missing feature
   * @param node the libsvm feature vector within which to set components
   * @return a reference to {@code node}
   */
  abstract svm_node[] map(AbstractFeature feature, svm_node[] node);
}
