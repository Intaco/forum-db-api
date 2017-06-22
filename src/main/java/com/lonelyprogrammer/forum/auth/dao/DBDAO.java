package com.lonelyprogrammer.forum.auth.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by nikita on 14.03.17.
 */
@Service
@Transactional
public class DBDAO {
    private final JdbcTemplate db;

    public DBDAO(JdbcTemplate template) {
        this.db = template;
    }
    public void reset(){
        final String sql = "TRUNCATE TABLE users, forums, threads, votes, posts CASCADE;";
        db.execute(sql);
    }
}
