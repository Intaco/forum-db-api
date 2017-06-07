package com.lonelyprogrammer.forum.auth.services;

import com.lonelyprogrammer.forum.auth.dao.PostDAO;
import com.lonelyprogrammer.forum.auth.models.entities.ForumThreadEntity;
import com.lonelyprogrammer.forum.auth.models.entities.PostEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by nikita on 27.05.17.
 */
@Service
public class PostsService {
    private PostDAO postDAO;

    public PostsService(@NotNull PostDAO postDAO) {
        this.postDAO = postDAO;
    }

    @NotNull
    public List<Integer> getThreadChildrenIds(ForumThreadEntity threadEntity){
        return postDAO.getThreadChildren(threadEntity.getId());
    }
    public void createPosts(List<PostEntity> posts, ForumThreadEntity thread){
        postDAO.createPosts(posts, thread);
    }
}
