package com.lonelyprogrammer.forum.auth.models.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ForumEntity {
    protected final String title;
    protected final String user;
    @JsonIgnore
    protected final Integer postsCount;
    @JsonIgnore
    protected final Integer threadsCount;
    protected final String slug;

    @JsonCreator
    ForumEntity(@JsonProperty("title") String title, @JsonProperty("user") String user, @JsonProperty("posts") Integer postsCount
            ,@JsonProperty("threads") Integer threadsCount,@JsonProperty("slug") String slug) {
        this.title = title;
        this.user = user;
        this.postsCount = postsCount;
        this.threadsCount = threadsCount;
        this.slug = slug;

    }

    public String getTitle() {
        return title;
    }

    public String getUser() {
        return user;
    }

    public Integer getPostsCount() {
        return postsCount;
    }

    public Integer getThreadsCount() {
        return threadsCount;
    }

    public String getSlug() {
        return slug;
    }

}
