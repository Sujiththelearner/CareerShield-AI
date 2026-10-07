package com.careershield.repository;

import com.careershield.entity.JobScan;
import com.careershield.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobScanRepository extends JpaRepository<JobScan, Long> {

    List<JobScan> findByUserOrderByCreatedAtDesc(User user);

    Page<JobScan> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    List<JobScan> findTop5ByUserOrderByCreatedAtDesc(User user);

    long countByUser(User user);

    @Query("SELECT js FROM JobScan js WHERE js.user = :user AND " +
           "(LOWER(js.jobTitle) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           " LOWER(js.companyName) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<JobScan> searchUserScans(@Param("user") User user, @Param("query") String query);

    List<JobScan> findTop10ByOrderByCreatedAtDesc();
}
