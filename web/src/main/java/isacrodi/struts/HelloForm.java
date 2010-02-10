package isacrodi.struts;

import org.apache.struts.action.ActionForm;

public class HelloForm extends ActionForm 
{
	private String name;

	public String getName() 
	{
		return this.name;
	}

	public void setName(String name) 
	{
		this.name = name;
	}
}

