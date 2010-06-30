package org.isacrodi.diagnosis;

import java.util.Map;
import java.util.HashMap;

import org.isacrodi.ejb.entity.*;
import org.isacrodi.ejb.session.*;

import libsvm.svm_node;

/**
 * Mapper for a categorical component.
 */

public class CategoricalComponentMapper extends AbstractComponentMapper
{
  /*
   * as a simplification, this implementation doesn't use a dedicated
   * state mapper class as in the previous design, a hash map using
   * state names as keys and indexes as values is sufficient.
   */
  private Map<String, Integer> stateIndexMap;


  public CategoricalComponentMapper()
  {
    super();
    this.stateIndexMap = new HashMap<String, Integer>();
  }


  public CategoricalComponentMapper(String featureName)
  {
    // FIXME: partially initialised, cannot represent uninitialised indexPresence
    super(featureName);
  }


  public CategoricalComponentMapper(String featureName, int indexPresence)
  {
    super(featureName, indexPresence);
    this.stateIndexMap = new HashMap<String, Integer>();
  }


  /**
   * Determine whether a state is mapped.
   *
   * @param stateName the name of the state to be checked
   * @return {@code true} if the state is mapped
   */
  public boolean hasState(String stateName)
  {
    return (this.stateIndexMap.containsKey(stateName));
  }


  /**
   * Add a state with a given index.
   *
   * @param stateName the name of the state
   * @param index the index to be associated with that state
   *
   * @throws IllegalArgumentException if the state name is alrady taken
   */
  public void addState(String stateName, int index)
  {
    if (this.hasState(stateName))
    {
      throw new IllegalArgumentException(String.format("state \"%s\" already mapped", stateName));
    }
    this.stateIndexMap.put(stateName, new Integer(index));
  }


  public int getNumberOfStates()
  {
    return this.stateIndexMap.size();
  }


  public String toString()
  {
    String s = String.format("CategoricalComponentMapper(indexPresence = %d", this.indexPresence);
    for (String stateName : stateIndexMap.keySet())
    {
      s += String.format(", %s -> %d", stateName, this.stateIndexMap.get(stateName).intValue());
    }
    return (s + ")");
  }


  public void designateIndexes(int startIndex)
  {
    Map<String, Integer> newMap = new HashMap<String, Integer>();
    int i = startIndex;
    for (String stateName : this.stateIndexMap.keySet())
    {
      newMap.put(stateName, new Integer(i++));
    }
    this.indexPresence = i++;
    this.stateIndexMap = newMap;
  }


  public int getMaxIndex()
  {
    int m = this.indexPresence;
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
    if (feature == null)
    {
      for (String stateName : this.stateIndexMap.keySet())
      {
	int index = this.stateIndexMap.get(stateName).intValue();
	node[index] = new svm_node();
	node[index].index = index;
	node[index].value = 0.0;
      }
      node[this.indexPresence] = new svm_node();
      node[this.indexPresence].index = this.indexPresence;
      node[this.indexPresence].value = 0.0;
    }
    else
    {
      if (!(feature instanceof CategoricalFeature))
      {
	throw new IllegalArgumentException(String.format("feature %s is not categorical", feature.getName()));
      }
      // FIXME: should also verify that feature has expected name -- by method provided by abstract base class?
      CategoricalFeature categoricalFeature = (CategoricalFeature) feature;
      for (String stateName : this.stateIndexMap.keySet())
      {
	int index = this.stateIndexMap.get(stateName).intValue();
	node[index] = new svm_node();
	node[index].index = index;
	if (stateName.equals(categoricalFeature.getState()))
	{
	  node[index].value = 1.0;
	}
	else
	{
	  node[index].value = 0.0;
	}
      }
      node[this.indexPresence] = new svm_node();
      node[this.indexPresence].index = this.indexPresence;
      node[this.indexPresence].value = 1.0;
    }
    return (node);
  }
}
