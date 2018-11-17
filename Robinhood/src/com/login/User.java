package com.login;

import jdk.nashorn.internal.ir.annotations.Immutable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by billupus on 11/11/18.
 */
public class User {
    private String username;
    private String password;

    public User(String username) {
        this.username = username;
        this.password = PasswordReader.readPasswordFromFile(username);
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
