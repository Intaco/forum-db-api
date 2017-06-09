package com.lonelyprogrammer.forum.auth.services;

import com.lonelyprogrammer.forum.auth.dao.ForumDAO;
import com.lonelyprogrammer.forum.auth.dao.ThreadDAO;
import com.lonelyprogrammer.forum.auth.dao.UserDAO;
import com.lonelyprogrammer.forum.auth.models.entities.ForumEntity;
import com.lonelyprogrammer.forum.auth.models.entities.ForumThreadEntity;
import com.lonelyprogrammer.forum.auth.models.entities.UserEntity;
import com.lonelyprogrammer.forum.auth.utils.TimeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Created by Nikita on 12.03.2017.
 */

@Service
public class ForumsService {
    private ForumDAO forumDAO;
    private UserDAO userDAO;
    private ThreadDAO threadDAO;


    public ForumsService(@NotNull ForumDAO forumDAO, @NotNull UserDAO userDAO, @NotNull ThreadDAO threadDAO) {
        this.forumDAO = forumDAO;
        this.userDAO = userDAO;
        this.threadDAO = threadDAO;
    }

    @Nullable
    public ForumEntity getBySlug(String slug) {
        return forumDAO.getBySlug(slug);
    }

    public HttpStatus createForum(ForumEntity data) {
        if (forumDAO.getBySlug(data.getSlug()) != null) {
            return HttpStatus.CONFLICT;
        }
        final UserEntity loadedUser = userDAO.getByNickname(data.getUser());
        if (loadedUser == null) {
            return HttpStatus.NOT_FOUND;
        }
        try {
            data.setUser(loadedUser.getNickname());
            forumDAO.add(data);
        } catch (DuplicateKeyException e) {
            return HttpStatus.CONFLICT;
        }
        return HttpStatus.CREATED;
    }
    @Nullable
    public ForumThreadEntity createThread(ForumThreadEntity data) {
        return threadDAO.add(data);
    }

    @Nullable
    public ForumThreadEntity loadForumThread(String slug) {
        return threadDAO.getBySlug(slug);

    }

    @NotNull
    public List<ForumThreadEntity> loadThreadsByForum(String forumSlug, Integer limit, String since, boolean desc) {
        return threadDAO.getByForum(forumSlug, limit, TimeUtil.timestampFromString(since), desc);
    }

    @SuppressWarnings("OverlyComplexBooleanExpression")
    public boolean forumsDifferExceptSlug(ForumEntity forum, ForumEntity data) {
        return (!forum.getTitle().equals(data.getTitle())
                || !forum.getPosts().equals(data.getPosts()) || !forum.getThreads().equals(data.getThreads())
                || !forum.getUser().equals(data.getUser()));
    }
}
