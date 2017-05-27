package com.lonelyprogrammer.forum.auth.dao;

import com.lonelyprogrammer.forum.auth.models.entities.ForumThreadEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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


    public ForumThreadEntity add2(ForumThreadEntity entity) {
        try {
            final String insertQuery = "INSERT INTO threads(title, author, forum, message) VALUES(?,?,?,?) RETURNING id;"; //get id
            final Integer id = db.queryForObject(insertQuery, Integer.class, entity.getTitle(), entity.getAuthor(), entity.getForum(), entity.getMessage());
            entity.setId(id);
            final String updateForumQuery = String.format("UPDATE forum SET threads = threads + 1 WHERE slug = '%s'", entity.getForum());
            db.update(updateForumQuery);

        } catch (DataAccessException e) {
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
        return new ForumThreadEntity(author, created, forum, id, message, slug, title, votes);
    };

    public ForumThreadEntity add(ForumThreadEntity thread) {
        String query = new StringBuilder()
                .append("INSERT INTO threads(title, author, forum, message) ")
                .append("VALUES(?,?,?,?) RETURNING id;")
                .toString();
        String createdQuery = new StringBuilder()
                .append("UPDATE threads SET created = ? WHERE id = ? ;")
                .toString();
        String slugQuery = new StringBuilder()
                .append("UPDATE threads SET slug = ? WHERE id = ? ;")
                .toString();
        String subQuery = new StringBuilder()
                .append("UPDATE forum SET threads = threads + 1 ")
                .append("WHERE slug = ? ;")
                .toString();

        ForumThreadEntity newThread = null;
        try {

            int id = db.queryForObject(query, Integer.class, thread.getTitle(), thread.getAuthor(), thread.getForum(), thread.getMessage());
            db.update(subQuery, thread.getForum());

            if (thread.getCreated() != null) {
                String st = ZonedDateTime.parse(thread.getCreated()).format(DateTimeFormatter.ISO_INSTANT);
                db.update(createdQuery, new Timestamp(ZonedDateTime.parse(st).toLocalDateTime().toInstant(ZoneOffset.UTC).toEpochMilli()), id);
            }

            if (thread.getSlug() != null) {
                db.update(slugQuery, thread.getSlug(), id);
            }

            newThread = getById(id);
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
            return null;
        }

        return newThread;
    }

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

    public List<ForumThreadEntity> getByForum(String slug, Integer limit, @Nullable Timestamp time, boolean desc) {
        StringBuilder queryBuilder = new StringBuilder()
                .append("SELECT * FROM threads WHERE forum = ? ");

        if (time != null) {
            if (desc) {
                queryBuilder.append("AND created <= ? ");
            } else
                queryBuilder.append("AND created >= ? ");
        }

        if (desc) {
            queryBuilder.append("ORDER BY created DESC ");
        } else
            queryBuilder.append("ORDER BY created ");

        queryBuilder.append("LIMIT ? ;");

        String query = queryBuilder.toString();

        ArrayList<ForumThreadEntity> threads = null;
        try {
            List<Map<String, Object>> rows;
            if (time != null)
                rows = db.queryForList(query, slug, time, limit);
            else
                rows = db.queryForList(query, slug, limit);

            threads = new ArrayList<>();
            for (Map<String, Object> row : rows) {
                threads.add(new ForumThreadEntity(row.get("author").toString(), Timestamp.valueOf(row.get("created").toString())
                                .toInstant().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                                row.get("forum").toString(),
                                Integer.parseInt(row.get("id").toString()),
                                row.get("message").toString(),
                                row.get("slug").toString(),
                                row.get("title").toString(),
                                Integer.parseInt(row.get("votes").toString())
                        )
                );
            }
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
            return null;
        }
        return threads;
    }
}
