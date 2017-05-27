package com.lonelyprogrammer.forum.auth.services;

import com.lonelyprogrammer.forum.auth.dao.ForumDAO;
import com.lonelyprogrammer.forum.auth.dao.ThreadDAO;
import com.lonelyprogrammer.forum.auth.dao.UserDAO;
import com.lonelyprogrammer.forum.auth.models.entities.ForumEntity;
import com.lonelyprogrammer.forum.auth.models.entities.ForumThreadEntity;
import com.lonelyprogrammer.forum.auth.models.entities.UserEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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

    public ForumThreadEntity createThread(ForumThreadEntity data) {
        ForumThreadEntity added = threadDAO.add(data);

        return added;
    }

    @Nullable
    public ForumThreadEntity loadForumThread(String slug) {
        return threadDAO.getBySlug(slug);

    }

    @NotNull
    public List<ForumThreadEntity> loadThreadsByForum(String forumSlug, Integer limit, String since, boolean desc) {
        Timestamp sinceTime = null;
        if (since != null) {
            final String formatted = ZonedDateTime.parse(since).format(DateTimeFormatter.ISO_INSTANT);
            sinceTime = new Timestamp(ZonedDateTime.parse(formatted).toLocalDateTime().toInstant(ZoneOffset.UTC).toEpochMilli());
        }
        return threadDAO.getByForum(forumSlug, limit, sinceTime, desc);
    }

    @SuppressWarnings("OverlyComplexBooleanExpression")
    public boolean forumsDifferExceptSlug(ForumEntity forum, ForumEntity data) {
        return (!forum.getTitle().equals(data.getTitle())
                || !forum.getPostsCount().equals(data.getPostsCount()) || !forum.getThreadsCount().equals(data.getThreadsCount())
                || !forum.getUser().equals(data.getUser()));
    }
}
