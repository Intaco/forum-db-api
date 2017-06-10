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

import java.sql.Timestamp;
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
    public List<UserEntity> getSimilarUsers(UserEntity entity) {
        final String sql = String.format("SELECT * FROM users WHERE nickname = '%s' OR email = '%s';",
                entity.getNickname(), entity.getEmail());
        return db.query(sql, userMapper);
    }

    @Nullable
    public UserEntity getByNickname(String nickName) {
        UserEntity loaded = null;
        try {
            final String sql = String.format("SELECT * FROM users WHERE nickname = '%s';", nickName);
            loaded = db.queryForObject(sql, userMapper);
        } catch (DataAccessException e) {

        }
        return loaded;
    }

    @Nullable
    public UserEntity getByEmail(String email) {
        UserEntity loaded = null;
        try {
            final String sql = String.format("SELECT * FROM users WHERE email = '%s';", email);
            loaded = db.queryForObject(sql, userMapper);
        } catch (DataAccessException e) {

        }
        return loaded;
    }

    public void updateUser(UserEntity entity) {
        final String nickname = entity.getNickname();
        final String fullname = entity.getFullName();
        final String about = entity.getAbout();
        final String email = entity.getEmail();
        final StringBuilder builder = new StringBuilder("UPDATE users SET ");
        if (fullname != null) {
            builder.append(String.format("fullname = '%s',", fullname));
        }
        if (about != null) {
            builder.append(String.format("about = '%s',", about));
        }
        if (email != null) {
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


    public List<UserEntity> loadUsersByForum(String slug, Integer limit, @Nullable String since, boolean desc) {
        final StringBuilder sql = new StringBuilder("SELECT * FROM users WHERE nickname IN (SELECT author FROM forum_users WHERE LOWER(forum) = LOWER(?)) ");
        final List<Object> args = new ArrayList<>();
        args.add(slug);
        if (since != null){
            if (desc){
                sql.append("AND LOWER(nickname COLLATE \"ucs_basic\") < LOWER(? COLLATE \"ucs_basic\") ");
            } else sql.append("AND LOWER(nickname COLLATE \"ucs_basic\") > LOWER(? COLLATE \"ucs_basic\") ");
            args.add(since);
        }
        if (desc){
            sql.append("ORDER BY LOWER(nickname COLLATE \"ucs_basic\") DESC ");
        } else sql.append("ORDER BY LOWER(nickname COLLATE \"ucs_basic\") ASC ");
        if (limit != null){
            sql.append("LIMIT ?");
            args.add(limit);
        }
        sql.append(';');
        return db.query(sql.toString(), userMapper, args.toArray());
    }
    public int getCount(){
        final String sql = "SELECT COUNT(nickname) FROM users ;";
        return db.queryForObject(sql, Integer.class);
    }

    private static final RowMapper<UserEntity> userMapper = (resultSet, rowNum) -> {
        final String nickname = resultSet.getString("nickname");
        final String about = resultSet.getString("about");
        final String email = resultSet.getString("email");
        final String fullname = resultSet.getString("fullname");

        return new UserEntity(nickname, about, email, fullname);
    };

}
