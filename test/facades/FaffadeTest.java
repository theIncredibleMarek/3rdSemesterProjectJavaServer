/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facades;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import exceptions.NotFoundException;
import java.security.NoSuchAlgorithmException;
import model.Users;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import security.SecurePassword;

/**
 *
 * @author Marek FURAK
 */
public class FaffadeTest
{
    
    public Faffade faffade;
    public Gson gson;
    public Gson gsonExpose;
    Users testUser;
    
    public FaffadeTest()
    {
    }
    
    @BeforeClass
    public static void setUpClass()
    {
        
    }
    
    @AfterClass
    public static void tearDownClass()
    {
    }
    
    @Before
    public void setUp() throws NoSuchAlgorithmException
    {
        faffade = new Faffade();
        gson = new Gson();
        gsonExpose = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        testUser = new Users();
        testUser.setUsername("test");
        testUser.setPassword("test");
        testUser.setType("user");
        String userAsJson = gson.toJson(testUser);
        faffade.addUserFromGson(userAsJson);
    }
    
    @After
    public void tearDown() throws NotFoundException
    {
        faffade.delete(testUser.getUsername());
    }

    /**
     * Test of loginAsJSON method, of class Faffade.
     */
    @Test
    public void testLoginAsJSON() throws Exception
    {
        System.out.println("loginAsJSON");
        String username = "test";
        String password = "test";
        String expected = gsonExpose.toJson(testUser.getType());
        String actual = faffade.loginAsJSON(username, password);
        assertEquals(expected, actual);
    }

    /**
     * Test of addUserFromGson method, of class Faffade.
     */
    @Test
    public void testAddUserFromGson()
    {
        System.out.println("addUserFromGson");
        Users testUser2 = new Users();
        testUser2.setUsername("test2");
        testUser2.setPassword("test");
        testUser2.setType("user");
        String json = gson.toJson(testUser2);
        Users expected = testUser2;
        Users result = faffade.addUserFromGson(json);
        assertEquals(expected, result);
        
    }

    /**
     * Test of editUserFromGson method, of class Faffade.
     */
    @Test
    public void testEditUserFromGson() throws Exception
    {
        System.out.println("editUserFromGson");
        testUser.setType("admin");
        testUser.setPassword("test2");
        String json = gson.toJson(testUser);
        Users expResult = testUser;
        Users result = faffade.editUserFromGson(json);
        assertEquals(expResult, result);
    }

    /**
     * Test of delete method, of class Faffade.
     */
    @Test
    public void testDelete() throws Exception
    {
        System.out.println("delete");
        Users testUser2 = new Users();
        testUser2.setUsername("test2");
        testUser2.setPassword("test");
        testUser2.setType("user");
        String username = testUser2.getUsername();
        Users expResult = testUser2;
        Users result = faffade.delete(username);
        assertEquals(expResult, result);
    }
    
}
