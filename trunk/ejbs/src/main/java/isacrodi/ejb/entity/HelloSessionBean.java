package isacrodi.ejb.entity;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


@Stateless
@Remote(HelloSession.class)
public class HelloSessionBean implements HelloSession, java.io.Serializable
{
  @PersistenceContext
  private EntityManager entityManager;
  private HelloEntity helloEntity;
  private static final long serialVersionUID = 1;


  public void doHello(String hello)
  {
    this.helloEntity = new HelloEntity(hello);
    this.entityManager.persist(this.helloEntity);
  }


  public String getHello()
  {
    String hello = "hello from the null ref";
    if (this.helloEntity != null)
    {
      hello = this.helloEntity.getHello();
    }
    return (hello);
  }
}
