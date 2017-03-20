package com.lonelyprogrammer.forum.auth.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by nikita on 14.03.17.
 */
@Service
@Transactional
public class DatabaseCreatorDAO {
    private final JdbcTemplate db;

    public DatabaseCreatorDAO(JdbcTemplate template) {
        this.db = template;
    }
    public void reset(){
        clearForums();
        clearUsers();

        createUsers();
        createForums();
    }
    private void clearUsers(){
        final String sql = "DROP TABLE IF EXISTS users ;";
        db.execute(sql);
    }
    private void createUsers(){
        final String sql = "CREATE EXTENSION IF NOT EXISTS citext; " +
                "CREATE TABLE IF NOT EXISTS users ( nickname CITEXT UNIQUE NOT NULL PRIMARY KEY, fullname varchar(128) NOT NULL," +
                " about text, email CITEXT UNIQUE NOT NULL); CREATE UNIQUE INDEX ON users (nickname); ";
        db.execute(sql);
    }
    private void createForums(){
        final String sql = ("CREATE TABLE IF NOT EXISTS forums ( ") +
                ("title VARCHAR(128) NOT NULL, ") +
                ("admin CITEXT NOT NULL, ") +
                ("slug CITEXT UNIQUE NOT NULL PRIMARY KEY, ") +
                ("posts BIGINT NOT NULL DEFAULT 0, ") +
                ("threads BIGINT NOT NULL DEFAULT 0, ") +
                ("FOREIGN KEY (admin) REFERENCES users(nickname)); ") +
                ("CREATE UNIQUE INDEX ON forums (slug); ") +
                ("CREATE INDEX ON forums (admin); ");
        db.execute(sql);

    }
    private void clearForums(){
        final String sql = "DROP TABLE IF EXISTS forums";
        db.execute(sql);
    }

}
