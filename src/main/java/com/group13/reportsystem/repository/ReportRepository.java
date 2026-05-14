package com.group13.reportsystem.repository;

import com.group13.reportsystem.model.Report;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class ReportRepository {
    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Report> reportRowMapper = (rs, rowNum) -> {
        Report report = new Report();
        report.setReportId(rs.getInt("report_id"));
        report.setStudentId(rs.getInt("student_id"));
        report.setInstructorId(rs.getInt("instructor_id"));
        report.setTitle(rs.getString("title"));
        report.setFilePath(rs.getString("file_path"));
        report.setOriginalFileName(rs.getString("original_file_name"));
        report.setStatus(rs.getString("status"));
        int parentReportId = rs.getInt("parent_report_id");
        report.setParentReportId(rs.wasNull() ? null : parentReportId);
        Timestamp submittedAt = rs.getTimestamp("submitted_at");
        if (submittedAt != null) {
            report.setSubmittedAt(submittedAt.toLocalDateTime());
        }
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            report.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        report.setStudentName(rs.getString("student_name"));
        report.setInstructorName(rs.getString("instructor_name"));
        report.setLatestFeedback(rs.getString("latest_feedback"));
        report.setStudentClass(rs.getString("student_class"));
        return report;
    };

    public ReportRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Report> findByStudentId(Integer studentId) {
        return jdbcTemplate.query(baseSelect() + """
                WHERE r.student_id = ?
                ORDER BY r.submitted_at DESC
                """, reportRowMapper, studentId);
    }

    public List<Report> findQueueByInstructorId(Integer instructorId) {
        return jdbcTemplate.query(baseSelect() + """
                WHERE r.instructor_id = ?
                  AND r.status IN ('pending', 'under_review')
                ORDER BY r.submitted_at DESC
                """, reportRowMapper, instructorId);
    }

    public List<Report> findReviewedByInstructorId(Integer instructorId) {
        return jdbcTemplate.query(baseSelect() + """
                WHERE r.instructor_id = ?
                  AND r.status IN ('approved', 'rejected')
                ORDER BY r.updated_at DESC
                """, reportRowMapper, instructorId);
    }

    public List<Report> findAll() {
        return jdbcTemplate.query(baseSelect() + " ORDER BY r.submitted_at DESC", reportRowMapper);
    }

    public Report findById(Integer reportId) {
        List<Report> reports = jdbcTemplate.query(baseSelect() + " WHERE r.report_id = ?", reportRowMapper, reportId);
        return reports.isEmpty() ? null : reports.get(0);
    }

    public Integer insert(Report report) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO reports (
                        student_id, instructor_id, title, file_path, original_file_name,
                        status, parent_report_id, submitted_at, updated_at
                    ) VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
                    """, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, report.getStudentId());
            statement.setInt(2, report.getInstructorId());
            statement.setString(3, report.getTitle());
            statement.setString(4, report.getFilePath());
            statement.setString(5, report.getOriginalFileName());
            statement.setString(6, report.getStatus());
            if (report.getParentReportId() == null) {
                statement.setNull(7, java.sql.Types.INTEGER);
            } else {
                statement.setInt(7, report.getParentReportId());
            }
            return statement;
        }, keyHolder);
        return keyHolder.getKey() == null ? null : keyHolder.getKey().intValue();
    }

    public void updateReview(Integer reportId, String status) {
        jdbcTemplate.update("""
                UPDATE reports
                SET status = ?, updated_at = CURRENT_TIMESTAMP
                WHERE report_id = ?
                """, status, reportId);
    }

    public void delete(Integer reportId) {
        jdbcTemplate.update("DELETE FROM reports WHERE report_id = ?", reportId);
    }

    public void update(Integer reportId, String title) {
        jdbcTemplate.update("""
                UPDATE reports
                SET title = ?, updated_at = CURRENT_TIMESTAMP
                WHERE report_id = ?
                """, title, reportId);
    }

    public long countByInstructorAndStatus(Integer instructorId, String status) {
        Long count = jdbcTemplate.queryForObject("""
                SELECT COUNT(*) FROM reports
                WHERE instructor_id = ? AND status = ?
                """, Long.class, instructorId, status);
        return count == null ? 0 : count;
    }

    public long countByInstructor(Integer instructorId) {
        Long count = jdbcTemplate.queryForObject("""
                SELECT COUNT(*) FROM reports
                WHERE instructor_id = ?
                """, Long.class, instructorId);
        return count == null ? 0 : count;
    }

    private String baseSelect() {
        return """
                SELECT r.report_id, r.student_id, r.instructor_id, r.title, r.file_path, r.original_file_name,
                       r.status, r.parent_report_id, r.submitted_at, r.updated_at,
                       s.full_name AS student_name,
                       i.full_name AS instructor_name,
                        (
                           SELECT f.content
                           FROM feedbacks f
                           WHERE f.report_id = r.report_id
                           ORDER BY f.created_at DESC
                           LIMIT 1
                       ) AS latest_feedback,
                       s.class_code AS student_class
                FROM reports r
                JOIN users s ON s.user_id = r.student_id
                JOIN users i ON i.user_id = r.instructor_id
                """;
    }
}
