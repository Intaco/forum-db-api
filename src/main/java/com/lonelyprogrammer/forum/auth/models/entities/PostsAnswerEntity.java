package com.lonelyprogrammer.forum.auth.models.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by nikita on 08.06.17.
 */
public class PostsAnswerEntity {
    @JsonProperty
    private String marker;

    @JsonProperty
    private List<PostEntity> posts;

    @JsonCreator
    public PostsAnswerEntity(@JsonProperty("marker") String marker, @JsonProperty("posts") List<PostEntity> posts){
        this.marker = marker;
        this.posts = posts;
    }

    public String getMarker() {
        return marker;
    }

    public void setMarker(String marker) {
        this.marker = marker;
    }

    public List<PostEntity> getPosts() {
        return posts;
    }

    public void setPosts(List<PostEntity> posts) {
        this.posts = posts;
    }
}
