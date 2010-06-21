package org.isacrodi.ejb.session;

import javax.ejb.Stateless;
import javax.ejb.Remote;

import org.javamisc.jee.entitycrud.EntityAccess;
import org.javamisc.jee.entitycrud.EntityAccessAdapter;

import org.isacrodi.ejb.entity.*;


@Stateless
@Remote(EntityAccess.class)
public class EntityAccessBean extends EntityAccessAdapter implements EntityAccess
{
  @Override
  public boolean isEntityClass(Class<?> aClass)
  {
    return (IsacrodiEntity.class.isAssignableFrom(aClass));
  }
}
