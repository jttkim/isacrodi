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
    Crop corn = new Crop("corn", "Zea mays");
    Crop eggplant = new Crop("eggplant", "Solanum melongena");
    MemoryDB memoryDB = new MemoryDB();
    Access access = memoryDB;
    UserHandler userHandler = memoryDB;
    userHandler.insertUser(this.userFarmer);
    access.insert(corn);
    access.insert(eggplant);
    Assert.assertEquals(access.findCropList().size(), 2);
    CropDisorder whitefly = new CropDisorder("whitefly", "Bemisia tabaci");
    String[] whiteflyHosts = {"Zea mays", "Solanum melongena"};
    access.insert(whitefly, whiteflyHosts);
    Assert.assertEquals(whitefly.getCropSet().size(), 2);
    String[] soilValues = {"silty", "loamy", "sandy", "peaty", "clay"};
    CategoricalType soilType = new CategoricalType("soil");
    access.insert(soilType, soilValues);
    // FIXME: continue adding tests so all entities are tested...
  }
}
