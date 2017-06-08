package com.lonelyprogrammer.forum.auth.services;

import com.lonelyprogrammer.forum.auth.dao.PostDAO;
import com.lonelyprogrammer.forum.auth.models.entities.ForumThreadEntity;
import com.lonelyprogrammer.forum.auth.models.entities.PostEntity;
import com.lonelyprogrammer.forum.auth.models.entities.PostsAnswerEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.lonelyprogrammer.forum.auth.controllers.ThreadsController.isNumeric;

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

    @Nullable
    public PostsAnswerEntity getPostsForThread(Integer id, Integer limit, Integer marker, String sort, boolean desc){
        Integer offset = marker == null ? 0 : marker;
        final List<PostEntity> loaded;
        switch (sort) {
            case "flat":
                loaded = postDAO.getPostsFlat(id, limit, offset, desc);
                break;
            case "tree":
                loaded = postDAO.getPostsTree(id, limit, offset, desc);
                break;
            case "parent_tree":
                loaded = postDAO.getPostsParentTree(id, desc,offset, postDAO.getParentIds(id,limit,desc));
                break;
            default:
                return null;
        }
        offset+= loaded.size();
        return new PostsAnswerEntity(offset.toString(), loaded);
    }
}
