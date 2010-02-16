package isacrodi.ejb.entity;


import javax.persistence.Entity;
import javax.persistence.OneToOne;
import java.io.Serializable;

@Entity
public class NumericDescriptor extends Descriptor implements Serializable
{
 
  public final CategoryType categoryType = CategoryType.NUMERIC;
  private NumericType numericType;

   
  NumericDescriptor()
  {
    super();
  }

  @OneToOne(mappedBy="numericDescriptor")
  public NumericType getNumericType()
  {
    return numericType;
  }


  public void setNumericType(NumericType numericType)
  {
    this.numericType = numericType;
  }

}
