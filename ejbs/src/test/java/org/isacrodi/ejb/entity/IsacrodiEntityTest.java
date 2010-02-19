package org.isacrodi.ejb.entity;

import org.junit.*;
import org.isacrodi.ejb.entity.*;


/**
 * Basic tests of Isacrodi entities.
 *
 * These tests do not require a database, entity instances are created
 * as needed.
 *
 * @author jtk
 */
public class IsacrodiEntityTest
{
  private IsacrodiUser isacrodiUser;
  private String isacrodiUserPassword;


  @Before
  public void setUp()
  {
    this.isacrodiUserPassword = "topsecret";
    this.isacrodiUser = new IsacrodiUser("Farmer", "Joe", "joefarmer", IsacrodiUser.hash(this.isacrodiUserPassword), "joe@joesfarm.co.uk");
  }


  @Test
  public void testPasswordCheck()
  {
    Assert.assertTrue(this.isacrodiUser.checkPassword(this.isacrodiUserPassword));
    Assert.assertFalse(this.isacrodiUser.checkPassword("nonsense"));
  }
}
