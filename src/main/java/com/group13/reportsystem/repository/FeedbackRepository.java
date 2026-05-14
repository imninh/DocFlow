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

    public void insertWithFile(Integer reportId, Integer instructorId, String content, String filePath, String originalFileName) {
        jdbcTemplate.update("""
                INSERT INTO feedbacks (report_id, instructor_id, content, file_path, original_file_name, created_at)
                VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
                """, reportId, instructorId, content, filePath, originalFileName);
    }

    public void update(Integer feedbackId, String content) {
        jdbcTemplate.update("""
                UPDATE feedbacks
                SET content = ?
                WHERE feedback_id = ?
                """, content, feedbackId);
    }

    public void delete(Integer feedbackId) {
        jdbcTemplate.update("DELETE FROM feedbacks WHERE feedback_id = ?", feedbackId);
    }

    public void deleteByReportId(Integer reportId) {
        jdbcTemplate.update("DELETE FROM feedbacks WHERE report_id = ?", reportId);
    }
}
