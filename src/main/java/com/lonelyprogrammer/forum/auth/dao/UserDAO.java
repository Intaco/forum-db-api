package com.lonelyprogrammer.forum.auth.dao;
import com.lonelyprogrammer.forum.auth.models.entities.UserEntity;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by algys on 24.02.17.
 */


@Service
@Transactional
public class UserDAO {

    private final JdbcTemplate db;

    @Autowired
    public UserDAO(JdbcTemplate template) {
        this.db = template;
    }

    public void add(UserEntity user) throws DuplicateKeyException {
        final String sql = "INSERT INTO users(nickname, fullname, about, email) VALUES(?,?,?,?);";
        db.update(sql, user.getNickname(), user.getFullName(), user.getAbout(), user.getEmail());
    }
    @Nullable
    public ArrayList<UserEntity> getSimilarUsers(UserEntity entity){
        final String sql = "SELECT * FROM users WHERE nickname = ? OR email = ?";
        final List<Map<String, Object>> maps = db.queryForList(sql, entity.getNickname(), entity.getEmail());
        final List<UserEntity> result = null;
        for (Map<String, Object> map: maps){

        }

    }

    private final RowMapper<UserEntity> userMapper = (resultSet, rowNum) -> {
        final String nickname = resultSet.getString("nickname");
        final String fullname = resultSet.getString("fullname");
        final String email = resultSet.getString("email");
        final String about = resultSet.getString("about");

        return new UserEntity(nickname, about, email, fullname);
    };

}
