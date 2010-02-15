package isacrodi.ejb.entity;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.Version;
import javax.persistence.ManyToOne;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
// JTK: is there any need to specify the column name and type?
// @DiscriminatorColumn(name="DesTy", discriminatorType=DiscriminatorType.STRING)
// @DiscriminatorValue("Descriptor")
public class Descriptor 
{
  private Integer id;
  private int version;
  private String descriptorType;
  private String name;
  private String description;
  private CropDisorderRecord cropDisorderRecord;

  private static final long serialVersionUID = 1;


  Descriptor() 
  {
    super();
  }

  public Descriptor(String descriptorType, String name, String description)
  {
    this.descriptorType = descriptorType;
    this.name = name;
    this.description = description;
  }

  @Id
  @GeneratedValue
  public Integer getId()
  {
    return (this.id);
  }


  public void setId(Integer id)
  {
    this.id = id;
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


  @ManyToOne
  public CropDisorderRecord getCropDisorderRecord()
  {
    return (this.cropDisorderRecord);
  }


  public void setCropDisorderRecord(CropDisorderRecord cropDisorderRecord)
  {
    this.cropDisorderRecord = cropDisorderRecord;
  }

  // JTK: removed descriptorType property as it is redundant to class


  public String getName() 
  {
    return (this.name);
  }

	
  public void setName(String name)
  {
    this.name = name;
  }


  // JTK: what's the purpose of this property?
  public String getDescription() 
  {
    return (this.description);
  }

	
  public void setDescription(String description)
  {
    this.description = description;
  }
}
