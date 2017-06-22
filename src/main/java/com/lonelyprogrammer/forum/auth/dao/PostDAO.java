package com.lonelyprogrammer.forum.auth.dao;

import com.lonelyprogrammer.forum.auth.models.Pair;
import com.lonelyprogrammer.forum.auth.models.entities.ForumThreadEntity;
import com.lonelyprogrammer.forum.auth.models.entities.PostEntity;
import com.lonelyprogrammer.forum.auth.utils.TimeUtil;
import org.jetbrains.annotations.Nullable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nikita on 27.05.17.
 */
@Service
@Transactional
public class PostDAO {
    private final JdbcTemplate db;
    //private static Logger logger = LoggerFactory.getLogger(PostDAO.class);

    public PostDAO(JdbcTemplate db) {
        this.db = db;
    }

    public List<Pair<Integer, Integer[]>> getThreadChildren(final Integer threadId) {
        final String sql = "SELECT id, post_path FROM posts WHERE thread_id=?;";
        return db.query(sql, parentWithpathMapper, threadId);
    }

    static final String INSERT_POST_SQL = "INSERT INTO posts(id,parent, author, message, thread_id, forum, created, post_path) VALUES(?,?,?,?,?,?,?,array_append(?, ?));";
    static final String NEXT_POST_ID_SQL = "SELECT nextval('posts_id_seq') from generate_series(1, ?);";
    static final String FORUM_UPDATE_POSTS_SQL = "UPDATE forums SET posts = posts + ? WHERE slug = ?;";
    static final String UPDATE_FORUM_USERS_SQL = "INSERT INTO forum_users (author, forum) VALUES (?, ?);";

    public void createPosts(List<PostEntity> posts, ForumThreadEntity thread, List<Integer[]> paths) throws SQLException {
        try (Connection connection = db.getDataSource().getConnection()) {
            final PreparedStatement prepStatement = connection.prepareStatement(INSERT_POST_SQL, Statement.NO_GENERATED_KEYS);
            final PreparedStatement updateForumsStatement = connection.prepareStatement(UPDATE_FORUM_USERS_SQL, Statement.NO_GENERATED_KEYS);
            final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            final String timeStr = TimeUtil.stringFromTimestamp(timestamp);
            final List<Integer> ids = db.queryForList(NEXT_POST_ID_SQL, Integer.class, posts.size());
            int i = 0;
            int id;
            for (PostEntity post : posts) {
                if (paths.get(i) == null) {
                    prepStatement.setArray(8, null);
                } else prepStatement.setArray(8, connection.createArrayOf("int4", paths.get(i)));
                id = ids.get(i++);
                post.setId(id);
                prepStatement.setInt(1, id);
                prepStatement.setInt(2, post.getParent());
                prepStatement.setString(3, post.getAuthor());
                prepStatement.setString(4, post.getMessage());
                prepStatement.setInt(5, thread.getId());
                prepStatement.setString(6, thread.getForum());
                if (post.getCreated() != null) {
                    prepStatement.setTimestamp(7, TimeUtil.timestampFromString(post.getCreated()));
                } else {
                    post.setCreated(timeStr);
                    prepStatement.setTimestamp(7, timestamp);
                }
                post.setThread(thread.getId());
                post.setForum(thread.getForum());
                post.setCreated(timeStr);
                prepStatement.setInt(9, id);
                updateForumsStatement.setString(1, post.getAuthor());
                updateForumsStatement.setString(2, post.getForum());
                updateForumsStatement.addBatch();
                /*db.queryForObject(ADD_FORUM_USERS_SQL, Object.class, post.getForum(),post.getAuthor());*/
                prepStatement.addBatch();

            }
            prepStatement.executeBatch();
            updateForumsStatement.executeBatch();
            updateForumsStatement.close();

            prepStatement.close();


        } catch (SQLException e) {
/*            logger.error("1 error: ", e);
            logger.error("2 error: ", e.getNextException());*/
            throw e;
        }
        ///////////////////////////////////
        db.update(FORUM_UPDATE_POSTS_SQL, posts.size(), thread.getForum());
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
    public PostEntity getById(int id) {
        try {
            final String sql = "SELECT * FROM posts WHERE id = ?;";
            return db.queryForObject(sql, postMapper, id);
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }
    }

    public int getCount() {
        final String sql = "SELECT COUNT(id) FROM posts ;";
        return db.queryForObject(sql, Integer.class);
    }

    public void updatePost(PostEntity post) {
        final String sql = "UPDATE posts SET message = ?, isEdited = TRUE WHERE id = ?;";
        db.update(sql, post.getMessage(), post.getId());
    }

    private static final RowMapper<PostEntity> postMapper = (rs, num) -> new PostEntity(rs.getInt("id"),
            TimeUtil.stringFromTimestamp(rs.getTimestamp("created")),
            rs.getInt("parent"), rs.getString("message"), rs.getString("author"),
            rs.getString("forum"), rs.getBoolean("isEdited"), rs.getInt("thread_id"));
    private static final RowMapper<Integer> parentMapper = (rs, num) -> rs.getInt("id");
    private static final RowMapper<Pair<Integer, Integer[]>> parentWithpathMapper = (rs, num) -> new Pair(rs.getInt("id"), (Integer[]) rs.getArray("post_path").getArray());
}
