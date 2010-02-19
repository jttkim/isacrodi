package org.isacrodi.ejb.session;

import javax.ejb.Stateless;
import javax.ejb.Local;


@Stateless
public class ImageDescriptorBean implements ImageStack
{
  
  @PersistenceContext
  EntityManager em;

  public doDummy()
  {

    ImageType t = new ImageType();
    t.setTypeName("Flower");

    ImageDescriptor d = new ImageDescriptor();
    d.setImageType(t);
    em.persist(d);

  }

  public List getDescriptors()
  {
    Query q = 
  }


}
