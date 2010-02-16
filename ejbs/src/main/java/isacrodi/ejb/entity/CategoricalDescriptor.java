package isacrodi.ejb.entity;


import javax.persistence.Entity;

@Entity
public class CategoricalDescriptor extends Descriptor
{
  
  public final CategoryType categoryType = CategoryType.CATEGORICAL;

  
  CategoricalDescriptor()
  {
    super();
  }

}
