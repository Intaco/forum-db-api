package com.lonelyprogrammer.forum.auth.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lonelyprogrammer.forum.auth.utils.AuthorizationUtils;

public final class User {
    //TODO password String ->>> Hashed
    @JsonIgnore
    private final long userId;
    private String login;
    @JsonIgnore
    private String password;
    private String email;

    public User(String login, String password, String email) {
        userId = AuthorizationUtils.generateUID();
        this.login = login;
        this.password = password;
        this.email = email;
    }

    public long getUserId() {
        return userId;
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

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
