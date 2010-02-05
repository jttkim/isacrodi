package isacrodi;


@Entity
@table(name = "descriptor")
public class DescriptorEntity()
{
	@Id
	@Colum(name = "descId")
	private Integer descId;
	@ManyToOne
	@JoinColumn(name="cdrId")
	private CropDisorderRecordEntity cdrDes;
	@Column(name = "descriptorType")
	private String descriptorType;
	@Column(name = ("name")
	private String name;
	@Column(name = "description")
	private String description;


  DescriptorEntity()
	{
		super();
	}
  
	
	public DescriptorEntity(String descriptorType, String name, String description)
	{
		this.descriptorType = descriptorType;
		this.name = name;
		this.description = description;
	}


  public String getDescriptorType() 
	{
		return descriptorType;
	}

	
	public void setDescriptorType(String descriptorType)
	{
		this.descriptorType = descriptorType;
	}


  public String getName() 
	{
		return name;
	}

	
	public void setName(String name)
	{
		this.name = name;
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
