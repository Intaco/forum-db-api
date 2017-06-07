package com.lonelyprogrammer.forum.auth.models.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by nikita on 27.05.17.
 */
public class PostEntity {
    @JsonProperty
    private int id;
    @JsonProperty
    private String created;
    @JsonProperty
    private int parent;
    @JsonProperty
    private String message;
    @JsonProperty
    private String author;
    @JsonProperty
    private String forum;
    @JsonProperty
    private boolean isEdited;
    @JsonProperty
    private int thread;

    @JsonCreator
    public PostEntity(@JsonProperty("id") int id,@JsonProperty("created") String created, @JsonProperty("parent") int parent,
                      @JsonProperty("message") String message, @JsonProperty("author") String author, @JsonProperty("forum") String forum,
                @JsonProperty("isEdited") boolean isEdited, @JsonProperty("thread") int thread ){
        this.id = id;
        this.created = created;
        this.parent = parent;
        this.message = message;
        this.author = author;
        this.forum = forum;
        this.isEdited = isEdited;
        this.thread = thread;
    }

    public String getCreated() {
        return created;
    }

    public String getForum() {
        return forum;
    }

    public int getParent() {
        return parent;
    }

    public int getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public String getAuthor() {
        return author;
    }

    public boolean isEdited() {
        return isEdited;
    }

    public int getThread() {
        return thread;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public void setParent(int parent) {
        this.parent = parent;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setForum(String forum) {
        this.forum = forum;
    }

    public void setEdited(boolean edited) {
        isEdited = edited;
    }

    public void setThread(int thread) {
        this.thread = thread;
    }
}
