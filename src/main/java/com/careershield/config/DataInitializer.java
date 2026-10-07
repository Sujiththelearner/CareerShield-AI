package com.careershield.config;

import com.careershield.dto.request.JobScanRequest;
import com.careershield.dto.response.RiskReportResponse;
import com.careershield.entity.Evidence;
import com.careershield.entity.JobScan;
import com.careershield.entity.SavedJob;
import com.careershield.entity.User;
import com.careershield.enums.Role;
import com.careershield.repository.EvidenceRepository;
import com.careershield.repository.JobScanRepository;
import com.careershield.repository.SavedJobRepository;
import com.careershield.repository.UserRepository;
import com.careershield.service.JobScanService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * CommandLineRunner that initializes realistic demo data on application startup.
 * Seeds a default Administrator, a demo Student account, 3 diverse historical scans
 * (Critical Scam, High-Risk Anomaly, and Genuine Listing), an Evidence Vault record,
 * and a verified bookmark.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final JobScanRepository jobScanRepository;
    private final EvidenceRepository evidenceRepository;
    private final SavedJobRepository savedJobRepository;
    private final JobScanService jobScanService;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           JobScanRepository jobScanRepository,
                           EvidenceRepository evidenceRepository,
                           SavedJobRepository savedJobRepository,
                           JobScanService jobScanService,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jobScanRepository = jobScanRepository;
        this.evidenceRepository = evidenceRepository;
        this.savedJobRepository = savedJobRepository;
        this.jobScanService = jobScanService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // 1. Ensure default Admin account exists
        User admin = userRepository.findByEmail("admin@careershield.ai").orElseGet(() -> {
            User newAdmin = new User(
                    "System Administrator",
                    "admin@careershield.ai",
                    passwordEncoder.encode("Admin@12345"),
                    Role.ADMIN,
                    "CareerShield National Security Cell"
            );
            return userRepository.save(newAdmin);
        });

        // 2. Ensure default demo Student account exists
        User student = userRepository.findByEmail("sujith@student.ac.in").orElseGet(() -> {
            User newStudent = new User(
                    "Sujith Kumar",
                    "sujith@student.ac.in",
                    passwordEncoder.encode("Password@123"),
                    Role.STUDENT,
                    "National Institute of Technology"
            );
            return userRepository.save(newStudent);
        });

        // 3. Pre-seed demo job scans if student has no historical scans
        long existingScans = jobScanRepository.countByUser(student);
        if (existingScans < 3) {
            System.out.println("🛡️ [DataInitializer] Pre-seeding realistic demo scans for student: " + student.getEmail());

            // --- Record 1: CRITICAL SCAM (Google Impersonation + Deposit Demand) ---
            JobScanRequest scan1 = new JobScanRequest(
                    student.getId(),
                    "Google Cloud Software Intern",
                    "Google",
                    "https://linkedin.com/jobs/view/fake-google-intern",
                    "googlehr.recruitment@gmail.com",
                    "₹75,000 / month",
                    "Remote / Work from Home",
                    "URGENT HIRING: We are offering an immediate remote software developer internship with Google Cloud team. " +
                    "No prior coding interview required! Selected candidates will receive a company MacBook and ₹75,000 monthly stipend. " +
                    "To confirm your onboarding and courier dispatch, candidates must transfer a refundable security deposit of ₹2,500. " +
                    "Contact HR directly on Telegram @google_cloud_hr for joining instructions.",
                    null
            );
            RiskReportResponse report1 = jobScanService.analyzeJob(scan1, null);

            // --- Record 2: HIGH RISK (WhatsApp Daily Payout Task Scam) ---
            JobScanRequest scan2 = new JobScanRequest(
                    student.getId(),
                    "Part-Time Media Rating Associate",
                    "Digital Trendz Media",
                    "https://indeed.com/view/quick-task-job",
                    "hr.digitaltrendz@yahoo.com",
                    "Earn ₹5,000 daily payout",
                    "Work from Home",
                    "Daily payout guaranteed! Simple part-time task: watch YouTube videos, like posts, and submit screenshots. " +
                    "Earn 5000 daily from home without experience. Direct selection without interview. Limited slots available! " +
                    "Send message on WhatsApp to start today and receive your first task reward.",
                    null
            );
            RiskReportResponse report2 = jobScanService.analyzeJob(scan2, null);

            // --- Record 3: GENUINE OPPORTUNITY (Infosys Developer Listing) ---
            JobScanRequest scan3 = new JobScanRequest(
                    student.getId(),
                    "Junior Java Backend Developer",
                    "Infosys Limited",
                    "https://careers.infosys.com/jobs/java-associate",
                    "talent.acquisition@infosys.com",
                    "₹30,000 / month stipend",
                    "Bangalore, India",
                    "Infosys is looking for motivated junior developers to join our Cloud Engineering team. " +
                    "Responsibilities include building RESTful microservices with Java, Spring Boot, and MySQL. " +
                    "Requirements: Bachelor degree in Computer Science or related discipline, understanding of OOP principles, and basic Git skills. " +
                    "The selection process includes an online coding assessment followed by a technical interview via Microsoft Teams.",
                    null
            );
            RiskReportResponse report3 = jobScanService.analyzeJob(scan3, null);

            // 4. Pre-seed Evidence Vault Item for Demo Student
            JobScan flaggedScan = jobScanRepository.findById(report1.getScanId()).orElse(null);
            Evidence demoEvidence = new Evidence(
                    student,
                    flaggedScan,
                    "Fraudulent Offer Letter & Courier Deposit Demand",
                    "TechNova Solutions / Impersonating Google",
                    "The recruiter demanded a ₹2,500 refundable security deposit for dispatching an Apple MacBook before the start date. " +
                    "Official Google recruiting guidelines explicitly state they never demand advance money for hardware. Incident flagged for placement cell records.",
                    null
            );
            demoEvidence.setReportedToAdmin(true); // Alerted to placement cell
            evidenceRepository.save(demoEvidence);

            // 5. Pre-seed a bookmarked genuine job
            JobScan genuineScan = jobScanRepository.findById(report3.getScanId()).orElse(null);
            if (genuineScan != null) {
                SavedJob savedJob = new SavedJob(student, genuineScan, "Verified official listing on Infosys careers portal");
                savedJobRepository.save(savedJob);
            }

            System.out.println("✅ [DataInitializer] Demo data seeded successfully: 3 Scans, 1 Evidence Vault record, 1 Saved bookmark.");
        }
    }
}
