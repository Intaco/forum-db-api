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
        clearThreads();
        clearForums();
        clearUsers();
        createUsers();
        createForums();
        createThreads();
    }
    private void clearUsers(){
        final String sql = "DROP TABLE IF EXISTS users CASCADE;";
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
        final String sql = "DROP TABLE IF EXISTS forums CASCADE ;";
        db.execute(sql);
    }
    private void clearThreads(){
        final String sql = "DROP TABLE IF EXISTS threads CASCADE;";
        db.execute(sql);
    }
    private void createThreads() {
        final String sql = "CREATE TABLE IF NOT EXISTS threads (id SERIAL PRIMARY KEY, title VARCHAR(128) NOT NULL, "
                + "author CITEXT NOT NULL, forum CITEXT NOT NULL, message TEXT NOT NULL, votes BIGINT NOT NULL DEFAULT 0, "
                + "slug CITEXT UNIQUE, created TIMESTAMP NOT NULL DEFAULT current_timestamp," +
                " FOREIGN KEY (author) REFERENCES users(nickname), FOREIGN KEY (forum) REFERENCES forums(slug)); "
                + "CREATE UNIQUE INDEX ON threads (id); " + "CREATE UNIQUE INDEX ON threads (slug); "
                + "CREATE INDEX ON threads (author); " + "CREATE INDEX ON threads (forum); ";
        db.execute(sql);
    }
}
