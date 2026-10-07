package com.careershield.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

/**
 * Entity for the "Evidence Vault" feature where students store forensic
 * proof (notes, screenshots, fraudulent offer letters) for reporting.
 */
@Entity
@Table(name = "evidence", indexes = {
    @Index(name = "idx_evidence_user", columnList = "user_id")
})
public class Evidence extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_scan_id")
    private JobScan jobScan;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "company_name", length = 150)
    private String companyName;

    @Lob
    @Column(name = "evidence_notes", columnDefinition = "LONGTEXT")
    private String evidenceNotes;

    @Column(name = "file_path", length = 500)
    private String filePath;

    @Column(name = "is_reported_to_admin", nullable = false)
    private boolean reportedToAdmin = false;

    public Evidence() {
    }

    public Evidence(User user, JobScan jobScan, String title, String companyName,
                    String evidenceNotes, String filePath) {
        this.user = user;
        this.jobScan = jobScan;
        this.title = title;
        this.companyName = companyName;
        this.evidenceNotes = evidenceNotes;
        this.filePath = filePath;
        this.reportedToAdmin = false;
    }

    // Getters and Setters
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public JobScan getJobScan() {
        return jobScan;
    }

    public void setJobScan(JobScan jobScan) {
        this.jobScan = jobScan;
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
}
