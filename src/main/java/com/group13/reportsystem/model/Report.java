package com.group13.reportsystem.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Report {
    private Integer reportId;
    private Integer studentId;
    private Integer instructorId;
    private String title;
    private String filePath;
    private String originalFileName;
    private String status;
    private Integer parentReportId;
    private LocalDateTime submittedAt;
    private LocalDateTime updatedAt;
    private String studentName;
    private String instructorName;
    private String latestFeedback;
    private String studentClass;
    private List<ReferenceItem> references = new ArrayList<>();

    public Report() {
    }

    public Integer getReportId() {
        return reportId;
    }

    public void setReportId(Integer reportId) {
        this.reportId = reportId;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public Integer getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(Integer instructorId) {
        this.instructorId = instructorId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public Integer getParentReportId() {
        return parentReportId;
    }

    public void setParentReportId(Integer parentReportId) {
        this.parentReportId = parentReportId;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getInstructorName() {
        return instructorName;
    }

    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
    }

    public String getLatestFeedback() {
        return latestFeedback;
    }

    public void setLatestFeedback(String latestFeedback) {
        this.latestFeedback = latestFeedback;
    }

    public String getStudentClass() {
        return studentClass;
    }

    public void setStudentClass(String studentClass) {
        this.studentClass = studentClass;
    }

    public List<ReferenceItem> getReferences() {
        return references;
    }

    public void setReferences(List<ReferenceItem> references) {
        this.references = references;
    }
}
