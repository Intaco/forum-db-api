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
    private final JdbcTemplate template;
    @Autowired
    public DatabaseCreatorDAO(JdbcTemplate template) {
        this.template = template;
    }
    public void clean(){
        clearUsers();
        createUsers();
    }
    private void clearUsers(){
        final String sql = "DROP TABLE IF EXISTS users ;";
        template.execute(sql);
    }
    private void createUsers(){
        final String sql = "CREATE EXTENSION IF NOT EXISTS citext; " +
                "CREATE TABLE IF NOT EXISTS users ( nickname CITEXT UNIQUE NOT NULL PRIMARY KEY, fullname varchar(128) NOT NULL," +
                " about text, email CITEXT UNIQUE NOT NULL); CREATE UNIQUE INDEX ON users (nickname); ";
        template.execute(sql);
    }

}
