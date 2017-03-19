package com.lonelyprogrammer.forum.auth.controllers;

import com.lonelyprogrammer.forum.auth.dao.DatabaseCreatorDAO;
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

import java.util.List;

@RestController
@RequestMapping(value = "api/user")
@CrossOrigin // for localhost usage
//@CrossOrigin(origins = "https://[...].herokuapp.com") //for remote usage
public class UserController {
    Logger logger = LoggerFactory.getLogger(UserController.class);

    @NotNull
    private final AccountService accountService;
    @NotNull
    private final DatabaseCreatorDAO databaseCreatorDAO;

    @RequestMapping(path = "/{username}/create", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public ResponseEntity create(@PathVariable(name = "username") String username, @RequestBody UserEntity data) {
        logger.debug("/create called with username: {}", username);
        data.setNickname(username);
        final HttpStatus status = accountService.create(data);
        if (status == HttpStatus.CONFLICT){
            final List<UserEntity> loaded = accountService.loadSimilarUsers(data); //should never be empty
            return ResponseEntity.status(HttpStatus.CONFLICT).body(loaded);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(data);
    }

    public UserController(@NotNull AccountService accountService, @NotNull DatabaseCreatorDAO databaseCreatorDAO) {
        this.accountService = accountService;
        this.databaseCreatorDAO = databaseCreatorDAO;
        databaseCreatorDAO.clean();
    }
}
