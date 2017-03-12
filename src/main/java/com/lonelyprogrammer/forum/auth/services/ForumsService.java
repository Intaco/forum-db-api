package com.lonelyprogrammer.forum.auth.services;

import com.lonelyprogrammer.forum.auth.ErrorState;
import com.lonelyprogrammer.forum.auth.dao.ForumDAO;
import com.lonelyprogrammer.forum.auth.dao.UserDAO;
import com.lonelyprogrammer.forum.auth.models.ErrorResponse;
import com.lonelyprogrammer.forum.auth.models.entities.ForumCreateBranchEntity;
import com.lonelyprogrammer.forum.auth.models.entities.ForumEntity;
import com.msiops.ground.either.Either;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nikita on 12.03.2017.
 */

@Service
public class ForumsService {
    private ForumDAO forumDAO;
    private UserDAO userDAO;
    public ForumsService(@NotNull ForumDAO forumDAO, @NotNull UserDAO userDAO) {
        this.forumDAO = forumDAO;
        this.userDAO = userDAO;
    }
    public Either<ForumEntity, ErrorResponse> createForum(ForumEntity data){
        final ForumEntity loaded = forumDAO.load(data.getSlug());
        if (loaded != null){
            return Either.left(loaded);
        } else if (userDAO.load(data.getUser()) == null){
            return Either.right(new ErrorResponse("Владелец форума не найден", ErrorState.NOT_FOUND));
        }
        //noinspection SuspiciousIndentAfterControlStatement
        forumDAO.add(data);
        return Either.left(data);
    }
    public Either<ForumEntity, ErrorResponse> createForumBranch(ForumCreateBranchEntity data, String forumSlug){
        
    }

    @SuppressWarnings("OverlyComplexBooleanExpression")
    public boolean forumsDifferExceptSlug(ForumEntity forum, ForumEntity data){
        return (!forum.getTitle().equals(data.getTitle())
                || !forum.getPostsCount().equals(data.getPostsCount()) || !forum.getThreadsCount().equals(data.getThreadsCount())
                || !forum.getUser().equals(data.getUser()));
    }
}
