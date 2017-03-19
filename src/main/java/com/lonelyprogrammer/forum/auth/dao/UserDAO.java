package com.lonelyprogrammer.forum.auth.dao;
import com.lonelyprogrammer.forum.auth.models.entities.UserEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.dao.DataAccessException;
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
    @Nullable
    public UserEntity getByNickname(String nickName){
        UserEntity loaded = null;
        try {
            final String sql = String.format("SELECT * FROM users WHERE nickname = '%s';", nickName);
            loaded = db.queryForObject(sql, userMapper);
        } catch (DataAccessException e) {

        }
        return loaded;
    }
    @Nullable
    public UserEntity getByEmail(String email){
        UserEntity loaded = null;
        try {
            final String sql = String.format("SELECT * FROM users WHERE email = '%s';", email);
            loaded = db.queryForObject(sql, userMapper);
        } catch (DataAccessException e) {

        }
        return loaded;
    }
    public void updateUser(UserEntity entity){
        final String nickname = entity.getNickname();
        final String fullname = entity.getFullName() ;
        final String about = entity.getAbout();
        final String email = entity.getEmail();
        final StringBuilder builder = new StringBuilder("UPDATE users SET ");
        if (fullname != null){
            builder.append(String.format("fullname = '%s',", fullname));
        }
        if (about != null){
            builder.append(String.format("about = '%s',", about));
        }
        if (email != null){
            builder.append(String.format("email = '%s',", email));
        }
        final int commaIndex = builder.lastIndexOf(",");
        if (commaIndex != -1) {
            builder.replace(commaIndex, commaIndex + 1, "");
        }
        builder.append(String.format(" WHERE nickname = '%s'", nickname));
        final String sql = builder.toString();
        db.execute(sql);
    }

    private final RowMapper<UserEntity> userMapper = (resultSet, rowNum) -> {
        final String nickname = resultSet.getString("nickname");
        final String about = resultSet.getString("about");
        final String email = resultSet.getString("email");
        final String fullname = resultSet.getString("fullname");

        return new UserEntity(nickname, about, email, fullname);
    };

}
