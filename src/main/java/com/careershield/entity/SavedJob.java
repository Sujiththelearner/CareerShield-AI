package com.careershield.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

/**
 * Entity for bookmarked or saved genuine postings.
 */
@Entity
@Table(name = "saved_jobs", indexes = {
    @Index(name = "idx_saved_user", columnList = "user_id")
})
public class SavedJob extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "job_scan_id", nullable = false)
    private JobScan jobScan;

    @Column(name = "notes", length = 500)
    private String notes;

    public SavedJob() {
    }

    public SavedJob(User user, JobScan jobScan, String notes) {
        this.user = user;
        this.jobScan = jobScan;
        this.notes = notes;
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public java.time.LocalDateTime getSavedAt() {
        return getCreatedAt();
    }
}
