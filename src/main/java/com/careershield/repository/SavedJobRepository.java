package com.careershield.repository;

import com.careershield.entity.JobScan;
import com.careershield.entity.SavedJob;
import com.careershield.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavedJobRepository extends JpaRepository<SavedJob, Long> {

    List<SavedJob> findByUserOrderByCreatedAtDesc(User user);

    Optional<SavedJob> findByUserAndJobScan(User user, JobScan jobScan);

    boolean existsByUserAndJobScan(User user, JobScan jobScan);

    long countByUser(User user);
}
