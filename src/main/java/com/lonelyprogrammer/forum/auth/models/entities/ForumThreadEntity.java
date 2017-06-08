package com.lonelyprogrammer.forum.auth.models.entities;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;

public class ForumThreadEntity {
    protected final String author;
    protected final String created;
    protected String forum;
    protected Integer id;
    protected final String message;
    protected String slug;
    protected final String title;
    protected final Integer votes;
    @JsonCreator
    public ForumThreadEntity(@JsonProperty("author") String author, @JsonProperty("created") String created,
                             @JsonProperty("forum") String forum, @JsonProperty("id") Integer id, @JsonProperty("message") String message,
                             @JsonProperty("slug") String slug, @JsonProperty("title") String title,
                             @JsonProperty("votes") Integer votes) {
        this.author = author;
        this.created = created;
        this.forum = forum;
        this.id = id;
        this.message = message;
        this.slug = slug;
        this.title = title;
        this.votes = votes;
    }

    public String getAuthor() {
        return author;
    }

    public String getCreated() {
        return created;
    }

    public String getForum() {
        return forum;
    }

    public Integer getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public String getSlug() {
        return slug;
    }

    public String getTitle() {
        return title;
    }

    public Integer getVotes() {
        return votes;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public void setForum(String forum) {this.forum = forum;
    }


}
