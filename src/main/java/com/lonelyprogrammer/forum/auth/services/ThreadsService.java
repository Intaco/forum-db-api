package com.lonelyprogrammer.forum.auth.services;

import com.lonelyprogrammer.forum.auth.dao.ForumDAO;
import com.lonelyprogrammer.forum.auth.dao.ThreadDAO;
import com.lonelyprogrammer.forum.auth.dao.UserDAO;
import com.lonelyprogrammer.forum.auth.models.entities.ForumThreadEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

/**
 * Created by nikita on 27.05.17.
 */


@Service
public class ThreadsService {

    private ForumDAO forumDAO;
    private UserDAO userDAO;
    private ThreadDAO threadDAO;


    public ThreadsService(@NotNull ForumDAO forumDAO, @NotNull UserDAO userDAO, @NotNull ThreadDAO threadDAO) {
        this.forumDAO = forumDAO;
        this.userDAO = userDAO;
        this.threadDAO = threadDAO;
    }


    @Nullable
    public ForumThreadEntity get(String slug){
        return threadDAO.getBySlug(slug);
    }
    @Nullable
    public ForumThreadEntity get(Integer id){
        return threadDAO.getById(id);

    }
    public void updateThread(ForumThreadEntity newData, ForumThreadEntity oldData){
        threadDAO.updateThread(newData, oldData);
    }

}
