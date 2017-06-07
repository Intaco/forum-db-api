package com.lonelyprogrammer.forum.auth.controllers;

import com.lonelyprogrammer.forum.auth.services.PostsService;
import com.lonelyprogrammer.forum.auth.services.ThreadsService;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by nikita on 27.05.17.
 */
@RestController
@RequestMapping(value = "api/threads")
@CrossOrigin // for localhost usage
public class PostsController {
    @NotNull
    private final PostsService postsService;







    public PostsController(@NotNull PostsService postsService) {
        this.postsService = postsService;
    }
}
