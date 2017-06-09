package com.lonelyprogrammer.forum.auth.dao;

import com.lonelyprogrammer.forum.auth.models.entities.ForumThreadEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
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
    private static Logger logger = LoggerFactory.getLogger(ThreadDAO.class);
    private final JdbcTemplate db;

    public ThreadDAO(JdbcTemplate template) {
        this.db = template;
    }

    private static final RowMapper<ForumThreadEntity> threadMapper = (resultSet, rowNum) -> {
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

    @Nullable
    public ForumThreadEntity add(ForumThreadEntity thread) { // TODO refactor
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
                .append("UPDATE forums SET threads = threads + 1 ")
                .append("WHERE slug = ? ;")
                .toString();

        ForumThreadEntity newThread = null;
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

        return newThread;
    }
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

    @NotNull
    public List<ForumThreadEntity> getByForum(String slug, Integer limit, @Nullable Timestamp time, boolean desc) {
        final StringBuilder builder = new StringBuilder(String.format("SELECT * FROM threads WHERE forum = '%s' ", slug));
        if (time != null) {
            if (desc) builder.append(String.format("AND created <= '%s'", time));
            else builder.append(String.format("AND created >= '%s'", time));
        }
        if (desc) {
            builder.append("ORDER BY created DESC ");
        } else builder.append("ORDER BY created ");

        builder.append(String.format("LIMIT '%s';", limit));
        final String query = builder.toString();

        List<ForumThreadEntity> threads = new ArrayList<>();
        try {
            threads = db.query(query, threadMapper);
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        return threads;
    }

    /**
     * @param newData info for updating in oldData
     * @param oldData old data object, IS UPDATED AND LATER USED
     */
    public void updateThread(ForumThreadEntity newData, ForumThreadEntity oldData) {
        final StringBuilder sql = new StringBuilder("UPDATE threads SET ");
        final List<Object> args = new ArrayList<>();
        if (newData.getTitle() != null) {
            sql.append("title = ?,");
            args.add(newData.getTitle());
            oldData.setTitle(newData.getTitle());
        }
        if (newData.getMessage() != null) {
            sql.append(" message = ?,");
            args.add(newData.getMessage());
            oldData.setMessage(newData.getMessage());
        }
        if (args.isEmpty()) {
            return;
        }
        sql.deleteCharAt(sql.length() - 1);
        sql.append(" WHERE id = ?;");
        args.add(oldData.getId());
        db.update(sql.toString(), args.toArray());
    }

    public void updateVotesForThread(ForumThreadEntity thread) {
        final String sql = "UPDATE threads SET votes = (SELECT SUM(voice) FROM votes WHERE (thread_id) = ?) WHERE id = ?";
        db.update(sql, thread.getId(), thread.getId());
    }
}
