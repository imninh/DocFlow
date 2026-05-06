package com.group13.reportsystem.model;

import java.time.LocalDateTime;

public class Feedback {
    private Integer feedbackId;
    private Integer reportId;
    private Integer instructorId;
    private String content;
    private LocalDateTime createdAt;

    public Feedback() {
    }

    public Feedback(Integer feedbackId, Integer reportId, Integer instructorId, String content, LocalDateTime createdAt) {
        this.feedbackId = feedbackId;
        this.reportId = reportId;
        this.instructorId = instructorId;
        this.content = content;
        this.createdAt = createdAt;
    }

    public Integer getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(Integer feedbackId) {
        this.feedbackId = feedbackId;
    }

    public Integer getReportId() {
        return reportId;
    }

    public void setReportId(Integer reportId) {
        this.reportId = reportId;
    }

    public Integer getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(Integer instructorId) {
        this.instructorId = instructorId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
