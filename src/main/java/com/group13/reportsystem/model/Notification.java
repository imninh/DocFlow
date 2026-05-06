package com.group13.reportsystem.model;

import java.time.LocalDateTime;

public class Notification {
    private Integer notificationId;
    private Integer userId;
    private Integer reportId;
    private String message;
    private boolean read;
    private LocalDateTime createdAt;

    public Notification() {
    }

    public Notification(Integer notificationId, Integer userId, Integer reportId, String message, boolean read,
                        LocalDateTime createdAt) {
        this.notificationId = notificationId;
        this.userId = userId;
        this.reportId = reportId;
        this.message = message;
        this.read = read;
        this.createdAt = createdAt;
    }

    public Integer getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Integer notificationId) {
        this.notificationId = notificationId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getReportId() {
        return reportId;
    }

    public void setReportId(Integer reportId) {
        this.reportId = reportId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
