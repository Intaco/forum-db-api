package com.lonelyprogrammer.forum.auth.dao;

import com.lonelyprogrammer.forum.auth.models.entities.ForumThreadEntity;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nikita
 * Date: 2017-03-14
 */
@Service
public class ThreadDAO {
    private List<ForumThreadEntity> forums = new ArrayList<>();

    public void add(ForumThreadEntity forumEntity, String slug){
        forums.add(forumEntity);
    }
    @Nullable
    public ForumThreadEntity load(String slug){
        for ( ForumThreadEntity entity: forums){
            if (entity.getSlug().equals(slug)) {
                return entity;
            }
        }
        return null;
    }
}
