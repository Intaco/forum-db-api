package com.lonelyprogrammer.forum.auth.dao;

import com.lonelyprogrammer.forum.auth.models.entities.ForumThreadEntity;
import org.jetbrains.annotations.Nullable;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Created by nikita
 * Date: 2017-03-14
 */
@Service
@Transactional
public class ThreadDAO {

    private final JdbcTemplate db;
    public ThreadDAO(JdbcTemplate template) {
        this.db = template;
    }
    public ForumThreadEntity add(ForumThreadEntity entity){
        try{
            final String sql = "INSERT INTO threads(title, author, forum, message) VALUES(?,?,?,?) RETURNING id;"; //get id
            Integer id = db.queryForObject(sql, Integer.class, entity.getTitle(), entity.getAuthor(), entity.getForum(), entity.getMessage());
            entity.setId(id);
        } catch (DataAccessException e){
            throw e;
        }
        return entity;
        //return THIS entity with id from db
    }

    private final RowMapper<ForumThreadEntity> threadMapper = (resultSet, rowNum) -> {
        final int id = resultSet.getInt("id");
        final String slug = resultSet.getString("slug");
        final String message = resultSet.getString("message");
        final String forum = resultSet.getString("forum");
        final String author = resultSet.getString("author");
        final int votes = resultSet.getInt("votes");
        final String title = resultSet.getString("title");
        final String created = resultSet.getTimestamp("created").toInstant().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        return new ForumThreadEntity(author,created,forum,id,message,slug,title,votes);
    };

    @Nullable
    public ForumThreadEntity getById(int id) {

        ForumThreadEntity loaded = null;
        try {
            final String query = String.format("SELECT * FROM threads WHERE id = '%d';", id);
            loaded = db.queryForObject(query, threadMapper);
        } catch (DataAccessException e) {

        }
        return loaded;
    }
    @Nullable
    public ForumThreadEntity getBySlug(String slug) {

        ForumThreadEntity loaded = null;
        try {
            final String query = String.format("SELECT * FROM threads WHERE slug = '%s';", slug);
            loaded = db.queryForObject(query, threadMapper);
        } catch (DataAccessException e) {

        }
        return loaded;
    }
}
