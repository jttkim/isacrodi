package isacrodi.ejb.entity;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.*;
import java.util.*;


@Entity
public class RecommendationTest
{

	private Integer id;
	private String description;
	private CropDisorderRecord cdr;


	RecommendationTest()
	{
		super();
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


	@OneToOne
	public CropDisorderRecord getCdr()
	{
		return cdr;
	}


	public void setCdr( CropDisorderRecord cdr)
	{
		this.cdr = cdr;
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
