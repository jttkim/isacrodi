package org.isacrodi.ejb.session;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.Remote;
import javax.persistence.PersistenceContext;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.isacrodi.ejb.entity.*;

import static org.javamisc.Util.genericTypecast;


@Stateless
@Remote({UserHandler.class})
public class UserHandlerBean implements UserHandler
{
  @PersistenceContext
  private EntityManager entityManager;


  public UserHandlerBean()
  {
    super();
  }


  public IsacrodiUser findUser(String username)
  {
    if (username == null)
    {
      throw new IllegalArgumentException("username must not be null");
    }
    Query query = this.entityManager.createQuery("SELECT u FROM IsacrodiUser u WHERE username = :username");
    query.setParameter("username", username);
    List<IsacrodiUser> userList = genericTypecast(query.getResultList());
    if (userList.size() == 1)
    {
      return(userList.get(0));
    }
    else
    {
      // relying on unique constraint on username
      return (null);
    }
  }


  public void insertUser(IsacrodiUser isacrodiUser)
  {
    // FIXME: should check whether user already exists
    this.entityManager.persist(isacrodiUser);
  }


  public IsacrodiUser authenticate(String username, String password)
  {
    IsacrodiUser user = this.findUser(username);
    if ((user != null) && user.checkPassword(password))
    {
      return (user);
    }
    else
    {
      return (null);
    }
  }
}
