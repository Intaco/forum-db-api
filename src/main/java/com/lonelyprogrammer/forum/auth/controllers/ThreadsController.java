package com.lonelyprogrammer.forum.auth.controllers;

import com.lonelyprogrammer.forum.auth.models.entities.ForumThreadEntity;
import com.lonelyprogrammer.forum.auth.models.entities.PostEntity;
import com.lonelyprogrammer.forum.auth.models.entities.UserEntity;
import com.lonelyprogrammer.forum.auth.services.PostsService;
import com.lonelyprogrammer.forum.auth.services.ThreadsService;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.CREATED;

/**
 * Created by nikita on 20.03.17.
 */

@RestController
@RequestMapping(value = "/api/thread")
@CrossOrigin // for localhost usage
public class ThreadsController {
    @NotNull
    private final ThreadsService threadsService;
    @NotNull
    private final PostsService postsService;


    @RequestMapping(path = "/{slugOrId}/create", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public ResponseEntity createPost(@PathVariable(name = "slugOrId") String slugOrId, @RequestBody List<PostEntity> posts) {
        ForumThreadEntity loaded;
        if (isNumeric(slugOrId)){
            final Integer id = Integer.parseInt(slugOrId);
            loaded = threadsService.get(id);
        } else loaded = threadsService.get(slugOrId); //as slug
        if (loaded == null){
            return ResponseEntity.notFound().build();
        }
        final List<Integer> children = postsService.getThreadChildrenIds(loaded);
        for (PostEntity post: posts){
            final int parentId = post.getParent();
            if (parentId !=0 && !children.contains(parentId)) return ResponseEntity.status(CONFLICT).build();
        }
        postsService.createPosts(posts, loaded);

        return ResponseEntity.status(CREATED).body(posts);
    }




    private boolean isNumeric(String slugOrId){
        return slugOrId.matches("\\d+");
    }

    public ThreadsController(@NotNull ThreadsService threadsService, @NotNull PostsService postsService) {
        this.threadsService = threadsService;
        this.postsService = postsService;
    }
}
