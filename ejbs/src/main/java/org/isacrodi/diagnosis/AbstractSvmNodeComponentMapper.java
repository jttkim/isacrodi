package org.isacrodi.diagnosis;

import org.isacrodi.ejb.entity.*;
import org.isacrodi.ejb.session.*;

import libsvm.svm_node;


public abstract class AbstractSvmNodeComponentMapper
{
  protected String featureName;


  public AbstractSvmNodeComponentMapper(String featureName)
  {
    super();
    this.featureName = featureName;
  }


  // FIXME: this should be the base class of component mappers, other
  // abstract classes should inherit the featureName property.
  public String getFeatureName()
  {
    return this.featureName = featureName;
  }


  public void setFeatureName(String featureName)
  {
    this.featureName = featureName;
  }


  public abstract void designateIndexes(int startIndex);


  public abstract int getMaxIndex();


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
