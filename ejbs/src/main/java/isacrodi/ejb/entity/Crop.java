package isacrodi.ejb.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.*;
import java.util.*;

@Entity
public class Crop
{
	private Integer id;	
	private String name;
	private String scientificName;
	private Set<CropDisorderRecord> cdr;


	Crop()
	{
		super();
	}


	public Crop(String name, String scientificName)
	{
		this.name = name;
		this.scientificName = scientificName;
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


	@OneToMany(mappedBy="crop")
	public Set<CropDisorderRecord> getCdr()
	{
		return cdr;
	}


	public void setCdr(Set<CropDisorderRecord> cdr)
	{
		this.cdr = cdr;
	}


	public String getName()
	{
		return name;
	}


	public void setName(String name)
	{
		this.name = name;
	}


	public String getScientificName()
	{
		return scientificName;
	}


	public void setScientificName(String scientificName)
	{
		this.scientificName = scientificName;
	}

}
