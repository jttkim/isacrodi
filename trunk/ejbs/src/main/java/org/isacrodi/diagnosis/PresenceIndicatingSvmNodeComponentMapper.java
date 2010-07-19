package org.isacrodi.diagnosis;

import org.isacrodi.ejb.entity.*;
import org.isacrodi.ejb.session.*;

import libsvm.svm_node;

/**
 * Base class for presence indicating component mappers.
 */
public abstract class PresenceIndicatingSvmNodeComponentMapper extends AbstractSvmNodeComponentMapper
{
  // FIXME: should manage value to use for missing values as well...??
  protected int indexPresence;


  public PresenceIndicatingSvmNodeComponentMapper(String featureName)
  {
    super(featureName);
    this.featureName = featureName;
  }


  public PresenceIndicatingSvmNodeComponentMapper(String featureName, int indexPresence)
  {
    this(featureName);
    this.indexPresence = indexPresence;
  }


  public int getIndexPresence()
  {
    return (this.indexPresence);
  }


  public void setIndexPresence(int indexPresence)
  {
    this.indexPresence = indexPresence;
  }
}
