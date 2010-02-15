package isacrodi.ejb.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.Version;
import javax.persistence.OneToMany;
import javax.persistence.ManyToOne;
import java.util.Set;

@Entity
public class CropDisorderRecord
{
  private Integer id;
  private int version;
  private Integer recommendation;
  private Integer diagnosis;
  private Set<Descriptor> descriptorSet;
  private IsacrodiUser isacrodiuser;
  private Crop crop;

  private static final long serialVersionUID = 1;


  CropDisorderRecord()
  {
    super();
  }


  public CropDisorderRecord(Integer recommendation, Integer diagnosis)
  {
    this.recommendation = recommendation;
    this.diagnosis = diagnosis;
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

  
  @Version
  public int getVersion()
  {
    return (this.version);
  }


  public void setVersion(int version)
  {
    this.version = version;
  }


  @OneToMany(mappedBy="cropDisorderRecord")
  public Set<Descriptor> getDescriptorSet()
  {
    return (this.descriptorSet);
  }


  public void setDescriptorSet(Set<Descriptor> descriptorSet)
  {
    this.descriptorSet = descriptorSet;
  }


  // FIXME: fix case
  @ManyToOne
  public IsacrodiUser getisacrodiuser()
  {
    return (this.isacrodiuser);
  }


  public void setisacrodiuser(IsacrodiUser isacrodiuser)
  {
    this.isacrodiuser = isacrodiuser;
  }


  @ManyToOne
  public Crop getCrop()
  {
    return (this.crop);
  }


  public void setCrop(Crop crop)
  {
    this.crop = crop;
  }
}
