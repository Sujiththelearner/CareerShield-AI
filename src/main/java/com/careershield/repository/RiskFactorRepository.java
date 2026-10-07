package com.careershield.repository;

import com.careershield.entity.RiskAssessment;
import com.careershield.entity.RiskFactor;
import com.careershield.enums.RiskCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RiskFactorRepository extends JpaRepository<RiskFactor, Long> {

    List<RiskFactor> findByRiskAssessment(RiskAssessment riskAssessment);

    List<RiskFactor> findByRiskAssessmentId(Long riskAssessmentId);

    @Query("SELECT rf.category, COUNT(rf) FROM RiskFactor rf GROUP BY rf.category ORDER BY COUNT(rf) DESC")
    List<Object[]> findMostCommonRiskCategories();
}
