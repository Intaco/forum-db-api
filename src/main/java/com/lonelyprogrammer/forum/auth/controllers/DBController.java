package com.lonelyprogrammer.forum.auth.controllers;

import com.lonelyprogrammer.forum.auth.dao.*;
import com.lonelyprogrammer.forum.auth.models.entities.StatusEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by nikita on 14.03.17.
 */
@RestController
@RequestMapping(value = "api/service")
@CrossOrigin // for localhost usage

public class DBController {

    private final DBDAO DBDAO;
    private final UserDAO userDAO;
    private final ForumDAO forumDAO;
    private final ThreadDAO threadDAO;
    private final PostDAO postDAO;

    public DBController(DBDAO DBDAO, UserDAO userDAO, ForumDAO forumDAO, ThreadDAO threadDAO, PostDAO postDAO) {
        this.DBDAO = DBDAO;
        this.userDAO = userDAO;
        this.forumDAO = forumDAO;
        this.threadDAO = threadDAO;
        this.postDAO = postDAO;
    }

    @RequestMapping(path = "/clear", method = RequestMethod.POST)
    public ResponseEntity clear() {
        DBDAO.reset();
        return ResponseEntity.ok("DB cleared");
    }

    @RequestMapping(path = "/status", method = RequestMethod.GET)
    public ResponseEntity status() {
        final StatusEntity status = new StatusEntity();
        status.setForum(forumDAO.getCount());
        status.setPost(postDAO.getCount());
        status.setThread(threadDAO.getCount());
        status.setUser(userDAO.getCount());
        return ResponseEntity.ok(status);
    }
}
