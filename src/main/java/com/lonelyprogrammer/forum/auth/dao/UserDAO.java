package com.lonelyprogrammer.forum.auth.dao;
import com.lonelyprogrammer.forum.auth.models.entities.UserEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
@Transactional
public class UserDAO {

    private final JdbcTemplate db;

    public UserDAO(JdbcTemplate template) {
        this.db = template;
    }

    public void add(UserEntity user) throws DuplicateKeyException {
        final String sql = "INSERT INTO users(nickname, fullname, about, email) VALUES(?,?,?,?);";
        db.update(sql, user.getNickname(), user.getFullName(), user.getAbout(), user.getEmail());
    }
    @NotNull
    public List<UserEntity> getSimilarUsers(UserEntity entity){
        final String sql = String.format("SELECT * FROM users WHERE nickname = '%s' OR email = '%s';",
                entity.getNickname(), entity.getEmail());
        return db.query(sql, userMapper);
    }

    private final RowMapper<UserEntity> userMapper = (resultSet, rowNum) -> {
        final String nickname = resultSet.getString("nickname");
        final String about = resultSet.getString("about");
        final String email = resultSet.getString("email");
        final String fullname = resultSet.getString("fullname");

        return new UserEntity(nickname, about, email, fullname);
    };

}
