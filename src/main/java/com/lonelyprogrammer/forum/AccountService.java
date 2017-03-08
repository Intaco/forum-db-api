package com.lonelyprogrammer.forum;

import com.msiops.ground.either.Either;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.lonelyprogrammer.forum.auth.ErrorState;
import com.lonelyprogrammer.forum.auth.dao.UserDAO;
import com.lonelyprogrammer.forum.auth.models.AuthorizationCredentials;
import com.lonelyprogrammer.forum.auth.models.ChangePasswordCredentials;
import com.lonelyprogrammer.forum.auth.models.ErrorResponse;
import com.lonelyprogrammer.forum.auth.models.User;

import static com.lonelyprogrammer.forum.auth.utils.RequestValidator.isValidEmailAddress;

@Service
public class AccountService {
    private UserDAO userDAO;
    /**
     * @return possible registration errors
     */
    @Nullable
    public ErrorResponse register(AuthorizationCredentials credentials) {
        if (StringUtils.isEmpty(credentials.getLogin()) || StringUtils.isEmpty(credentials.getEmail())
                || StringUtils.isEmpty(credentials.getPassword())) {
            return new ErrorResponse("login, password, and email should be non-empty!", ErrorState.BAD_REQUEST);
        } else if (!credentials.getLogin().matches("^[a-zA-Z0-9\\-_]+$")) {
            return new ErrorResponse("login should contain only latin letters or digits!", ErrorState.BAD_REQUEST);
        } else if (userDAO.load(credentials.getLogin()) != null) {
            return new ErrorResponse("User with that login already exists!", ErrorState.FORBIDDEN);
        } else if (!isValidEmailAddress(credentials.getEmail())) {
            return new ErrorResponse("Invalid e-mail format!", ErrorState.BAD_REQUEST);
        }
        final User newUser = new User(credentials.getLogin(), credentials.getPassword(), credentials.getEmail());
        userDAO.save(newUser);
        return null;
    }

    /**
     * @return possibly existing User
     */

    public Either<User, ErrorResponse> loadUser(String login) {
        final User loaded = userDAO.load(login);
        if (loaded != null) {
            return Either.left(loaded);
        }
        return Either.right(new ErrorResponse("Incorrect login", ErrorState.FORBIDDEN));
    }

    public Either<User, ErrorResponse> loginUser(AuthorizationCredentials credentials) {
        final User userFromDB;
        if (StringUtils.isEmpty(credentials.getLogin()) || StringUtils.isEmpty(credentials.getPassword())) {
            return Either.right(new ErrorResponse("login and password should be non-empty!", ErrorState.BAD_REQUEST));
        } else {
            final User loaded = userDAO.load(credentials.getLogin());
            if (loaded == null) {
                return Either.right(new ErrorResponse("User with that login does not exist", ErrorState.FORBIDDEN));
            } else userFromDB = loaded;
        }
        if (!userFromDB.getPassword().equals(credentials.getPassword())) {
            return Either.right(new ErrorResponse("Incorrect password!", ErrorState.FORBIDDEN));
        }
        return Either.left(userFromDB);
    }

    @Nullable
    public ErrorResponse changePassword(ChangePasswordCredentials credentials) {
        if (StringUtils.isEmpty(credentials.getLogin()) || StringUtils.isEmpty(credentials.getNewPassword())
                || StringUtils.isEmpty(credentials.getPassword())) {
            return new ErrorResponse("login, password, and new password should be non-empty!", ErrorState.BAD_REQUEST);
        } else {
            final User userFromDB = userDAO.load(credentials.getLogin());
            if (userFromDB != null) {
                if (userFromDB.getLogin().equals(credentials.getLogin())) {
                    return new ErrorResponse("Incorrect login!", ErrorState.FORBIDDEN);
                }
                if (!userFromDB.getPassword().equals(credentials.getPassword())) {
                    return new ErrorResponse("Incorrect password!", ErrorState.FORBIDDEN);
                }
            } else {
                return new ErrorResponse("User with that login does not exist", ErrorState.FORBIDDEN);
            }
            userDAO.updatePassword(credentials.getLogin(), credentials.getNewPassword());
        }
        return null; //success
    }

    public AccountService(@NotNull UserDAO userDAO) {
        this.userDAO = userDAO;
    }
}
