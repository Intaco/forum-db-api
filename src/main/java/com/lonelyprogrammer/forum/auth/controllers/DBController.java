package com.lonelyprogrammer.forum.auth.controllers;

import com.lonelyprogrammer.forum.auth.dao.DatabaseCreatorDAO;
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
@RequestMapping(value = "api/service")
@CrossOrigin // for localhost usage

public class DBController {

    private final DatabaseCreatorDAO databaseCreatorDAO;
    @Autowired
    public DBController(DatabaseCreatorDAO databaseCreatorDAO) {
        this.databaseCreatorDAO = databaseCreatorDAO;
    }
    @RequestMapping(path = "/clear", method = RequestMethod.GET)
    public ResponseEntity clear(){
        databaseCreatorDAO.reset();
        return ResponseEntity.ok("DB cleared");
    }
}
