package isacrodi.ejb.entity;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.*;

@Entity
@DiscriminatorValue("ImageDescriptor")
public class ImageDescriptor extends Descriptor
{

  public ImageDescriptor()
	{

		super();
	}
}
