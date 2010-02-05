package isacrodi;

import image.*


@Entity
@Table(name="image")
public class ImageEntity()
{

	@Id
	@Column(name="imageId")
	private Integer imageId;
	@Column(name="name")
	private String name;


	ImageEntity()
	{

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



	public List getFeatureVector()
	{
		feature_vector = image.featureVector(imageId);
	}

}
