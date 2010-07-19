package org.isacrodi.diagnosis;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;


import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

// FIXME
import libsvm.*;

import org.isacrodi.ejb.entity.*;
import org.isacrodi.ejb.session.*;
import org.isacrodi.util.io.*;


/**
 *  Feature Vector Mapper.
 */
public class SvmNodeFeatureVectorMapper implements FeatureVectorMapper<svm_node[]>, Serializable
{
  // FIXME: should probably use a specific base class of component mappers (BasicSvmNodeComponentMapper?)
  private List<AbstractSvmNodeComponentMapper> componentMapperList;

  private static final long serialVersionUID = 1;


  protected static AbstractSvmNodeComponentMapper makeComponentMapper(AbstractFeature feature)
  {
    AbstractSvmNodeComponentMapper componentMapper = null;
    if (feature instanceof NumericFeature)
    {
      componentMapper = new NumericSvmNodeComponentMapper(feature.getName());
    }
    else if (feature instanceof CategoricalFeature)
    {
      componentMapper = new CategoricalSvmNodeComponentMapper(feature.getName());
    }
    else
    {
      throw new IllegalArgumentException("unsupported feature type: " + feature.getClass().getCanonicalName());
    }
    return (componentMapper);
  }


  protected static void updateComponentMapper(AbstractFeature feature, AbstractSvmNodeComponentMapper componentMapper)
  {
    if ((feature instanceof NumericFeature) && (componentMapper instanceof NumericSvmNodeComponentMapper))
    {
      NumericFeature numericFeature = (NumericFeature) feature;
      NumericSvmNodeComponentMapper numericSvmNodeComponentMapper = (NumericSvmNodeComponentMapper) componentMapper;
      // FIXME: nothing to do really if we're going to map to sparse vectors -- could collect set of values to obtain statistics for scaling etc., though.
    }
    else if ((feature instanceof CategoricalFeature) && (componentMapper instanceof CategoricalSvmNodeComponentMapper))
    {
      CategoricalFeature categoricalFeature = (CategoricalFeature) feature;
      CategoricalSvmNodeComponentMapper categoricalSvmNodeComponentMapper = (CategoricalSvmNodeComponentMapper) componentMapper;
      if (!categoricalSvmNodeComponentMapper.hasState(categoricalFeature.getState()))
      {
	// FIXME: using index -1 to try and trigger exceptions if index designation is forgotten or fails -- should really set a proper NA value
	categoricalSvmNodeComponentMapper.addState(categoricalFeature.getState(), -1);
      }
    }
    else
    {
      throw new IllegalArgumentException(String.format("feature / component mapper mismatch or unsupported feature or mapper: feature type %s, mapper type %s", feature.getClass().getSimpleName(), componentMapper.getClass().getSimpleName()));
    }
  }


  public SvmNodeFeatureVectorMapper()
  {
    super();
    this.componentMapperList = new ArrayList<AbstractSvmNodeComponentMapper>();
  }


  public SvmNodeFeatureVectorMapper(List<AbstractSvmNodeComponentMapper> componentMapperList)
  {
    this();
    this.componentMapperList = componentMapperList;
  }


  /**
   * Construct a mapper based on a collection of feature vectors.
   *
   * Each feature contained in a feature vector of the collection, and
   * each state of a categorical feature occurring in the collection,
   * is guaranteed to be mappable by the mapper constructed.
   *
   * @param featureVectorCollection the feature vector collection
   */
  public SvmNodeFeatureVectorMapper(Collection<FeatureVector> featureVectorCollection)
  {
    this();
    Map<String, AbstractSvmNodeComponentMapper> componentMapperMap = new HashMap<String, AbstractSvmNodeComponentMapper>();
    for (FeatureVector featureVector : featureVectorCollection)
    {
      for (String featureName : featureVector.keySet())
      {
	AbstractFeature feature = featureVector.get(featureName);
	AbstractSvmNodeComponentMapper componentMapper = componentMapperMap.get(featureName);
	if (componentMapper == null)
	{
	  componentMapper = makeComponentMapper(feature);
	  componentMapperMap.put(featureName, componentMapper);
	}
	updateComponentMapper(feature, componentMapper);
      }
    }
    this.componentMapperList = new ArrayList<AbstractSvmNodeComponentMapper>(componentMapperMap.values());
    this.designateIndexes();
  }


  public void addComponentMapper(AbstractSvmNodeComponentMapper componentMapper)
  {
    this.componentMapperList.add(componentMapper);
  }


  public List<AbstractSvmNodeComponentMapper> getComponentMapperList()
  {
    return (this.componentMapperList);
  }


  public int targetSpaceDimension()
  {
    int d = 0;
    for (AbstractSvmNodeComponentMapper c : this.componentMapperList)
    {
      int n = c.getMaxIndex();
      if (n > d)
      {
	d = n;
      }
    }
    return (d + 1);
  }


  /**
   * Designate indexes for all component mappers of this feature mapper.
   *
   * <p><strong>Notice:</strong> Existing indexes are wiped out by
   * this method. This method is intended to be used to complete
   * construction of a feature vector mapper based on a training set
   * only.</p>
   */
  public void designateIndexes()
  {
    int startIndex = 0;
    for (AbstractSvmNodeComponentMapper componentMapper : this.componentMapperList)
    {
      componentMapper.designateIndexes(startIndex);
      startIndex = componentMapper.getMaxIndex() + 1;
    }
  }


  public svm_node[] map(FeatureVector featureVector)
  {
    svm_node[] node = new svm_node[0];
    for (AbstractSvmNodeComponentMapper c : this.componentMapperList)
    {
      AbstractFeature f = featureVector.get(c.getFeatureName());
      node = c.map(f, node);
    }
    return (node);
  }


  public String toString()
  {
    String s = String.format("SvmNodeFeatureVectorMapper, target space dimension = %d\n", this.targetSpaceDimension());
    for (AbstractSvmNodeComponentMapper m : this.getComponentMapperList())
    {
      s += String.format("%s\n", m.toString());
    }
    return (s);
  }
}
