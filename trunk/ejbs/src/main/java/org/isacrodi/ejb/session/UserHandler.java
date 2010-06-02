package org.isacrodi.ejb.session;

import org.isacrodi.ejb.entity.*;


public interface UserHandler
{
  /**
   * Insert a user into persistent storage.
   */
  void insertUser(IsacrodiUser isacrodiUser);

  /**
   * Find a user by username.
   *
   * @param username the username
   * @return the user, or {@code null} if no user with that username exists
   */
  IsacrodiUser findUser(String username);

  /**
   * Authenticate a user.
   *
   * @param username the username
   * @param password the password in clear text
   * @return the authenticated user if authentication was successful, {@code null} otherwise
   */
  IsacrodiUser authenticate(String username, String password);
}
