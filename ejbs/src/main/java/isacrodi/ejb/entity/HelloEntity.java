package isacrodi.ejb.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;


@Entity
public class HelloEntity
{
  private int id;
  private String hello;


  public HelloEntity(String hello)
  {
    this.hello = hello;
  }


  public HelloEntity()
  {
    this("default hello");
  }


  @Id @GeneratedValue
  public int getId()
  {
    return id;
  }


  public void setId(int id)
  {
    this.id = id;
  }


  public String getHello()
  {
    return (this.hello);
  }


  public void setHello(String hello)
  {
    this.hello = hello;
  }
}
