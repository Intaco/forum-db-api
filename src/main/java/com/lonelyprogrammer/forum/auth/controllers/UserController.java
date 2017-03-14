package com.lonelyprogrammer.forum.auth.controllers;

import com.lonelyprogrammer.forum.auth.models.*;
import com.lonelyprogrammer.forum.auth.models.entities.UserEntity;
import com.lonelyprogrammer.forum.auth.services.AccountService;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

import static com.lonelyprogrammer.forum.auth.utils.ResponseUtils.buildErrorResponse;

@RestController
@RequestMapping(value = "api/user")
@CrossOrigin // for localhost usage
//@CrossOrigin(origins = "https://[...].herokuapp.com") //for remote usage
public class UserController {
    Logger logger = LoggerFactory.getLogger(UserController.class);

    @NotNull
    private final AccountService accountService;

    @RequestMapping(path = "/{nickname}/create", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public ResponseEntity create(@PathVariable String username, @RequestBody UserEntity data, HttpSession httpSession) {
        logger.debug("/create called with username: {}", username);
        data.setNickname(username);
        final HttpStatus status = accountService.create(data);
        if (status == HttpStatus.CONFLICT){

        }
        return ResponseEntity.ok(new SuccessResponseMessage("Successfully registered user"));
    }

    public UserController(@NotNull AccountService accountService) {
        this.accountService = accountService;
    }
}
