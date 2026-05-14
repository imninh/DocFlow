package com.group13.reportsystem.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public class ReviewForm {
    @NotNull
    private Integer reportId;

    @NotBlank
    private String decision;

    @NotBlank
    private String feedbackContent;

    private MultipartFile feedbackFile;

    public Integer getReportId() {
        return reportId;
    }

    public void setReportId(Integer reportId) {
        this.reportId = reportId;
    }

    public String getDecision() {
        return decision;
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }

    public String getFeedbackContent() {
        return feedbackContent;
    }

    public void setFeedbackContent(String feedbackContent) {
        this.feedbackContent = feedbackContent;
    }

    public MultipartFile getFeedbackFile() {
        return feedbackFile;
    }

    public void setFeedbackFile(MultipartFile feedbackFile) {
        this.feedbackFile = feedbackFile;
    }
}
