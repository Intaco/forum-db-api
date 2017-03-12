package com.lonelyprogrammer.forum.auth.dao;

import com.lonelyprogrammer.forum.auth.models.User;
import com.lonelyprogrammer.forum.auth.models.entities.ForumEntity;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Nikita on 12.03.2017.
 */
@Service
public class ForumDAO {
    private List<ForumEntity> forums = new ArrayList<>();

    public void add(ForumEntity forumEntity){
        forums.add(forumEntity);
    }
    @Nullable
    public ForumEntity load(String slug){
        for ( ForumEntity entity: forums){
            if (entity.getSlug().equals(slug)) {
                return entity;
            }
        }
        return null;
    }
}
