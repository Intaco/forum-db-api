package com.lonelyprogrammer.forum.auth.controllers;

import com.lonelyprogrammer.forum.auth.dao.DatabaseCreatorDAO;
import com.lonelyprogrammer.forum.auth.services.AccountService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by nikita on 14.03.17.
 */
@RestController
@RequestMapping(value = "api/db")
@CrossOrigin // for localhost usage

public class DBController {

    private final DatabaseCreatorDAO databaseCreatorDAO;
    @Autowired
    public DBController(DatabaseCreatorDAO databaseCreatorDAO) {
        this.databaseCreatorDAO = databaseCreatorDAO;
    }
    @RequestMapping(path = "/clear", method = RequestMethod.POST)
    public ResponseEntity clear(){
        databaseCreatorDAO.clean();
        return ResponseEntity.ok("DB cleared");
    }
}
