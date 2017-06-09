package com.lonelyprogrammer.forum.auth.controllers;

import com.lonelyprogrammer.forum.auth.models.entities.PostDetailsEntity;
import com.lonelyprogrammer.forum.auth.services.PostsService;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * Created by nikita on 27.05.17.
 */
@RestController
@RequestMapping(value = "api/post")
@CrossOrigin // for localhost usage
public class PostsController {
    @NotNull
    private final PostsService postsService;

    @GetMapping(path = "/{id}/details")
    public ResponseEntity getIdDetails(@PathVariable(name = "id") Integer id,
                                       @RequestParam(name = "related", required = false) Set<String> related) {
        final PostDetailsEntity loaded = postsService.getPostDetails(id, related);
        if (loaded == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(loaded);
    }


    public PostsController(@NotNull PostsService postsService) {
        this.postsService = postsService;
    }
}
