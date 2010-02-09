package isacrodi.ejb.entity;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.*;
import java.util.*;

@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="DesTy", discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue("Descriptor")
public class Descriptor 
{
	private Integer id;
	private String descriptorType;
	private String name;
	private String description;
	private CropDisorderRecord cdr;

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


  @ManyToOne
  public CropDisorderRecord getCdr()
  {
	return cdr;
  }


  public void setCdr(CropDisorderRecord cdr)
  {
	this.cdr = cdr;
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
