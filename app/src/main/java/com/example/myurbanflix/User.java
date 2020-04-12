package com.example.myurbanflix;

public class User {
    private String email;
    private String username;
    private String password;

    public User(String email, String un, String pw) {
        this.email = email;
        this.username = un;
        this.password = pw;
    }

    public String getEmail() {
        return this.email;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }
}
