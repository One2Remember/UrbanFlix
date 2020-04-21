package com.example.myurbanflix;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test Suite for testing User class methods
 */
public class UserTest {
    /**
     * Dummy user for testing getters
     */
    User testUser;

    /**
     * Before method instantiates a new user to test
     */
    @Before
    public void initUser() {
        testUser = new User("oh_bother@att.net", "UserGoGo123", "password");
    }
    /**
     * Asserts that the email getter returns proper value
     */
    @Test
    public void getEmail() {
        assertEquals(testUser.getEmail(), "oh_bother@att.net");
    }
    /**
     * Asserts that the username getter returns proper value
     */
    @Test
    public void getUsername() {
        assertEquals(testUser.getUsername(), "UserGoGo123");
    }
    /**
     * Asserts that the password getter returns proper value
     */
    @Test
    public void getPassword() {
        assertEquals(testUser.getPassword(), "password");
    }
}