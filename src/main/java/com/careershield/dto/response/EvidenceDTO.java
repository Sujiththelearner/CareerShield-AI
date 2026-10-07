package com.careershield.dto.response;

import java.time.LocalDateTime;

public class EvidenceDTO {

    private Long id;
    private Long scanId;
    private String title;
    private String companyName;
    private String evidenceNotes;
    private String filePath;
    private boolean reportedToAdmin;
    private LocalDateTime createdAt;

    public EvidenceDTO() {
    }

    public EvidenceDTO(Long id, Long scanId, String title, String companyName,
                       String evidenceNotes, String filePath, boolean reportedToAdmin,
                       LocalDateTime createdAt) {
        this.id = id;
        this.scanId = scanId;
        this.title = title;
        this.companyName = companyName;
        this.evidenceNotes = evidenceNotes;
        this.filePath = filePath;
        this.reportedToAdmin = reportedToAdmin;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getScanId() {
        return scanId;
    }

    public void setScanId(Long scanId) {
        this.scanId = scanId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getEvidenceNotes() {
        return evidenceNotes;
    }

    public void setEvidenceNotes(String evidenceNotes) {
        this.evidenceNotes = evidenceNotes;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public boolean isReportedToAdmin() {
        return reportedToAdmin;
    }

    public void setReportedToAdmin(boolean reportedToAdmin) {
        this.reportedToAdmin = reportedToAdmin;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
