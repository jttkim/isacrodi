package org.isacrodi.diagnosis;

import java.util.Map;
import java.util.Set;
import java.util.HashMap;

import org.isacrodi.ejb.entity.*;
import org.isacrodi.ejb.session.*;

import libsvm.svm_node;

/**
 * Mapper for a categorical component.
 */

public class CategoricalSvmNodeComponentMapper extends AbstractSvmNodeComponentMapper
{
  private Map<String, Integer> stateIndexMap;

  private static final long serialVersionUID = 1;


  public CategoricalSvmNodeComponentMapper(String featureName)
  {
    // FIXME: partially initialised, cannot represent uninitialised indexPresence
    super(featureName);
    this.stateIndexMap = new HashMap<String, Integer>();
  }


  /**
   * Determine whether all states in a set can be mapped.
   *
   * @param stateNameSet the set of names of the states to be checked
   * @return {@code true} if all states are mapped
   */
  public boolean hasAllStates(Set<String> stateNameSet)
  {
    // FIXME: shared method with presence indicating
    boolean allMapped = true;
    for (String stateName : stateNameSet)
    {
      allMapped = allMapped && this.stateIndexMap.containsKey(stateName);
    }
    return (allMapped);
  }


  /**
   * Determine what states are not mapped.
   *
   * @param stateNameSet the set of names of the states to be checked
   */
  public Map<String, Integer> getUnmappedStates(Set<String> stateNameSet)
  {
    Map<String, Integer> unmappedStates = new HashMap<String, Integer>();
    int i = 0;

    for (String stateName : stateNameSet)
    {
      if (!this.stateIndexMap.containsKey(stateName))
      {
        unmappedStates.put(stateName, i);
	i++;
      }
    }
    return (unmappedStates);
  }


  /**
   * Add a state with a given index.
   *
   * @param stateName the name of the state
   * @param index the index to be associated with that state
   *
   * @throws IllegalArgumentException if the state name is already taken
   */
  public void addState(String stateName, Integer index)
  {
    //System.err.println(stateName);
    if (this.stateIndexMap.containsKey(stateName))
    {
      throw new IllegalArgumentException(String.format("state \"%s\" already mapped", stateName));
    }
    this.stateIndexMap.put(stateName, index);
  }


  public void addState(String stateName)
  {
    this.addState(stateName, null);
  }


  public int getNumberOfStates()
  {
    return this.stateIndexMap.size();
  }


  public String toString()
  {
    String s = String.format("CategoricalSvmNodeComponentMapper for feature %s", this.featureName);
    for (String stateName : stateIndexMap.keySet())
    {
      s += String.format(", %s -> %d", stateName, this.stateIndexMap.get(stateName).intValue());
    }
    return (s);
  }


  public void designateIndexes(int startIndex)
  {
    Map<String, Integer> newMap = new HashMap<String, Integer>();
    int i = startIndex;
    for (String stateName : this.stateIndexMap.keySet())
    {
      newMap.put(stateName, new Integer(i++));
    }
    this.stateIndexMap = newMap;
  }


  public int getMaxIndex()
  {
    int m = -1;
    for (String stateName : this.stateIndexMap.keySet())
    {
      int i = this.stateIndexMap.get(stateName).intValue();
      if (i > m)
      {
        m = i;
      }
    }
    return (m);
  }


  public svm_node[] map(AbstractFeature feature, svm_node[] node)
  {
    if (feature != null)
    {
      if (!(feature instanceof CategoricalFeature))
      {
        throw new IllegalArgumentException(String.format("feature %s is not categorical", feature.getName()));
      }
      // FIXME: should also verify that feature has expected name -- by method provided by abstract base class?
      CategoricalFeature categoricalFeature = (CategoricalFeature) feature;
      svm_node[] c = new svm_node[this.stateIndexMap.size()];
      int i = 0;
      for (String stateName : this.stateIndexMap.keySet())
      {
        Integer index = this.stateIndexMap.get(stateName).intValue();
	if (index == null)
	{
	  throw new IllegalStateException(String.format("no index designated for state %s", stateName));
	}
        c[i] = new svm_node();
        c[i].index = index.intValue();
        if (categoricalFeature.containsState(stateName))
        {
          c[i++].value = 1.0;
        }
        else
        {
          c[i++].value = 0.0;
        }
      }
      svm_node[] newNode = new svm_node[node.length + c.length];
      for (int j = 0; j < node.length; j++)
      {
	newNode[j] = node[j];
      }
      for (int j = 0; j < c.length; j++)
      {
	newNode[node.length + j] = c[j];
      }
      node = newNode;
    }
    return (node);
  }
}
