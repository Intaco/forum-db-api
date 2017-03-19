package com.lonelyprogrammer.forum.auth.services;

import com.lonelyprogrammer.forum.auth.dao.UserDAO;
import com.lonelyprogrammer.forum.auth.models.entities.UserEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.util.StringUtils.isEmpty;


@Service
public class AccountService {
    private UserDAO userDAO;

    /**
     *
     * @param user - user to add
     * @return - Http status
     */
    public HttpStatus create(UserEntity user){
        if (isEmpty(user.getNickname()) || isEmpty(user.getFullName()) || isEmpty(user.getEmail())){
            return HttpStatus.UNPROCESSABLE_ENTITY;
        }
        try {
            userDAO.add(user);
        }
        catch (DuplicateKeyException ex1){
            return HttpStatus.CONFLICT;
        }
        return HttpStatus.CREATED;
    }

    /**
     *
     * @param entity - user to compare
     * @return - all users with same nickname / e-mail
     */
    @NotNull
    public List<UserEntity> loadSimilarUsers(UserEntity entity){
        return userDAO.getSimilarUsers(entity);
    }
    @Nullable
    public UserEntity loadProfile(String nickname){
        return userDAO.getByNickname(nickname);
    }
    public HttpStatus updateProfile(UserEntity entity){
        final UserEntity byNickname = userDAO.getByNickname(entity.getNickname());
        if (byNickname == null){ //check if user exists
            return HttpStatus.NOT_FOUND;
        }
        if (emptyUserDesc(entity)){
            return HttpStatus.OK;
        }
        final UserEntity byEmail = userDAO.getByEmail(entity.getEmail());
        if (byEmail != null && !byEmail.getNickname().equals(entity.getNickname())){ //check if user with that email already exists
            return HttpStatus.CONFLICT;
        }
        userDAO.updateUser(entity);
        return HttpStatus.OK;
    }
    private boolean emptyUserDesc(UserEntity entity){
        return isEmpty(entity.getEmail()) && isEmpty(entity.getFullName()) && isEmpty(entity.getAbout());
    }


    public AccountService(@NotNull UserDAO userDAO) {
        this.userDAO = userDAO;
    }
}
