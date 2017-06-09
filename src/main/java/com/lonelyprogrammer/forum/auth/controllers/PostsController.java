package com.lonelyprogrammer.forum.auth.controllers;

import com.lonelyprogrammer.forum.auth.models.entities.PostDetailsEntity;
import com.lonelyprogrammer.forum.auth.models.entities.PostEntity;
import com.lonelyprogrammer.forum.auth.models.entities.PostUpdateEntity;
import com.lonelyprogrammer.forum.auth.services.PostsService;
import org.jetbrains.annotations.NotNull;
import static org.springframework.http.HttpStatus.*;
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
    public ResponseEntity getPostDetails(@PathVariable(name = "id") int id,
                                       @RequestParam(name = "related", required = false) Set<String> related) {
        final PostDetailsEntity loaded = postsService.getPostDetails(id, related);
        if (loaded == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(loaded);
    }
    @PostMapping(path = "/{id}/details")
    public ResponseEntity updatePostDetails(@PathVariable(name = "id") int id,
                                       @RequestBody PostUpdateEntity update) {
        final PostEntity loaded = postsService.get(id);
        if (loaded == null) return ResponseEntity.status(NOT_FOUND).build();
        postsService.updatePost(loaded, update);
        return ResponseEntity.ok(loaded);
    }



    public PostsController(@NotNull PostsService postsService) {
        this.postsService = postsService;
    }
}
