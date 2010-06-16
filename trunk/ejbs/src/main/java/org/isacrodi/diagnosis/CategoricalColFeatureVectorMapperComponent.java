package org.isacrodi.diagnosis;

import org.isacrodi.ejb.entity.*;
import org.isacrodi.ejb.session.*;
import java.util.Set;
import java.util.HashSet;


/**
 * Categorical Collection Feature Vector Mapper Component
 */

public class CategoricalColFeatureVectorMapperComponent extends FeatureVectorMapperComponent
{

  private Set<CategoricalFeatureVectorMapperComponent> stateMapperSet;


  public CategoricalColFeatureVectorMapperComponent()
  {
    super();
    this.stateMapperSet = new HashSet<CategoricalFeatureVectorMapperComponent>();
  }


  public CategoricalColFeatureVectorMapperComponent(String name, int indexpresence)
  {
    super(name, indexpresence);
  }


  public void setCategoricalFeatureVectorMapperComponentSet(Set<CategoricalFeatureVectorMapperComponent> stateMapperSet)
  {
    this.stateMapperSet = stateMapperSet;
  }


  public Set<CategoricalFeatureVectorMapperComponent> getCategoricalFeatureVectorMapperComponentSet()
  {
    return this.stateMapperSet;
  }


  public void addCategoryElement(CategoricalFeatureVectorMapperComponent cfvm)
  {
    this.stateMapperSet.add(cfvm);
  }


  public int getArraySize()
  {
    return stateMapperSet.size();
  }


  public String toString()
  {
    return String.format("Nothing to return");
  }

}


