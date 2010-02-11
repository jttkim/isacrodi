package isacrodi.ejb.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;


@Entity
public class Procedure
{
	private Integer id;
	private String description;

	Procedure()
	{
		super();
	}
 

 	public Procedure(String description)
	{
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


	public String getDescription()
	{
		return description;
	}


	public void setDescription(String description)
	{
		this.description = description;
	}

}