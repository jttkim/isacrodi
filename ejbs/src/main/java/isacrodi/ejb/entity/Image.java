package isacrodi.ejb.entity;



public class Image
{

	private Integer imageId;
	private String name;


	Image()
	{
		super();
	}


	public Integer getImageId()
	{
		return imageId;
	}


	public void setImageId(Integer imageId)
	{
		this.imageId = imageId;
	}


	public String getName()
	{
		return name;
	}


	public void setName(String name)
	{
		this.name = name;
	}


}
