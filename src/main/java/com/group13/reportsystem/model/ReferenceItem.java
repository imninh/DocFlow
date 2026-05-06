package com.group13.reportsystem.model;

import java.time.LocalDateTime;

public class ReferenceItem {
    private Integer referenceId;
    private Integer reportId;
    private String citationText;
    private String url;
    private LocalDateTime addedAt;

    public ReferenceItem() {
    }

    public ReferenceItem(Integer referenceId, Integer reportId, String citationText, String url, LocalDateTime addedAt) {
        this.referenceId = referenceId;
        this.reportId = reportId;
        this.citationText = citationText;
        this.url = url;
        this.addedAt = addedAt;
    }

    public Integer getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Integer referenceId) {
        this.referenceId = referenceId;
    }

    public Integer getReportId() {
        return reportId;
    }

    public void setReportId(Integer reportId) {
        this.reportId = reportId;
    }

    public String getCitationText() {
        return citationText;
    }

    public void setCitationText(String citationText) {
        this.citationText = citationText;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public LocalDateTime getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(LocalDateTime addedAt) {
        this.addedAt = addedAt;
    }
}
