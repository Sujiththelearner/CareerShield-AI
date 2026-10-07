package com.careershield.repository;

import com.careershield.entity.Evidence;
import com.careershield.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvidenceRepository extends JpaRepository<Evidence, Long> {

    List<Evidence> findByUserOrderByCreatedAtDesc(User user);

    long countByUser(User user);

    List<Evidence> findByReportedToAdminTrueOrderByCreatedAtDesc();
}
