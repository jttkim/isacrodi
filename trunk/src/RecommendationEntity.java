package isacrodi;


@Entity
@Table(name = "recommendation")
public class RecommendationEntity()
{

	@Id
	@Column(name="recId")
	private Integer recId;
	@OneToOne
	@JoinColumn(name="cdrRec")
	private CropDisorderRecordEntity cdrRec;
	@Column(name="description")
	private String description;


	RecommendationEntity()
	{
		super();
	}


	public Integer getRecId()
	{
		return recId;
	}


	public void setRecId(Integer recId)
	{
		this.recId = recId;
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
