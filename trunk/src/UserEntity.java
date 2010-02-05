package isacrodi


@Entity
@Table(name = "user")

public class UserEntity() 
{
	@Id
	@Column(name="name")
	private String name;
	@Column(name="username")
	private String username;
	@Column(name="password")
	private String password;


	public UserEntity() {
  		super();
	}


	public UserEntity(String name, String username, String password)
	{

		this.name = name;
		this.username = username
		this.password = password;
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

