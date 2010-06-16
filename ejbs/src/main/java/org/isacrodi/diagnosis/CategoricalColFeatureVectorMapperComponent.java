package org.isacrodi.diagnosis;

import org.isacrodi.ejb.entity.*;
import org.isacrodi.ejb.session.*;
import java.util.Set;
import java.util.HashSet;


/*
** Categorical Collection Feature Vector Mapper Component
*/

public class CategoricalColFeatureVectorMapperComponent extends FeatureVectorMapperComponent
{

  private Set<CategoricalFeatureVectorMapperComponent> cfvms;


  public CategoricalColFeatureVectorMapperComponent()
  {
    super();
    this.cfvms = new HashSet<CategoricalFeatureVectorMapperComponent>();
  }


  public CategoricalColFeatureVectorMapperComponent(String name, int indexpresence)
  {
    super(name, indexpresence);
  }


  public void setCategoricalFeatureVectorMapperComponentSet(Set<CategoricalFeatureVectorMapperComponent> cfvms)
  {
    this.cfvms = cfvms;
  }


  public Set<CategoricalFeatureVectorMapperComponent> getCategoricalFeatureVectorMapperComponentSet()
  {
    return this.cfvms;
  }


  public void addCategoryElement(CategoricalFeatureVectorMapperComponent cfvm)
  {
    this.cfvms.add(cfvm);
  }


  public int getArraySize()
  {
    return cfvms.size();
  }


  public String toString()
  {
    return String.format("Nothing to return");
  }

}


