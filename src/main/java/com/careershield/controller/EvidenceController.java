package com.careershield.controller;

import com.careershield.dto.response.ApiResponse;
import com.careershield.dto.response.EvidenceDTO;
import com.careershield.service.EvidenceService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/evidence")
public class EvidenceController {

    private final EvidenceService evidenceService;

    public EvidenceController(EvidenceService evidenceService) {
        this.evidenceService = evidenceService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<EvidenceDTO>> saveEvidence(
            @RequestParam("userId") Long userId,
            @RequestParam(value = "scanId", required = false) Long scanId,
            @RequestParam("title") String title,
            @RequestParam(value = "companyName", required = false) String companyName,
            @RequestParam(value = "evidenceNotes", required = false) String evidenceNotes,
            @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {

        EvidenceDTO dto = evidenceService.saveEvidence(userId, scanId, title, companyName, evidenceNotes, file);
        return ResponseEntity.ok(ApiResponse.ok("Evidence securely logged in vault", dto));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<EvidenceDTO>>> getMyEvidence(
            @RequestParam("userId") Long userId) {
        List<EvidenceDTO> list = evidenceService.getUserEvidence(userId);
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EvidenceDTO>> getEvidenceById(@PathVariable("id") Long id) {
        EvidenceDTO dto = evidenceService.getEvidenceById(id);
        return ResponseEntity.ok(ApiResponse.ok(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteEvidence(
            @PathVariable("id") Long id,
            @RequestParam("userId") Long userId) {
        evidenceService.deleteEvidence(id, userId);
        return ResponseEntity.ok(ApiResponse.ok("Evidence item deleted", null));
    }

    @PostMapping("/{id}/toggle-report")
    public ResponseEntity<ApiResponse<EvidenceDTO>> toggleReport(@PathVariable("id") Long id) {
        EvidenceDTO dto = evidenceService.toggleReportToAdmin(id);
        return ResponseEntity.ok(ApiResponse.ok("Reporting status updated", dto));
    }
}
