package com.lonelyprogrammer.forum.auth.controllers;


import com.lonelyprogrammer.forum.auth.dao.DatabaseCreatorDAO;
import com.lonelyprogrammer.forum.auth.models.entities.ForumEntity;
import com.lonelyprogrammer.forum.auth.models.entities.ForumThreadEntity;
import com.lonelyprogrammer.forum.auth.services.AccountService;
import com.lonelyprogrammer.forum.auth.services.ForumsService;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.springframework.http.HttpStatus.*;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "api/forum")
@CrossOrigin // for localhost usage
public class ForumController {
    Logger logger = LoggerFactory.getLogger(ForumController.class);

    @NotNull
    private final ForumsService forumsService;
    @NotNull
    private final AccountService accountService;

    @RequestMapping(path = "/create", method = RequestMethod.POST)
    public ResponseEntity createForum(@RequestBody ForumEntity data) {
        logger.debug("/forum create called with slug: {}", data.getSlug());
        final HttpStatus status = forumsService.createForum(data);

        if (status == NOT_FOUND) {
            return ResponseEntity.status(NOT_FOUND).build();
        } else if (status == CONFLICT) {
            final ForumEntity loaded = forumsService.getBySlug(data.getSlug());
            return ResponseEntity.status(CONFLICT).body(loaded);
        }
        return ResponseEntity.status(CREATED).body(data);
    }

    @RequestMapping(path = "/{slug}/details", method = RequestMethod.GET)
    public ResponseEntity getDetails(@PathVariable(name = "slug") String slug) {
        logger.debug("/forum details called with slug: {}", slug);
        final ForumEntity loaded = forumsService.getBySlug(slug);
        if (loaded == null) {
            return ResponseEntity.status(NOT_FOUND).build();
        }
        return ResponseEntity.ok(loaded);
    }

    @RequestMapping(path = "/{slug}/create", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public ResponseEntity createThread(@PathVariable(name = "slug") String slug, @RequestBody ForumThreadEntity data) {
        logger.debug("/forum threads create called with slug: {}", slug);
        final ForumEntity loadedForum = forumsService.getBySlug(slug);
        if (loadedForum == null){
            return ResponseEntity.status(NOT_FOUND).build();
        }
        data.setForum(loadedForum.getSlug());
        ForumThreadEntity created = null;
        try{
            created = forumsService.createThread(data);
        } catch (DuplicateKeyException e){
            ForumThreadEntity loaded = forumsService.loadForumThread(data.getSlug());
            return ResponseEntity.status(CONFLICT).body(loaded);
        } catch (DataIntegrityViolationException e){
            return ResponseEntity.status(NOT_FOUND).build();
        }

        if (created!=null && created.getId() != null) {
            return ResponseEntity.status(CREATED).body(created);
        }
        return ResponseEntity.status(NOT_FOUND).build();
    }

    @RequestMapping(path = "/{slug}/threads", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getThreads(@PathVariable(name = "slug") String slug, @RequestParam(name = "limit", required = false) Integer limit,
                                     @RequestParam(name = "since", required = false) String since,
                                     @RequestParam(name = "desc", required = false) Boolean desc) {
        logger.debug("/forum threads info called with slug: {}", slug);
        if (desc == null) {
            desc = false;
        }
        if (limit == null) {
            limit = 0;
        }
        final ForumEntity forum = forumsService.getBySlug(slug);
        if (forum == null) {
            return ResponseEntity.status(NOT_FOUND).build();
        }
        final List<ForumThreadEntity> loaded = forumsService.loadThreadsByForum(slug, limit, since, desc);
        return ResponseEntity.ok(loaded);
    }

    public ForumController(@NotNull ForumsService forumsService, @NotNull AccountService accountService) {
        this.forumsService = forumsService;
        this.accountService = accountService;
    }
}
