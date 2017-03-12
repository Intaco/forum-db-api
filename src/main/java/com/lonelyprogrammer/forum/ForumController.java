package com.lonelyprogrammer.forum;


import com.lonelyprogrammer.forum.auth.models.entities.ForumCreateBranchEntity;
import com.lonelyprogrammer.forum.auth.models.entities.ForumEntity;
import com.lonelyprogrammer.forum.auth.services.ForumsService;
import com.msiops.ground.either.Either;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.lonelyprogrammer.forum.auth.models.*;

import java.util.List;

import static com.lonelyprogrammer.forum.auth.utils.ResponseUtils.buildErrorResponse;

@RestController
@CrossOrigin // for localhost usage
//@CrossOrigin(origins = "https://[...].herokuapp.com") //for remote usage
public class ForumController {
    Logger logger = LoggerFactory.getLogger(ForumController.class);

    @NotNull
    private final ForumsService forumsService;

    @RequestMapping(path = "forum/create", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public ResponseEntity register(@RequestBody ForumEntity data) {
        logger.debug("/forum create called with slug: {}", data.getSlug());
        final Either<ForumEntity, List<ErrorResponse>> result = forumsService.createForum(data);
        if (result.isLeft()) {
            final ForumEntity forum = result.getLeft();
            if (forum.getSlug().equals(data.getSlug()) && forumsService.forumsDifferExceptSlug(forum, data)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(forum);
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(forum);
        }
        return buildErrorResponse(result.getRight());
    }

    @RequestMapping(path = "forum/{slug}/create", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public ResponseEntity register(@PathVariable String slug, @RequestBody ForumCreateBranchEntity data) {
        logger.debug("/forum disqus branch called with slug: {}", data.getSlug());
        final Either<ForumEntity, List<ErrorResponse>> result = forumsService.createForumBranch(data, slug);
    }

    public ForumController(@NotNull ForumsService forumsService) {
        this.forumsService = forumsService;
    }
}
