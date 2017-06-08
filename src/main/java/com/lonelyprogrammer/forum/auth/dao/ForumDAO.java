package com.lonelyprogrammer.forum.auth.dao;

import com.lonelyprogrammer.forum.auth.models.entities.ForumEntity;
import org.jetbrains.annotations.Nullable;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Nikita on 12.03.2017.
 */
@Service
@Transactional
public class ForumDAO {

    private final JdbcTemplate db;
    public ForumDAO(JdbcTemplate template) {
        this.db = template;
    }
    @Nullable
    public ForumEntity getBySlug(String slug){

        ForumEntity loaded = null;
        try{
            final String sql = String.format("SELECT * FROM forums WHERE slug = '%s';", slug);
            loaded = db.queryForObject(sql, forumMapper);
        }
        catch (DataAccessException e){}
        return loaded;
    }
    public void add(ForumEntity data){
        try{
            final String query = "INSERT INTO forums(title, admin, slug) VALUES(?,?,?);";
            db.update(query, data.getTitle(), data.getUser(), data.getSlug());
        }
        catch (DataAccessException e){}
    }


    private static final RowMapper<ForumEntity> forumMapper = (resultSet, rowNum) -> {

        final String title = resultSet.getString("title");
        final String user = resultSet.getString("admin");
        final Integer postsCount = resultSet.getInt("posts");
        final Integer threadsCount = resultSet.getInt("threads");
        final String slug = resultSet.getString("slug");

        return new ForumEntity(title, user, postsCount, threadsCount, slug);
    };
}
