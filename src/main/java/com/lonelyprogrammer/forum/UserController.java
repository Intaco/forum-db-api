package com.lonelyprogrammer.forum;

import com.lonelyprogrammer.forum.auth.models.*;
import com.lonelyprogrammer.forum.auth.services.AccountService;
import com.lonelyprogrammer.forum.auth.utils.RequestValidator;
import com.msiops.ground.either.Either;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

import static com.lonelyprogrammer.forum.auth.utils.ResponseUtils.buildErrorResponse;

@RestController
@CrossOrigin // for localhost usage
//@CrossOrigin(origins = "https://[...].herokuapp.com") //for remote usage
public class UserController {
    Logger logger = LoggerFactory.getLogger(UserController.class);

    @NotNull
    private final AccountService accountService;

    @RequestMapping(path = "api/signup", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public ResponseEntity register(@RequestBody AuthorizationCredentials credentials, HttpSession httpSession) {
        logger.debug("/signup called with login: {}", credentials.getLogin());
        final ErrorResponse sessionError = RequestValidator.validateNotAuthorizedSession(httpSession);
        if (sessionError != null) {
            return buildErrorResponse(sessionError);
        }
        final List<ErrorResponse> registrationError = accountService.register(credentials);
        if (!registrationError.isEmpty()){ // if errors returned
            return buildErrorResponse(registrationError);
        }
        return ResponseEntity.ok(new SuccessResponseMessage("Successfully registered user"));
    }

    @RequestMapping(path = "/api/login", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public ResponseEntity login(@RequestBody AuthorizationCredentials credentials, HttpSession httpSession) {
        logger.debug("/login called with login: {}", credentials.getLogin());
        final ErrorResponse sessionError = RequestValidator.validateNotAuthorizedSession(httpSession);
        if (sessionError !=null) {
            return buildErrorResponse(sessionError);
        }
        final Either<User, List<ErrorResponse>> result = accountService.loginUser(credentials);
        if (!result.isLeft()){ //if error
            return buildErrorResponse(result.getRight());
        }
        final User user = result.getLeft();
        httpSession.setAttribute(httpSession.getId(), user.getLogin());
        return ResponseEntity.ok(new SuccessResponseMessage("Successfully authorized user " + user.getLogin()));
    }

    @RequestMapping(path = "/api/logout", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public ResponseEntity logout(HttpSession httpSession) {
        logger.debug("/logout called for id: {}", httpSession.getId());
        final ErrorResponse sessionError = RequestValidator.validateAlreadyAuthorizedSession(httpSession);
        if (sessionError != null) {
            return buildErrorResponse(sessionError);
        }
        httpSession.removeAttribute(httpSession.getId());
        return ResponseEntity.ok(new SuccessResponseMessage("User successfully logged out"));
    }

    // change password
    @RequestMapping(path = "/api/user", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public ResponseEntity changePassword(@RequestBody ChangePasswordCredentials credentials, HttpSession httpSession) {
        logger.debug("/user-change-pass called");
        final ErrorResponse sessionError = RequestValidator.validateAlreadyAuthorizedSession(httpSession);
        if (sessionError !=null) {
            return buildErrorResponse(sessionError);
        }
        final List<ErrorResponse> passwordChangeErrors = accountService.changePassword(credentials);
        if (!passwordChangeErrors.isEmpty()) {
            return buildErrorResponse(passwordChangeErrors);
        }
        return ResponseEntity.ok(new SuccessResponseMessage("Successfully changed password for user "+credentials.getLogin()));
    }
    //get logged user data
    @RequestMapping(path = "/api/user", method = RequestMethod.GET, produces = "application/json", consumes = "application/json")
    public ResponseEntity getCurrentUser(HttpSession httpSession){
        final ErrorResponse sessionError = RequestValidator.validateAlreadyAuthorizedSession(httpSession);
        if (sessionError != null) {
            return buildErrorResponse(sessionError);
        }
        final String login = String.valueOf(httpSession.getAttribute(httpSession.getId())); //get login from session, 100% not null
        final Either<User, List<ErrorResponse>> result = accountService.loadUser(login);
        if (!result.isLeft()){
            return buildErrorResponse(result.getRight());
        }
        return ResponseEntity.ok(result.getLeft());
    }

    public UserController(@NotNull AccountService accountService) {
        this.accountService = accountService;
    }
}
