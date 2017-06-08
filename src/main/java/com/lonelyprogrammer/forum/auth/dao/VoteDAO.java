package com.lonelyprogrammer.forum.auth.dao;

import com.lonelyprogrammer.forum.auth.models.entities.ForumThreadEntity;
import com.lonelyprogrammer.forum.auth.models.entities.VoteEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by nikita on 08.06.17.
 */
@Service
@Transactional
public class VoteDAO {
    private final JdbcTemplate db;
    @NotNull
    private final ThreadDAO threadDAO;

    public VoteDAO(JdbcTemplate template, @NotNull ThreadDAO threadDAO) {
        this.db = template;
        this.threadDAO = threadDAO;
    }

    public void createVote(VoteEntity vote, ForumThreadEntity thread){
        final String sql = "INSERT INTO votes (author, thread_id, voice) VALUES(?,?,?) ON CONFLICT(author, thread_id) DO UPDATE SET voice = excluded.voice ;";
        db.update(sql, vote.getNickname(), thread.getId(), vote.getVoice());
        threadDAO.updateVotesForThread(thread);
    }

}
