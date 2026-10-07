package com.careershield.entity;

import com.careershield.enums.ScanStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

/**
 * Entity representing an individual internship or job posting scanned by a user.
 */
@Entity
@Table(name = "job_scans", indexes = {
    @Index(name = "idx_scan_user", columnList = "user_id"),
    @Index(name = "idx_scan_created", columnList = "created_at")
})
public class JobScan extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @Column(name = "job_title", nullable = false, length = 200)
    private String jobTitle;

    @Column(name = "company_name", nullable = false, length = 150)
    private String companyName;

    @Column(name = "job_url", length = 500)
    private String jobUrl;

    @Column(name = "recruiter_email", length = 150)
    private String recruiterEmail;

    @Column(name = "salary_info", length = 100)
    private String salaryInfo;

    @Column(name = "location", length = 100)
    private String location;

    @Lob
    @Column(name = "job_description", nullable = false, columnDefinition = "LONGTEXT")
    private String jobDescription;

    @Column(name = "attachment_path", length = 500)
    private String attachmentPath;

    @Enumerated(EnumType.STRING)
    @Column(name = "scan_status", nullable = false, length = 30)
    private ScanStatus scanStatus = ScanStatus.PENDING;

    @OneToOne(mappedBy = "jobScan", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private RiskAssessment riskAssessment;

    public JobScan() {
    }

    public JobScan(User user, String jobTitle, String companyName, String jobUrl,
                   String recruiterEmail, String salaryInfo, String location,
                   String jobDescription, String attachmentPath) {
        this.user = user;
        this.jobTitle = jobTitle;
        this.companyName = companyName;
        this.jobUrl = jobUrl;
        this.recruiterEmail = recruiterEmail;
        this.salaryInfo = salaryInfo;
        this.location = location;
        this.jobDescription = jobDescription;
        this.attachmentPath = attachmentPath;
        this.scanStatus = ScanStatus.PENDING;
    }

    // Getters and Setters
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getJobUrl() {
        return jobUrl;
    }

    public void setJobUrl(String jobUrl) {
        this.jobUrl = jobUrl;
    }

    public String getRecruiterEmail() {
        return recruiterEmail;
    }

    public void setRecruiterEmail(String recruiterEmail) {
        this.recruiterEmail = recruiterEmail;
    }

    public String getSalaryInfo() {
        return salaryInfo;
    }

    public void setSalaryInfo(String salaryInfo) {
        this.salaryInfo = salaryInfo;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public String getAttachmentPath() {
        return attachmentPath;
    }

    public void setAttachmentPath(String attachmentPath) {
        this.attachmentPath = attachmentPath;
    }

    public ScanStatus getScanStatus() {
        return scanStatus;
    }

    public void setScanStatus(ScanStatus scanStatus) {
        this.scanStatus = scanStatus;
    }

    public RiskAssessment getRiskAssessment() {
        return riskAssessment;
    }

    public void setRiskAssessment(RiskAssessment riskAssessment) {
        this.riskAssessment = riskAssessment;
        if (riskAssessment != null) {
            riskAssessment.setJobScan(this);
        }
    }
}
