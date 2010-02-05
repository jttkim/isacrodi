package isacrodi;


@Entity
@Table(name = "cdr")
public class CropDisorderRecordEntity()
{

	@Id
	@Column(name = "cdrId");
	private Integer cdrId;
	@OneToMany(mappedBy="cdrDes")
	private Set<Descriptor> descriptors;
  @OneToOne(mappedBy="cdrRec")
	private Integer recommendation;
	@OneToOne(mappedBy="cdrDiag");
	private Integer diagnosis;


	CropDisorderRecordEntity()
	{
		super();
	}


  public CropDisorderRecordEntity(Integer cdrId, Set<Descriptor> descriptors, Integer recommendation, Integer diagnosis)
	{
		this.cdrId = cdrId;
		this.descriptors = descriptors;
		this.recommendation = recommendation;
		this.diagnosis = diagnosis;
	}


	public Integer getCdrId()
	{
		return cdrId;
	}


	public void setCdrId(Integer cdrId) 
	{
		this.cdrId = cdrId;
	}


	public Set<Descriptor> getDescriptors()
	{
		return getDescriptors;
	}

	public void setDescriptors(Set<Descriptor> descriptors)
	{
		this.descriptors = descriptors;
	}

	
	public Integer getRecommendation()
	{
		AssessRecommendation(descriptors);
	}

	public Integer getDiagnosis()
	{
		FindDiagnosis(recommendation);
	}

}
