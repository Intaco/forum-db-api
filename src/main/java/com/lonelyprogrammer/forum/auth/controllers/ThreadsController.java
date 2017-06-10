package com.lonelyprogrammer.forum.auth.controllers;

import com.lonelyprogrammer.forum.auth.dao.VoteDAO;
import com.lonelyprogrammer.forum.auth.models.entities.ForumThreadEntity;
import com.lonelyprogrammer.forum.auth.models.entities.PostEntity;
import com.lonelyprogrammer.forum.auth.models.entities.VoteEntity;
import com.lonelyprogrammer.forum.auth.services.PostsService;
import com.lonelyprogrammer.forum.auth.services.ThreadsService;
import org.jetbrains.annotations.NotNull;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

import static org.springframework.http.HttpStatus.*;

/**
 * Created by nikita on 20.03.17.
 */

@RestController
@RequestMapping(value = "/api/thread")
@CrossOrigin // for localhost usage
public class ThreadsController {
    //private static Logger logger = LoggerFactory.getLogger(ThreadsController.class);
    @NotNull
    private final ThreadsService threadsService;
    @NotNull
    private final PostsService postsService;
    @NotNull
    private final VoteDAO voteDAO;

    @RequestMapping(path = "/{slugOrId}/create", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public ResponseEntity createPost(@PathVariable(name = "slugOrId") String slugOrId, @RequestBody List<PostEntity> posts) {
        final ForumThreadEntity loaded;
        if (isNumeric(slugOrId)) {
            final Integer id = Integer.parseInt(slugOrId);
            loaded = threadsService.get(id);
        } else loaded = threadsService.get(slugOrId); //as slug
        if (loaded == null) {
            return ResponseEntity.notFound().build();
        }
        final List<Integer> children = postsService.getThreadChildrenIds(loaded);
        for (PostEntity post : posts) {
            final int parentId = post.getParent();
            if (parentId != 0 && !children.contains(parentId)) return ResponseEntity.status(CONFLICT).build();
        }
        try {
            postsService.createPosts(posts, loaded);
        } catch (DuplicateKeyException e) {
            return ResponseEntity.status(CONFLICT).build();
        } catch (SQLException e) {
            return ResponseEntity.status(NOT_FOUND).build();
        }

        return ResponseEntity.status(CREATED).body(posts);
    }

    @GetMapping(path = "/{slugOrId}/details")
    public ResponseEntity getThreadDetails(@PathVariable(name = "slugOrId") String slugOrId) {
        final ForumThreadEntity loaded;
        if (isNumeric(slugOrId)) {
            final Integer id = Integer.parseInt(slugOrId);
            loaded = threadsService.get(id);
        } else loaded = threadsService.get(slugOrId); //as slug
        return loaded == null ? ResponseEntity.status(NOT_FOUND).build() : ResponseEntity.ok(loaded);
    }

    @PostMapping(path = "/{slugOrId}/details")
    public ResponseEntity updateThreadDetails(@PathVariable(name = "slugOrId") String slugOrId, @RequestBody ForumThreadEntity data) {
        final ForumThreadEntity loaded;
        if (isNumeric(slugOrId)) {
            final Integer id = Integer.parseInt(slugOrId);
            loaded = threadsService.get(id);
        } else loaded = threadsService.get(slugOrId); //as slug
        if (loaded == null) return ResponseEntity.status(NOT_FOUND).build();
        threadsService.updateThread(data, loaded);
        return ResponseEntity.ok(loaded);
    }

    @GetMapping(path = "/{slugOrId}/posts")
    public ResponseEntity getThreadPosts(@PathVariable(name = "slugOrId") final String slugOrId,
                                         @RequestParam(name = "limit", required = false, defaultValue = "0") final Integer limit,
                                         @RequestParam(name = "marker", required = false, defaultValue = "0") final Integer marker,
                                         @RequestParam(name = "sort", required = false, defaultValue = "flat") final String sort,
                                         @RequestParam(name = "desc", required = false, defaultValue = "false") final Boolean desc) {

        final ForumThreadEntity loaded;
        if (isNumeric(slugOrId)) {
            final Integer id = Integer.parseInt(slugOrId);
            loaded = threadsService.get(id);
        } else loaded = threadsService.get(slugOrId); //as slug
        return loaded == null ? ResponseEntity.status(NOT_FOUND).build() : ResponseEntity.ok(postsService.getPostsForThread(loaded.getId(), limit, marker, sort, desc));
    }

    @RequestMapping(path = "/{slugOrId}/vote", method = RequestMethod.POST)
    public ResponseEntity createVote(@PathVariable(name = "slugOrId") String slugOrId, @RequestBody VoteEntity vote) {
        ForumThreadEntity loaded;
        Integer id = null;
        if (isNumeric(slugOrId)) {
            id = Integer.parseInt(slugOrId);
            loaded = threadsService.get(id);
        } else loaded = threadsService.get(slugOrId); //as slug
        if (loaded == null) {
            return ResponseEntity.notFound().build();
        }
        try {
            voteDAO.createVote(vote, loaded);
        } catch (Exception e) {
            return ResponseEntity.status(NOT_FOUND).build();
        }
        loaded = id == null ? threadsService.get(slugOrId) : threadsService.get(id);
        return ResponseEntity.ok(loaded);
    }


    public static boolean isNumeric(String slugOrId) {
        return slugOrId.matches("\\d+");
    }

    public ThreadsController(@NotNull ThreadsService threadsService, @NotNull PostsService postsService, @NotNull VoteDAO voteDAO) {
        this.threadsService = threadsService;
        this.postsService = postsService;
        this.voteDAO = voteDAO;
    }
}
