package com.lonelyprogrammer.forum.auth.dao;

import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import com.lonelyprogrammer.forum.auth.models.User;

import java.util.HashMap;
import java.util.Map;

//В дальнейшем здесь будем доставать из БД
@Service
public class UserDAO {
    //<login, User>
    private Map<String, User> registeredUsers = new HashMap<>();

    public void save(User user){
        registeredUsers.put(user.getLogin(), user);
    }
    @Nullable
    public User load(String login){
        return registeredUsers.get(login);
    }
    public void updatePassword(String login, String newPassword){
        final User user = registeredUsers.get(login);
        user.setPassword(newPassword);
        registeredUsers.put(login,user); //overwrite old
    }

    public UserDAO() {
        registeredUsers = new HashMap<>();
        registeredUsers.put("test-user", new User("test-user", "test-password","test_email@test.ru"));
    }
}
