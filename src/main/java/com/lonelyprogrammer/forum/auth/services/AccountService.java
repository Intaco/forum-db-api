package com.lonelyprogrammer.forum.auth.services;

import com.lonelyprogrammer.forum.auth.ErrorState;
import com.lonelyprogrammer.forum.auth.dao.UserDAO;
import com.lonelyprogrammer.forum.auth.models.AuthorizationCredentials;
import com.lonelyprogrammer.forum.auth.models.ChangePasswordCredentials;
import com.lonelyprogrammer.forum.auth.models.ErrorResponse;
import com.lonelyprogrammer.forum.auth.models.User;
import com.msiops.ground.either.Either;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


import java.util.ArrayList;
import java.util.List;

import static com.lonelyprogrammer.forum.auth.utils.RequestValidator.isValidEmailAddress;


@Service
public class AccountService {
    private UserDAO userDAO;

    /**
     * @return possible registration errors
     */
    @NotNull
    public List<ErrorResponse> register(AuthorizationCredentials credentials) {

        final List<ErrorResponse> errors = new ArrayList<>();

        if (StringUtils.isEmpty(credentials.getLogin()) || StringUtils.isEmpty(credentials.getEmail()) || StringUtils.isEmpty(credentials.getPassword())) {
            errors.add(new ErrorResponse("Empty credentials", ErrorState.BAD_REQUEST));
        }
        if (!credentials.getLogin().matches("^[a-zA-Z0-9\\-_]+$") && !StringUtils.isEmpty(credentials.getLogin())) {
            errors.add(new ErrorResponse("Incorrect login", ErrorState.BAD_REQUEST));
        }
        if (userDAO.load(credentials.getLogin()) != null && !StringUtils.isEmpty(credentials.getLogin())) {
            errors.add(new ErrorResponse("User exists", ErrorState.FORBIDDEN));
        }
        if (!isValidEmailAddress(credentials.getEmail()) && !StringUtils.isEmpty(credentials.getEmail())) {
            errors.add(new ErrorResponse("Email format", ErrorState.BAD_REQUEST));
        }

        if (errors.isEmpty()) {
            final User newUser = new User(credentials.getLogin(), credentials.getPassword(), credentials.getEmail());
            userDAO.save(newUser);
        }
        return errors;
    }

    /**
     * @return possibly existing User
     */

    public Either<User, List<ErrorResponse>> loadUser(String login) {
        final User loaded = userDAO.load(login);
        if (loaded != null) {
            return Either.left(loaded);
        }
        final List<ErrorResponse> errors = new ArrayList<>();
        errors.add(new ErrorResponse("Incorrect login", ErrorState.FORBIDDEN));
        return Either.right(errors);
    }
    public Either<User, List<ErrorResponse>> loginUser(AuthorizationCredentials credentials) {
        final List<ErrorResponse> errors = new ArrayList<>();
        if (StringUtils.isEmpty(credentials.getLogin()) || StringUtils.isEmpty(credentials.getPassword())) {
            errors.add(new ErrorResponse("login and password should be non-empty!", ErrorState.BAD_REQUEST));
        }
        final User userFromDB = userDAO.load(credentials.getLogin());
        if (userFromDB == null){
            errors.add(new ErrorResponse("User with that login does not exist", ErrorState.FORBIDDEN));
        } else if (!userFromDB.getPassword().equals(credentials.getPassword())) {
            errors.add(new ErrorResponse("Incorrect password!", ErrorState.FORBIDDEN));
        }
        if (!errors.isEmpty()){
            return Either.right(errors);
        }
        //noinspection ConstantConditions
        return Either.left(userFromDB); //wont be reached if null
    }

    @NotNull
    public List<ErrorResponse> changePassword(ChangePasswordCredentials credentials) {

        final ArrayList<ErrorResponse> errors = new ArrayList<>();
        if (StringUtils.isEmpty(credentials.getLogin()) || StringUtils.isEmpty(credentials.getNewPassword())
                || StringUtils.isEmpty(credentials.getPassword())) {
            errors.add(new ErrorResponse("Empty credentials", ErrorState.BAD_REQUEST));
        }
        if (!StringUtils.isEmpty(credentials.getLogin())) {
            final User userFromDB = userDAO.load(credentials.getLogin());
            if (userFromDB != null) {
                if (!userFromDB.getPassword().equals(credentials.getPassword())
                        && StringUtils.isEmpty(credentials.getNewPassword())) {
                    errors.add(new ErrorResponse("Incorrect password!", ErrorState.FORBIDDEN));
                }
            } else {
                errors.add(new ErrorResponse("No such user", ErrorState.FORBIDDEN));
            }
        }

        if (errors.isEmpty()) {
            userDAO.updatePassword(credentials.getLogin(), credentials.getNewPassword());
        }

        return errors;
    }

    public AccountService(@NotNull UserDAO userDAO) {
        this.userDAO = userDAO;
    }
}
