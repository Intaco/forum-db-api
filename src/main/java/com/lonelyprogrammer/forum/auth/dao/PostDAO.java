package com.lonelyprogrammer.forum.auth.dao;

import com.lonelyprogrammer.forum.auth.models.entities.ForumThreadEntity;
import com.lonelyprogrammer.forum.auth.models.entities.PostEntity;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nikita on 27.05.17.
 */
@Service
@Transactional
public class PostDAO {
    private final JdbcTemplate db;
    private static Logger logger = LoggerFactory.getLogger(PostDAO.class);

    public PostDAO(JdbcTemplate db) {
        this.db = db;
    }

    public List<Integer> getThreadChildren(final Integer threadId) {
        final String sql = "SELECT id FROM posts WHERE thread_id=?;";
        return db.queryForList(sql, Integer.class, threadId);
    }

    public void createPosts(List<PostEntity> posts, ForumThreadEntity thread) {
        final String sql = "INSERT INTO posts(parent, author, message, thread_id, forum, created, post_path) VALUES(?,?,?,?,?,?,array_append((SELECT post_path FROM posts WHERE id = ?), currval('posts_id_seq')::INT));";
        try (Connection connection = db.getDataSource().getConnection()) {
            final PreparedStatement prepStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            final String timeStr = timestamp.toInstant().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            for (PostEntity post : posts) {

                prepStatement.setInt(1, post.getParent());
                prepStatement.setString(2, post.getAuthor());
                prepStatement.setString(3, post.getMessage());
                prepStatement.setInt(4, thread.getId());
                prepStatement.setString(5, thread.getForum());
                if (post.getCreated() != null) {
                    prepStatement.setTimestamp(6, new Timestamp(ZonedDateTime.parse(post.getCreated()).toInstant().toEpochMilli()));
                } else {
                    post.setCreated(timeStr);
                    prepStatement.setTimestamp(6, timestamp);
                }
                prepStatement.setInt(7, post.getParent());

                prepStatement.addBatch();
                post.setThread(thread.getId());
                post.setForum(thread.getForum());
                post.setCreated(timeStr);
            }
            prepStatement.executeBatch();
            final ResultSet rs = prepStatement.getGeneratedKeys();
            for (int i = 0; rs.next(); i++)
                posts.get(i).setId(rs.getInt(1));
            prepStatement.close();
        } catch (SQLException e) {
            logger.error("1 error: ", e);
            logger.error("2 error: ", e.getNextException());
        }
        ///////////////////////////////////
        final String forumUpdateSql = "UPDATE forums SET posts = posts + ? WHERE slug = ?;";
        db.update(forumUpdateSql, posts.size(), thread.getForum());
        ///////////////////////////////////
    }

    public List<PostEntity> getPostsFlat(Integer id, Integer limit, Integer offset, boolean desc) {
        final String descOrAsc = (desc ? "DESC " : "ASC ");
        final String sql = "SELECT * FROM posts WHERE thread_id = ? ORDER BY created " + descOrAsc + ", id " + descOrAsc + "LIMIT ? OFFSET ?;";
        final List<PostEntity> loadedPosts = db.query(sql, postMapper, id, limit, offset);

        return loadedPosts;

    }

    public List<PostEntity> getPostsTree(Integer id, Integer limit, Integer offset, boolean desc) {
        final String sql = "SELECT * FROM posts WHERE thread_id = ? ORDER BY post_path " + (desc ? "DESC " : "ASC ") + "LIMIT ? OFFSET ?;";
        final List<PostEntity> loadedPosts = db.query(sql, postMapper, id, limit, offset);

        return loadedPosts;
    }

    public List<PostEntity> getPostsParentTree(Integer id, boolean desc, List<Integer> parentIds) {
        final String descOrAsc = (desc ? "DESC " : "ASC ");
        final String sql = "SELECT * FROM posts WHERE thread_id  = ? AND post_path[1] = ? ORDER BY post_path " + descOrAsc + " , id " + descOrAsc + ";";
        final List<PostEntity> loadedPosts = new ArrayList<>();
        for (Integer parent : parentIds) {
            loadedPosts.addAll(db.query(sql, postMapper, id, parent));
        }

        return loadedPosts;
    }

    public List<Integer> getParentIds(Integer id, Integer limit, boolean desc, Integer offset) {
        final String sql = "SELECT id from posts where parent = 0 AND thread_id = ? ORDER BY id " + (desc ? "DESC " : "ASC ") + " LIMIT ? OFFSET ?;";
        return db.query(sql, parentMapper, id, limit, offset);
    }
    @Nullable
    public PostEntity getById(Integer id){
        try{
            final String sql = "SELECT * FROM posts WHERE id = ?;";
            return db.queryForObject(sql, postMapper, id);
        } catch (DataAccessException e){
            e.printStackTrace();
            return null;
        }
    }

    private static final RowMapper<PostEntity> postMapper = (rs, num) -> {
        final int id = rs.getInt("id");
        final String created = rs.getTimestamp("created").toInstant().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        final int parent = rs.getInt("parent");
        final String message = rs.getString("message");
        final String author = rs.getString("author");
        final String forum = rs.getString("forum");
        final boolean isEdited = rs.getBoolean("isEdited");
        final int thread = rs.getInt("thread_id");
        return new PostEntity(id, created, parent, message, author, forum, isEdited, thread);
    };
    private static final RowMapper<Integer> parentMapper = (rs, num) -> rs.getInt("id");
}
