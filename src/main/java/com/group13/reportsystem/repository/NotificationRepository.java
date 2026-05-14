package com.group13.reportsystem.repository;

import com.group13.reportsystem.model.Notification;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public class NotificationRepository {
    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Notification> notificationRowMapper = (rs, rowNum) -> {
        Notification notification = new Notification();
        notification.setNotificationId(rs.getInt("notification_id"));
        notification.setUserId(rs.getInt("user_id"));
        int reportId = rs.getInt("report_id");
        notification.setReportId(rs.wasNull() ? null : reportId);
        notification.setMessage(rs.getString("message"));
        notification.setRead(rs.getBoolean("is_read"));
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            notification.setCreatedAt(createdAt.toLocalDateTime());
        }
        return notification;
    };

    public NotificationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Notification> findByUserId(Integer userId) {
        return jdbcTemplate.query("""
                SELECT * FROM notifications
                WHERE user_id = ?
                ORDER BY created_at DESC
                """, notificationRowMapper, userId);
    }

    public void insert(Integer userId, Integer reportId, String message) {
        jdbcTemplate.update("""
                INSERT INTO notifications (user_id, report_id, message, is_read, created_at)
                VALUES (?, ?, ?, FALSE, CURRENT_TIMESTAMP)
                """, userId, reportId, message);
    }

    public void deleteByReportId(Integer reportId) {
        jdbcTemplate.update("DELETE FROM notifications WHERE report_id = ?", reportId);
    }
}
