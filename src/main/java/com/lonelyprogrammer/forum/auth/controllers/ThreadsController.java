package com.lonelyprogrammer.forum.auth.controllers;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by nikita on 20.03.17.
 */

@RestController
@RequestMapping(value = "api/threads")
@CrossOrigin // for localhost usage
public class ThreadsController {

    public ThreadsController() {
    }
}
