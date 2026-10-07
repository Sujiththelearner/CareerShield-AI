package com.careershield.service;

import com.careershield.dto.response.EvidenceDTO;
import com.careershield.entity.Evidence;
import com.careershield.entity.JobScan;
import com.careershield.entity.User;
import com.careershield.exception.ResourceNotFoundException;
import com.careershield.repository.EvidenceRepository;
import com.careershield.repository.JobScanRepository;
import com.careershield.repository.UserRepository;
import com.careershield.util.FileUploadUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service managing the student "Evidence Vault" feature.
 * Allows storing forensic notes, offer letter PDFs, and fraudulent chat screenshots.
 */
@Service
public class EvidenceService {

    private final EvidenceRepository evidenceRepository;
    private final UserRepository userRepository;
    private final JobScanRepository jobScanRepository;

    @Value("${careershield.upload.dir:uploads/evidence}")
    private String uploadDir;

    public EvidenceService(EvidenceRepository evidenceRepository,
                           UserRepository userRepository,
                           JobScanRepository jobScanRepository) {
        this.evidenceRepository = evidenceRepository;
        this.userRepository = userRepository;
        this.jobScanRepository = jobScanRepository;
    }

    @Transactional
    public EvidenceDTO saveEvidence(Long userId, Long scanId, String title,
                                    String companyName, String evidenceNotes,
                                    MultipartFile file) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        JobScan scan = null;
        if (scanId != null) {
            scan = jobScanRepository.findById(scanId).orElse(null);
            if (companyName == null && scan != null) {
                companyName = scan.getCompanyName();
            }
        }

        String storedFileName = null;
        if (file != null && !file.isEmpty()) {
            storedFileName = FileUploadUtil.saveFile(uploadDir, file);
        }

        Evidence evidence = new Evidence(user, scan, title, companyName, evidenceNotes, storedFileName);
        Evidence saved = evidenceRepository.save(evidence);

        return mapToDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<EvidenceDTO> getUserEvidence(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        return evidenceRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EvidenceDTO getEvidenceById(Long id) {
        Evidence ev = evidenceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evidence item not found with ID: " + id));
        return mapToDTO(ev);
    }

    @Transactional
    public void deleteEvidence(Long id, Long userId) {
        Evidence ev = evidenceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evidence not found with ID: " + id));
        evidenceRepository.delete(ev);
    }

    @Transactional
    public EvidenceDTO toggleReportToAdmin(Long id) {
        Evidence ev = evidenceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evidence not found with ID: " + id));
        ev.setReportedToAdmin(!ev.isReportedToAdmin());
        Evidence updated = evidenceRepository.save(ev);
        return mapToDTO(updated);
    }

    @Transactional(readOnly = true)
    public List<EvidenceDTO> getAllReportedEvidence() {
        return evidenceRepository.findByReportedToAdminTrueOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private EvidenceDTO mapToDTO(Evidence ev) {
        return new EvidenceDTO(
                ev.getId(),
                ev.getJobScan() != null ? ev.getJobScan().getId() : null,
                ev.getTitle(),
                ev.getCompanyName(),
                ev.getEvidenceNotes(),
                ev.getFilePath(),
                ev.isReportedToAdmin(),
                ev.getCreatedAt()
        );
    }
}
