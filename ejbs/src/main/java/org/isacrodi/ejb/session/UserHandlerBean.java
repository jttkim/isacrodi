package org.isacrodi.ejb.session;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.Remote;
import javax.persistence.PersistenceContext;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.isacrodi.ejb.entity.*;


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


  public void insertUser(IsacrodiUser isacrodiUser)
  {
    // FIXME: should check whether user already exists
    this.entityManager.persist(isacrodiUser);
  }


  public IsacrodiUser authenticate(String username, String password)
  {
    Query query = this.entityManager.createQuery("SELECT u FROM IsacrodiUser u WHERE username = :username");
    query.setParameter("username", username);
    List<IsacrodiUser> userList = Util.genericTypecast(query.getResultList());
    if (userList.size() == 1)
    {
      IsacrodiUser user = userList.get(0);
      if (user.checkPassword(password))
      {
	return (user);
      }
    }
    return (null);
  }
}
