package isacrodi;

@Entity
@Table(name = "procedure")


public class ProcedureEntity()
{
	@Id
	@Column(name="procId")
	private Integer procId;

	@column(name="description")
	private String description;

	ProcedureEntity()
	{
		super();
	}
 

 	public ProcedureEntity(Integer procId, String description)
	{
		this.procID = procId;
		this.description = description;
	}


	public String getProcId()
	{
		return procID;
	}


	public void setProcId(Integer procId)
	{
		this.procId = procId;
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
