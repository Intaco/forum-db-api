package com.lonelyprogrammer.forum.auth.dao;

import com.lonelyprogrammer.forum.auth.models.entities.ForumThreadEntity;
import com.lonelyprogrammer.forum.auth.utils.TimeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

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
        final String created = TimeUtil.stringFromTimestamp(resultSet.getTimestamp("created"));
        return new ForumThreadEntity(author, created, forum, id, message, slug, title, votes);
    };
    static final String INSERT_THREAD_SQL = "INSERT INTO threads(title, author, forum, message) VALUES(?,?,?,?) RETURNING id;";
    static final String UPDATE_CREATED_SQL = "UPDATE threads SET created = ? WHERE id = ? ;";
    static final String UPDATE_SLUG_SQL = "UPDATE threads SET slug = ? WHERE id = ? ;";
    static final String INCREMENT_THREADS_COUNT_SQL = "UPDATE forums SET threads = threads + 1 WHERE slug = ? ;";

    @Nullable
    public ForumThreadEntity add(ForumThreadEntity thread) {


        final int id = db.queryForObject(INSERT_THREAD_SQL, Integer.class, thread.getTitle(), thread.getAuthor(), thread.getForum(), thread.getMessage());
        db.update(INCREMENT_THREADS_COUNT_SQL, thread.getForum());

        if (thread.getCreated() != null) {
            db.update(UPDATE_CREATED_SQL, TimeUtil.timestampFromString(thread.getCreated()), id);
        }

        if (thread.getSlug() != null) {
            db.update(UPDATE_SLUG_SQL, thread.getSlug(), id);
        }
        return getById(id);
    }

    @Nullable
    public ForumThreadEntity getById(int id) {

        ForumThreadEntity loaded = null;
        try {
            final String query = "SELECT * FROM threads WHERE id = ?";
            loaded = db.queryForObject(query, threadMapper, id);
        } catch (DataAccessException e) {

        }
        return loaded;
    }

    @Nullable
    public ForumThreadEntity getBySlug(String slug) {

        ForumThreadEntity loaded = null;
        try {
            final String query = "SELECT * FROM threads WHERE slug = ?::CITEXT";
            loaded = db.queryForObject(query, threadMapper, slug);
        } catch (DataAccessException e) {

        }
        return loaded;
    }

    public int getCount() {
        final String sql = "SELECT COUNT(id) FROM threads ;";
        return db.queryForObject(sql, Integer.class);
    }

    @NotNull
    public List<ForumThreadEntity> getByForum(String slug, Integer limit, @Nullable Timestamp time, boolean desc) {
        final StringBuilder builder = new StringBuilder("SELECT * FROM threads WHERE forum = ?::CITEXT ");
        final List<Object> args = new ArrayList<>();
        args.add(slug);
        if (time != null) {
            if (desc) builder.append("AND created <= ?");
            else builder.append("AND created >= ?");
            args.add(time);
        }
        if (desc) {
            builder.append("ORDER BY created DESC ");
        } else builder.append("ORDER BY created ");

        builder.append("LIMIT ?;");
        args.add(limit);
        final String query = builder.toString();

        List<ForumThreadEntity> threads = new ArrayList<>();
        try {
            threads = db.query(query, threadMapper, args.toArray());
        } catch (DataAccessException e) {
            //e.printStackTrace();
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
