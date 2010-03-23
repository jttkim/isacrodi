package org.isacrodi.ejb.session;

import javax.ejb.Stateless;
import javax.ejb.Remote;
import javax.persistence.PersistenceContext;
import javax.persistence.EntityManager;

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
}
