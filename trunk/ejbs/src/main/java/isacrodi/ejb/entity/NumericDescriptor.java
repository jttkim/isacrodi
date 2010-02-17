package isacrodi.ejb.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Version;

@Entity
public class NumericDescriptor extends Descriptor implements Serializable
{
  // JTK: this looks like a redundant recording of the type to me
  public final CategoryType categoryType = CategoryType.NUMERIC;
  private NumericType numericType;

  private static final long serialVersionUID = 1;

   
  NumericDescriptor()
  {
    super();
  }


  // JTK: according to the class diagram this should be a OneToMany
  // relationship -- that doesn't make much sense, though, it seems to
  // me it should be ManyToOne.
  @OneToOne
  public NumericType getNumericType()
  {
    return numericType;
  }


  public void setNumericType(NumericType numericType)
  {
    this.numericType = numericType;
  }
}
