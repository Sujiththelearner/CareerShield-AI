/**
 * CareerShield AI - Job Scanner Client Logic
 */

function loadSampleScam1() {
    document.getElementById('jobTitle').value = 'Google Cloud Software Intern';
    document.getElementById('companyName').value = 'Google';
    document.getElementById('jobUrl').value = 'https://linkedin.com/jobs/view/fake-google-intern';
    document.getElementById('recruiterEmail').value = 'google.hr.recruitment@gmail.com';
    document.getElementById('salaryInfo').value = '₹75,000 / month';
    document.getElementById('location').value = 'Remote / Work from Home';
    document.getElementById('jobDescription').value = 
        'URGENT HIRING: We are offering an immediate remote software developer internship with Google Cloud team. ' +
        'No prior coding interview required! Selected candidates will receive a company MacBook and ₹75,000 monthly stipend. ' +
        'To confirm your onboarding and courier dispatch, candidates must transfer a refundable security deposit of ₹2,500. ' +
        'Contact HR directly on Telegram @google_cloud_hr for joining instructions.';
}

function loadSampleScam2() {
    document.getElementById('jobTitle').value = 'Part-Time Media Rating Associate';
    document.getElementById('companyName').value = 'Digital Trendz Inc';
    document.getElementById('jobUrl').value = '';
    document.getElementById('recruiterEmail').value = 'hr.digitaltrendz@yahoo.com';
    document.getElementById('salaryInfo').value = 'Earn ₹5,000 daily payout';
    document.getElementById('location').value = 'Work from Home';
    document.getElementById('jobDescription').value = 
        'Daily payout guaranteed! Simple task: Watch YouTube videos, like posts, and submit screenshots. ' +
        'Earn 5000 daily from home without experience. Direct selection without interview. Limited slots available! ' +
        'Send message on WhatsApp to start today and receive your first task reward.';
}

function loadSampleLegit() {
    document.getElementById('jobTitle').value = 'Junior Java Backend Developer';
    document.getElementById('companyName').value = 'Infosys Limited';
    document.getElementById('jobUrl').value = 'https://careers.infosys.com/jobs/java-associate';
    document.getElementById('recruiterEmail').value = 'talent.acquisition@infosys.com';
    document.getElementById('salaryInfo').value = '₹30,000 / month stipend';
    document.getElementById('location').value = 'Bangalore, India';
    document.getElementById('jobDescription').value = 
        'Infosys is looking for motivated junior developers to join our Cloud Engineering team. ' +
        'Responsibilities include building RESTful microservices with Java, Spring Boot, and MySQL. ' +
        'Requirements: Bachelor degree in Computer Science or related discipline, understanding of OOP principles, and basic Git skills. ' +
        'The selection process includes an online coding assessment followed by a technical interview via Microsoft Teams.';
}

document.getElementById('scan-form').addEventListener('submit', async (e) => {
    e.preventDefault();

    const user = CareerAuth.getUser();
    const userId = user ? user.userId : null;

    const modalEl = document.getElementById('scanningModal');
    const modal = new bootstrap.Modal(modalEl);
    modal.show();

    const statusText = document.getElementById('radar-status-text');
    const progressBar = document.getElementById('radar-progress-bar');

    // Simulate animated diagnostics stages
    const stages = [
        { progress: 25, text: 'Scanning recruiter email against corporate DNS registries...' },
        { progress: 50, text: 'Executing Java Strategy Rules for deposit demands & urgency syntax...' },
        { progress: 75, text: 'Calling AI NLP Fraud Classifier microservice...' },
        { progress: 90, text: 'Compiling Explainable Safety Report...' }
    ];

    let currentStage = 0;
    const interval = setInterval(() => {
        if (currentStage < stages.length) {
            progressBar.style.width = stages[currentStage].progress + '%';
            statusText.textContent = stages[currentStage].text;
            currentStage++;
        }
    }, 450);

    const formData = new FormData();
    formData.append('jobTitle', document.getElementById('jobTitle').value.trim());
    formData.append('companyName', document.getElementById('companyName').value.trim());
    formData.append('jobUrl', document.getElementById('jobUrl').value.trim());
    formData.append('recruiterEmail', document.getElementById('recruiterEmail').value.trim());
    formData.append('salaryInfo', document.getElementById('salaryInfo').value.trim());
    formData.append('location', document.getElementById('location').value.trim());
    formData.append('jobDescription', document.getElementById('jobDescription').value.trim());
    if (userId) {
        formData.append('userId', userId);
    }

    const fileInput = document.getElementById('jobFile');
    if (fileInput.files.length > 0) {
        formData.append('file', fileInput.files[0]);
    }

    try {
        const response = await fetch('/api/scans/upload-analyze', {
            method: 'POST',
            body: formData
        });

        clearInterval(interval);
        progressBar.style.width = '100%';
        statusText.textContent = 'Analysis Complete! Loading safety report...';

        const json = await response.json();

        if (json.success && json.data) {
            setTimeout(() => {
                window.location.href = `/report.html?id=${json.data.scanId}`;
            }, 600);
        } else {
            alert('Scan Error: ' + (json.message || 'Unable to complete scan.'));
            modal.hide();
        }
    } catch (err) {
        clearInterval(interval);
        alert('Network or server error while connecting to Java backend.');
        modal.hide();
    }
});
