package com.example.myurbanflix;

/**
 * This class is used to define a User as the database will understand it; When a user is created
 * in the CreateAccountActivity class, the User is pushed to the database as a User object;
 * The firestore will parse the object in order to generate a document which it will store
 * remotely.
 */
public class User {
    /**
     * Holds the User's email address
     */
    private String email;
    /**
     * Holds the User's username
     */
    private String username;
    /**
     * Holds the User's password
     */
    private String password;

    /**
     * This constructor takes the given email, un, and pw and populates the object's local fields
     * @param email
     * @param un
     * @param pw
     */
    public User(String email, String un, String pw) {
        this.email = email;
        this.username = un;
        this.password = pw;
    }

    /**
     * Used by the database to get the object's private fields
     * @return a string containing user's email address
     */
    public String getEmail() {
        return this.email;
    }
    /**
     * Used by the database to get the object's private fields
     * @return a string containing user's username
     */
    public String getUsername() {
        return this.username;
    }
    /**
     * Used by the database to get the object's private fields
     * @return a string containing user's password
     */
    public String getPassword() {
        return this.password;
    }
}
