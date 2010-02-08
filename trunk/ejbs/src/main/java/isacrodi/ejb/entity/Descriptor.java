package isacrodi.ejb.entity;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.*;

@Entity
public class Descriptor
{
	private Integer id;
	@ManyToOne
	@JoinColumn(name="id")
	private CropDisorderRecord cdrDesc;
	private String descriptorType;
	private String name;
	private String description;


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

  @Id @GeneratedValue
  public Integer getId()
  {
	return id;
  }


  public void setId(Integer id)
  {
  	this.id = id;
  }


  public CropDisorderRecord getCdrDesc()
  {
	return cdrDesc;
  }


  public void setCdrDesc()
  {
	this.cdrDesc = cdrDesc;
  }


  public String getDescriptorType() 
	{
		return descriptorType;
	}

	
	public void setDescriptorType(String descriptorType)
	{
		this.descriptorType = descriptorType;
	}


  public String getName() 
	{
		return name;
	}

	
	public void setName(String name)
	{
		this.name = name;
	}


  public String getDescription() 
	{
		return description;
	}

	
	public void setDescription(String description)
	{
		this.description = description;
	}


}
