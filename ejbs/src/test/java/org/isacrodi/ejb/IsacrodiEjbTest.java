package org.isacrodi.ejb;

import org.junit.*;
import org.isacrodi.ejb.entity.*;
import org.isacrodi.ejb.session.*;
import org.isacrodi.ejb.io.*;


/**
 * Basic tests of Isacrodi entities.
 *
 * These tests do not require a database, entity instances are created
 * as needed.
 *
 * @author jtk
 */
public class IsacrodiEjbTest
{
  private IsacrodiUser userFarmer;
  private String farmerPassword;


  @Before
  public void setUp()
  {
    this.farmerPassword = "topsecret";
    this.userFarmer = new IsacrodiUser("Farmer", "Joe", "joefarmer", IsacrodiUser.hash(this.farmerPassword), "joe@joesfarm.co.uk");
  }


  @Test
  public void testPasswordCheck()
  {
    Assert.assertTrue(this.userFarmer.checkPassword(this.farmerPassword));
    Assert.assertFalse(this.userFarmer.checkPassword("nonsense"));
  }


  @Test
  public void testMemoryDB()
  {
    MemoryDB memoryDB = new MemoryDB();
    Access access = memoryDB;
    UserHandler userHandler = memoryDB;
    userHandler.insertUser(this.userFarmer);
  }
}
