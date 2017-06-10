package com.lonelyprogrammer.forum.auth.controllers;

import com.lonelyprogrammer.forum.auth.dao.DBDAO;
import com.lonelyprogrammer.forum.auth.models.entities.UserEntity;
import com.lonelyprogrammer.forum.auth.services.AccountService;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping(value = "api/user")
@CrossOrigin // for localhost usage
//@CrossOrigin(origins = "https://[...].herokuapp.com") //for remote usage
public class UserController {
    Logger logger = LoggerFactory.getLogger(UserController.class);

    @NotNull
    private final AccountService accountService;

    @RequestMapping(path = "/{nickname}/create", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public ResponseEntity create(@PathVariable(name = "nickname") String nickname, @RequestBody UserEntity data) {
        //logger.debug("/create called with nickname: {}", nickname);
        data.setNickname(nickname);
        final HttpStatus status = accountService.create(data);
        if (status == CONFLICT){
            final List<UserEntity> loaded = accountService.loadSimilarUsers(data); //should never be empty
            return ResponseEntity.status(CONFLICT).body(loaded);
        }
        return ResponseEntity.status(CREATED).body(data);
    }
    @RequestMapping(path = "/{nickname}/profile", method = RequestMethod.GET)
    public ResponseEntity getProfile(@PathVariable(name = "nickname") String nickname) {
        //logger.debug("/get profile called with nickname: {}", nickname);
        final UserEntity data = accountService.loadProfile(nickname);
        if (data == null){
            return ResponseEntity.status(NOT_FOUND).build();
        }
        return ResponseEntity.ok(data);
    }
    @RequestMapping(path = "/{nickname}/profile", method = RequestMethod.POST)
    public ResponseEntity updateProfile(@PathVariable(name = "nickname") String nickname, @RequestBody UserEntity data) {
        //logger.debug("/update profile called with nickname: {}", nickname);
        data.setNickname(nickname);
        final HttpStatus status = accountService.updateProfile(data);
        if (status != OK){
            return ResponseEntity.status(status).build();
        }
        return ResponseEntity.ok(accountService.loadProfile(nickname));
    }

    public UserController(@NotNull AccountService accountService) {
        this.accountService = accountService;
    }
}
