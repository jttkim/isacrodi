package isacrodi.ejb.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.*;
import java.util.*;

@Entity
public class IsacrodiUser
{
	private Integer id;
	private String name;
	private String username;
	private String password;
	private Set<CropDisorderRecord> cdr;


	public IsacrodiUser() {
  		super();
	}


	public IsacrodiUser(String name, String username, String password)
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


	@OneToMany(mappedBy="isacrodiuser")
	public Set <CropDisorderRecord> getCdr()
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

