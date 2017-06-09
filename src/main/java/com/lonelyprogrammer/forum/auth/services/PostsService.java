package com.lonelyprogrammer.forum.auth.services;

import com.lonelyprogrammer.forum.auth.dao.ForumDAO;
import com.lonelyprogrammer.forum.auth.dao.PostDAO;
import com.lonelyprogrammer.forum.auth.dao.ThreadDAO;
import com.lonelyprogrammer.forum.auth.dao.UserDAO;
import com.lonelyprogrammer.forum.auth.models.entities.ForumThreadEntity;
import com.lonelyprogrammer.forum.auth.models.entities.PostDetailsEntity;
import com.lonelyprogrammer.forum.auth.models.entities.PostEntity;
import com.lonelyprogrammer.forum.auth.models.entities.PostsAnswerEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Created by nikita on 27.05.17.
 */
@Service
public class PostsService {
    private PostDAO postDAO;
    private UserDAO userDAO;
    private ForumDAO forumDAO;
    private ThreadDAO threadDAO;

    public PostsService(@NotNull PostDAO postDAO, @NotNull UserDAO userDAO, @NotNull ForumDAO forumDAO, @NotNull ThreadDAO threadDAO) {
        this.postDAO = postDAO;
        this.userDAO = userDAO;
        this.forumDAO = forumDAO;
        this.threadDAO = threadDAO;

    }

    @NotNull
    public List<Integer> getThreadChildrenIds(ForumThreadEntity threadEntity) {
        return postDAO.getThreadChildren(threadEntity.getId());
    }

    public void createPosts(List<PostEntity> posts, ForumThreadEntity thread) {
        postDAO.createPosts(posts, thread);
    }

    @Nullable
    public PostsAnswerEntity getPostsForThread(Integer id, Integer limit, Integer marker, String sort, boolean desc) {
        Integer offset = marker == null ? 0 : marker;
        final List<PostEntity> loaded;
        switch (sort) {
            case "flat":
                loaded = postDAO.getPostsFlat(id, limit, offset, desc);
                offset += loaded.size();
                break;
            case "tree":
                loaded = postDAO.getPostsTree(id, limit, offset, desc);
                offset += loaded.size();
                break;
            case "parent_tree":
                final List<Integer> parents = postDAO.getParentIds(id, limit, desc, offset);
                loaded = postDAO.getPostsParentTree(id, desc, parents);
                offset += parents.size();
                break;
            default:
                return null;
        }

        return new PostsAnswerEntity(offset.toString(), loaded);
    }

    @SuppressWarnings("OverlyComplexMethod")
    @Nullable
    public PostDetailsEntity getPostDetails(Integer id, Set<String> related) {
        final PostEntity loadedPost = postDAO.getById(id);
        if (loadedPost == null) return null;
        final PostDetailsEntity details = new PostDetailsEntity();
        if (related != null) {
            for (String s : related) {
                switch (s) {
                    case "user":
                        details.setAuthor(userDAO.getByNickname(loadedPost.getAuthor()));
                        if (details.getAuthor() == null) return null;
                        break;
                    case "forum":
                        details.setForum(forumDAO.getBySlug(loadedPost.getForum()));
                        if (details.getForum() == null) return null;
                        break;
                    case "thread":
                        details.setThread(threadDAO.getById(loadedPost.getThread()));
                        if (details.getThread() == null) return null;
                        break;
                    default:
                        return null;
                }
            }
        }
        details.setPost(loadedPost);
        return details;

    }
}
