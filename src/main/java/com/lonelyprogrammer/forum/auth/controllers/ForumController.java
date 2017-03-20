package com.lonelyprogrammer.forum.auth.controllers;


import com.lonelyprogrammer.forum.auth.dao.DatabaseCreatorDAO;
import com.lonelyprogrammer.forum.auth.models.entities.ForumEntity;
import com.lonelyprogrammer.forum.auth.services.ForumsService;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "api/forum")
@CrossOrigin // for localhost usage
//@CrossOrigin(origins = "https://[...].herokuapp.com") //for remote usage
public class ForumController {
    Logger logger = LoggerFactory.getLogger(ForumController.class);

    @NotNull
    private final ForumsService forumsService;

    @RequestMapping(path = "/create", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public ResponseEntity createForum(@RequestBody ForumEntity data) {
        logger.debug("/forum create called with slug: {}", data.getSlug());
        final HttpStatus status = forumsService.createForum(data);

        if (status == HttpStatus.NOT_FOUND){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else if (status == HttpStatus.CONFLICT){
            final ForumEntity loaded = forumsService.getBySlug(data.getSlug());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(loaded);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(data);
    }
/*
    @RequestMapping(path = "/{slug}/create", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public ResponseEntity register(@PathVariable String slug, @RequestBody ForumThreadEntity data) {
        logger.debug("/forum disqus branch called with slug: {}", data.getSlug());
        final Either<ForumThreadEntity,ErrorResponse> result = forumsService.createForumThread(data, slug);
        if (!result.isLeft()) {
            return buildErrorResponse(result.getRight());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(result.getLeft());

    }*/

    public ForumController(@NotNull ForumsService forumsService) {
        this.forumsService = forumsService;
    }
}
