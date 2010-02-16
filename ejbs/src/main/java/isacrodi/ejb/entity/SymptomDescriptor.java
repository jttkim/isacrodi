package isacrodi.ejb.entity;


import javax.persistence.Entity;

@Entity
public class SymptomDescriptor extends Descriptor
{
  
  public final CategoryType categoryType = CategoryType.SYMPTOM;

  
  SymptomDescriptor()
  {
    super();
  }

}
