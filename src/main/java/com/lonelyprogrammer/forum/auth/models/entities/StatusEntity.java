package com.lonelyprogrammer.forum.auth.models.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by nikita on 10.06.17.
 */
public class StatusEntity {
    @JsonProperty
    private int user;
    @JsonProperty
    private int forum;
    @JsonProperty
    private int thread;
    @JsonProperty
    private int post;

    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }

    public int getForum() {
        return forum;
    }

    public void setForum(int forum) {
        this.forum = forum;
    }

    public int getThread() {
        return thread;
    }

    public void setThread(int thread) {
        this.thread = thread;
    }

    public int getPost() {
        return post;
    }

    public void setPost(int post) {
        this.post = post;
    }
}
