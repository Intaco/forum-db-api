package com.lonelyprogrammer.forum.auth.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

//TODO REFACTOR ME PLS
public class AuthorizationCredentials {
    protected final String login;
    protected final String password;
    protected final String email;
    @JsonCreator
    AuthorizationCredentials(@JsonProperty("login") String login, @JsonProperty("password") String password, @JsonProperty("email") String email) {
        this.login = login;
        this.password = password;
        this.email = email;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }
}