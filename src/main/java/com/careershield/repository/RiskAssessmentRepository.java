package com.careershield.repository;

import com.careershield.entity.JobScan;
import com.careershield.entity.RiskAssessment;
import com.careershield.entity.User;
import com.careershield.enums.RiskLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RiskAssessmentRepository extends JpaRepository<RiskAssessment, Long> {

    Optional<RiskAssessment> findByJobScan(JobScan jobScan);

    Optional<RiskAssessment> findByJobScanId(Long jobScanId);

    @Query("SELECT COUNT(ra) FROM RiskAssessment ra WHERE ra.jobScan.user = :user AND ra.riskLevel = :level")
    long countByUserAndRiskLevel(@Param("user") User user, @Param("level") RiskLevel level);

    long countByRiskLevel(RiskLevel level);

    @Query("SELECT AVG(ra.overallSafetyScore) FROM RiskAssessment ra")
    Double calculateAverageSafetyScore();
}
