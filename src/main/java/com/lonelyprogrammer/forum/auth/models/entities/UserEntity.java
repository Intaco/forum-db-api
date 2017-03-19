package com.lonelyprogrammer.forum.auth.models.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserEntity {
    @JsonProperty
    protected String nickname;
    @JsonProperty
    protected String fullName;
    @JsonProperty
    protected String about;
    @JsonProperty
    protected String email;
    @JsonCreator
    public UserEntity(@JsonProperty("nickname") String nickname, @JsonProperty("about") String about,
               @JsonProperty("email") String email, @JsonProperty("fullname") String fullName) {
        this.nickname = nickname;
        this.about = about;
        this.email = email;
        this.fullName = fullName;
    }

    public String getNickname() {
        return nickname;
    }

    public String getAbout() {
        return about;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}