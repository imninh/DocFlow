package com.group13.reportsystem.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class FeedbackRepository {
    private final JdbcTemplate jdbcTemplate;

    public FeedbackRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void insert(Integer reportId, Integer instructorId, String content) {
        jdbcTemplate.update("""
                INSERT INTO feedbacks (report_id, instructor_id, content, created_at)
                VALUES (?, ?, ?, CURRENT_TIMESTAMP)
                """, reportId, instructorId, content);
    }
}
