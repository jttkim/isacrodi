package isacrodi.ejb.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;


@Entity
public class IsacrodiUser
{
	private Integer id;
	private String name;
	private String username;
	private String password;


	public User() {
  		super();
	}


	public User(String name, String username, String password)
	{

		this.name = name;
		this.username = username;
		this.password = password;
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


	public String getName()
	{
		return name;
	}
	

	public void setName(String name)
	{
		this.name = name;
	}


	public String getUsername()
	{
		return username;
	}
	

	public void setUsername(String username)
	{
		this.username = username;
	}


	public String getPassword()
	{
		return password;
	}
	

	public void setPassword(String password)
	{
		this.password = password;
	}
}

