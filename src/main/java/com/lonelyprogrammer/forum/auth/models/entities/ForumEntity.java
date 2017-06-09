package com.lonelyprogrammer.forum.auth.models.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ForumEntity {
    protected final String title;
    protected String user;
    protected final Integer posts;
    protected final Integer threads;
    protected final String slug;

    @JsonCreator
    public ForumEntity(@JsonProperty("title") String title, @JsonProperty("user") String user, @JsonProperty("posts") Integer posts
            ,@JsonProperty("threads") Integer threads ,@JsonProperty("slug") String slug) {
        this.title = title;
        this.user = user;
        this.posts = posts;
        this.threads = threads;
        this.slug = slug;

    }

    public String getTitle() {
        return title;
    }

    public String getUser() {
        return user;
    }

    public Integer getPosts() {
        return posts;
    }

    public Integer getThreads() {
        return threads;
    }

    public String getSlug() {
        return slug;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
