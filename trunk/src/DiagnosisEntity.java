package isacrodi;


@Entity
@Table(name = "diagnosis")
public class DiagnosisEntity()
{

	@Id
	@Column(name="diagId")
	private Integer diagId;
	@OneToOne
	@JoinColumn(name="cdrDiag")
	private CropDisorderRecordEntity cdrDiag;
	@Column(name="description")
	private String description;

	DiagnosisEntity()
	{
		super();
	}


	public Integer getDiagId()
	{
		return diagId;
	}


	public void setDiagId(Integer diagId)
	{
		this.diagId = diagId;
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
