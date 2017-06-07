package com.lonelyprogrammer.forum.auth.dao;

import com.lonelyprogrammer.forum.auth.models.entities.ForumThreadEntity;
import com.lonelyprogrammer.forum.auth.models.entities.PostEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by nikita on 27.05.17.
 */
@Service
@Transactional
public class PostDAO {
    private final JdbcTemplate db;

    public PostDAO(JdbcTemplate db) {
        this.db = db;
    }

    public List<Integer> getThreadChildren(final Integer threadId) {
        final String sql = "SELECT id FROM posts WHERE thread_id=?;";
        return db.queryForList(sql, Integer.class, threadId);
    }

    public void createPosts(List<PostEntity> posts, ForumThreadEntity thread){
        final String sql = "INSERT INTO posts(id, parent, author, message, thread_id, forum, created, post_path) VALUES(?,?,?,?,?,?,?,array_append((SELECT post_path FROM post WHERE id = ?), ?));";
        try(Connection connection = db.getDataSource().getConnection()) {
            final PreparedStatement prepStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            final String timeStr = timestamp.toInstant().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            for (PostEntity post: posts){

                prepStatement.setInt(1, post.getId());
                prepStatement.setInt(2, post.getParent());
                prepStatement.setString(3, post.getAuthor());
                prepStatement.setString(4, post.getMessage());
                prepStatement.setInt(5, thread.getId());
                prepStatement.setString(6, thread.getForum());
                if(post.getCreated()!=null) {
                    prepStatement.setTimestamp(7, new Timestamp(ZonedDateTime.parse(post.getCreated()).toInstant().toEpochMilli()));
                } else{
                    post.setCreated(timeStr);
                    prepStatement.setTimestamp(7, timestamp);
                }

                prepStatement.setInt(8, post.getParent());
                prepStatement.setInt(9, post.getId());


                prepStatement.addBatch();
/*                post.setThread(thread.getId());
                post.setForum(thread.getForum());*/
                post.setCreated(timeStr);
            }
            prepStatement.executeBatch();
            final ResultSet rs = prepStatement.getGeneratedKeys();
            for (int i = 0; rs.next(); i++)
                posts.get(i).setId(rs.getInt(1));
            prepStatement.close();
        } catch (SQLException e){

        }
    }

    private final RowMapper<PostEntity> postMapper = (rs, num) -> {
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
}
