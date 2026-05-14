package com.group13.reportsystem.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class SubmissionForm {
    @NotBlank
    private String title;

    @NotBlank
    private String referencesText;

    @NotNull
    private Integer instructorId;

    @NotNull
    private MultipartFile reportFile;

    private List<MultipartFile> referenceFiles;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReferencesText() {
        return referencesText;
    }

    public void setReferencesText(String referencesText) {
        this.referencesText = referencesText;
    }

    public Integer getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(Integer instructorId) {
        this.instructorId = instructorId;
    }

    public MultipartFile getReportFile() {
        return reportFile;
    }

    public void setReportFile(MultipartFile reportFile) {
        this.reportFile = reportFile;
    }

    public List<MultipartFile> getReferenceFiles() {
        return referenceFiles;
    }

    public void setReferenceFiles(List<MultipartFile> referenceFiles) {
        this.referenceFiles = referenceFiles;
    }
}
