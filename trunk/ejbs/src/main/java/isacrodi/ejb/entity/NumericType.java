package isacrodi.ejb.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.OneToOne;
import javax.persistence.Version;
import java.io.Serializable;


@Entity
public class NumericType implements Serializable
{
  private Integer id;
  private int version;
  private String typeName;
  private NumericDescriptor numericDescriptor;

  private static final long serialVersionUID = 1;


  NumericType()
  {
    super();
  }
 

  @Id
  @GeneratedValue
  public Integer getId()
  {
    return id;
  }


  public void setId(Integer id)
  {
    this.id = id;
  }


  @OneToOne(mappedBy="numericType")
  public NumericDescriptor getNumericDescriptor()
  {
    return numericDescriptor;
  }


  public void setNumericDescriptor(NumericDescriptor numericDescriptor)
  {
    this.numericDescriptor = numericDescriptor;
  }


  public String getTypeName()
  {
    return typeName;
  }


  public void setNumericType(String typeName)
  {
    this.typeName = typeName;
  }


  @Version
  public int getVersion()
  {
    return (this.version);
  }


  public void setVersion(int version)
  {
    this.version = version;
  }


}
