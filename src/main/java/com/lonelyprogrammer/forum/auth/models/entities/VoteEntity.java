package com.lonelyprogrammer.forum.auth.models.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by nikita on 08.06.17.
 */
public class VoteEntity {
    @JsonProperty
    private String nickname;
    @JsonProperty
    private int voice;

    @JsonCreator
    public VoteEntity(@JsonProperty("nickname") String nickname, @JsonProperty("voice") int voice){
        this.nickname = nickname;
        this.voice = voice;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getVoice() {
        return voice;
    }

    public void setVoice(int voice) {
        this.voice = voice;
    }
}
