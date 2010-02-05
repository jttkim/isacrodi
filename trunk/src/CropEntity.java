package isacrodi;


public class CropEntity()
{
	
	private String name;
	private String scientificName;


	cropEntity()
	{
		super();
	}


	public CroptEntity(String name, String scientificName)
	{
		this.name = name;
		this.scientificName = scientificName;
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
