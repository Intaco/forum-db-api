package com.lonelyprogrammer.forum.auth.services;

import com.lonelyprogrammer.forum.auth.dao.ForumDAO;
import com.lonelyprogrammer.forum.auth.dao.ThreadDAO;
import com.lonelyprogrammer.forum.auth.dao.UserDAO;
import com.lonelyprogrammer.forum.auth.models.entities.ForumEntity;
import org.springframework.stereotype.Service;

/**
 * Created by Nikita on 12.03.2017.
 */

@Service
public class ForumsService {
    private ForumDAO forumDAO;
    private UserDAO userDAO;
    private ThreadDAO threadDAO;

/*
    public ForumsService(@NotNull ForumDAO forumDAO, @NotNull UserDAO userDAO, @NotNull ThreadDAO threadDAO) {
        this.forumDAO = forumDAO;
        this.userDAO = userDAO;
        this.threadDAO = threadDAO;
    }

    public Either<ForumEntity, ErrorResponse> createForum(ForumEntity data) {
        final ForumEntity loaded = forumDAO.load(data.getSlug());
        if (userDAO.load(data.getUser()) == null) {
            return Either.right(new ErrorResponse("Владелец форума не найден", ExternalError.NOT_FOUND));
        } else if (loaded != null) {
            return Either.left(loaded);
        }
        //noinspection SuspiciousIndentAfterControlStatement
        forumDAO.add(data);
        return Either.left(data);
    }

    public Either<ForumThreadEntity, ErrorResponse> createForumThread(ForumThreadEntity data, String forumSlug) {
        final ForumThreadEntity loaded = threadDAO.load(data.getSlug());
        if (forumDAO.load(forumSlug) == null){
            return Either.right(new ErrorResponse("Форум не найден", ExternalError.NOT_FOUND));
        } else if (userDAO.load(data.getAuthor()) == null) {
            return Either.right(new ErrorResponse("Владелец форума не найден", ExternalError.NOT_FOUND));
        } else if (loaded != null) {
            return Either.left(loaded);
        }
        //noinspection SuspiciousIndentAfterControlStatement
        threadDAO.add(data, forumSlug);
        return Either.left(data);
    }
*/

    @SuppressWarnings("OverlyComplexBooleanExpression")
    public boolean forumsDifferExceptSlug(ForumEntity forum, ForumEntity data) {
        return (!forum.getTitle().equals(data.getTitle())
                || !forum.getPostsCount().equals(data.getPostsCount()) || !forum.getThreadsCount().equals(data.getThreadsCount())
                || !forum.getUser().equals(data.getUser()));
    }
}
