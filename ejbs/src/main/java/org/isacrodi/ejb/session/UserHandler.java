package org.isacrodi.ejb.session;

import org.isacrodi.ejb.entity.*;


public interface UserHandler
{
  /**
   * Insert a user into persistent storage.
   */
  void insertUser(IsacrodiUser isacrodiUser);

  /**
   * Authenticate a user.
   *
   * @param username the username
   * @param password the password in clear text
   * @return the authenticated user if authentication was successful, {@code null} otherwise
   */
  IsacrodiUser authenticate(String username, String password);
}
