package com.lonelyprogrammer.forum.auth.models.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by nikita on 09.06.17.
 */
public class PostDetailsEntity {
    @JsonProperty
    private PostEntity post;
    @JsonProperty
    private UserEntity author;
    @JsonProperty
    private ForumThreadEntity thread;
    @JsonProperty
    private ForumEntity forum;

    @JsonCreator
    public PostDetailsEntity(@JsonProperty("post") PostEntity post,
                    @JsonProperty("author") UserEntity author,
                    @JsonProperty("thread") ForumThreadEntity thread,
                    @JsonProperty("forum") ForumEntity forum){
        this.post = post;
        this.author = author;
        this.thread = thread;
        this.forum = forum;
    }

    public PostDetailsEntity(){}

    public PostEntity getPost() {
        return post;
    }

    public UserEntity getAuthor() {
        return author;
    }

    public ForumThreadEntity getThread() {
        return thread;
    }

    public ForumEntity getForum() {
        return forum;
    }

    public void setPost(PostEntity post) {
        this.post = post;
    }

    public void setAuthor(UserEntity author) {
        this.author = author;
    }

    public void setThread(ForumThreadEntity thread) {
        this.thread = thread;
    }

    public void setForum(ForumEntity forum) {
        this.forum = forum;
    }
}
