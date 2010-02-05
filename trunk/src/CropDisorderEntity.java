package isacrodi

@Entity
@Table(name = "cropDisorder")

public class CropDisorderEntity()
{
	@Id
	@Column(name = "name")
	private String name;
	@Column(name = "scientificName")
	private String scientificName;


  CropDisorderEntity() 
	{
		super();
	}


  public String CropDisorderEntity(String name, String scientificName) 
	{
		this.name = name;
		this scientificName = scientificName;
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
		this.name = scientificName;
	}

}
