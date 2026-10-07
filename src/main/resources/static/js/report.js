/**
 * CareerShield AI - Explainable Safety Report Client Logic
 */

let currentReportData = null;

async function loadReport() {
    const params = new URLSearchParams(window.location.search);
    const scanId = params.get('id');

    if (!scanId) {
        alert('No scan ID provided.');
        window.location.href = '/scan.html';
        return;
    }

    const user = CareerAuth.getUser();
    const userIdParam = user ? `?userId=${user.userId}` : '';

    try {
        const response = await fetch(`/api/reports/${scanId}${userIdParam}`);
        const json = await response.json();

        if (json.success && json.data) {
            currentReportData = json.data;
            renderReport(json.data);
        } else {
            document.getElementById('loading-spinner').innerHTML = `
                <div class="alert alert-danger">Error: ${json.message || 'Report not found.'}</div>
                <a href="/scan.html" class="btn btn-shield-primary mt-3">Back to Scanner</a>
            `;
        }
    } catch (err) {
        document.getElementById('loading-spinner').innerHTML = `
            <div class="alert alert-danger">Could not connect to the backend server.</div>
            <a href="/scan.html" class="btn btn-shield-primary mt-3">Back to Scanner</a>
        `;
    }
}

function renderReport(data) {
    document.getElementById('loading-spinner').classList.add('d-none');
    document.getElementById('report-content').classList.remove('d-none');

    document.getElementById('report-job-title').textContent = data.jobTitle;
    document.getElementById('report-company').textContent = data.companyName;
    document.getElementById('report-date').textContent = new Date(data.scannedAt).toLocaleString();

    // Summary & metadata
    document.getElementById('summary-explanation').textContent = data.summaryExplanation;
    document.getElementById('meta-email').textContent = data.recruiterEmail || 'Not specified';
    document.getElementById('meta-salary').textContent = data.salaryInfo || 'Not specified';
    document.getElementById('meta-location').textContent = data.location || 'Remote';
    document.getElementById('meta-ai-confidence').textContent = Math.round(data.mlConfidenceScore * 100) + '%';

    if (data.attachmentPath) {
        const attachBox = document.getElementById('attachment-container');
        const attachLink = document.getElementById('attachment-link');
        attachBox.classList.remove('d-none');
        attachLink.href = `/uploads/evidence/${data.attachmentPath}`;
    }

    // Animate Dial Gauge
    animateDial(data.overallSafetyScore, data.riskLevel);

    // Factors breakdown
    const factorContainer = document.getElementById('factors-container');
    const factorBadge = document.getElementById('factor-count-badge');
    const factors = data.factors || [];

    factorBadge.textContent = `${factors.length} Warning Sign${factors.length === 1 ? '' : 's'} Identified`;

    if (factors.length === 0) {
        factorContainer.innerHTML = `
            <div class="p-4 bg-dark rounded border border-success border-opacity-50 text-center">
                <i class="bi bi-shield-check text-success fs-1 mb-2"></i>
                <h5 class="text-white fw-bold">Clean Security Profile</h5>
                <p class="text-secondary small mb-0">No corporate impersonation, payment demands, or suspicious communication patterns detected.</p>
            </div>
        `;
    } else {
        factorContainer.innerHTML = factors.map(factor => {
            const severityClass = factor.severity ? factor.severity.toLowerCase() : 'medium';
            let badgeHtml = '';
            if (factor.severity === 'CRITICAL') badgeHtml = '<span class="badge-critical"><i class="bi bi-exclamation-triangle-fill"></i> CRITICAL SEVERITY</span>';
            else if (factor.severity === 'HIGH') badgeHtml = '<span class="badge-danger"><i class="bi bi-exclamation-circle-fill"></i> HIGH RISK</span>';
            else badgeHtml = '<span class="badge-warning"><i class="bi bi-info-circle-fill"></i> MODERATE RISK</span>';

            return `
                <div class="factor-card factor-${severityClass}">
                    <div class="d-flex flex-wrap justify-content-between align-items-center mb-2">
                        <span class="badge bg-secondary text-light small me-2">${escapeHtml(factor.categoryLabel || 'Indicator')}</span>
                        <div class="d-flex align-items-center gap-2">
                            ${badgeHtml}
                            <span class="badge bg-dark border border-secondary text-danger">-${factor.scorePenalty} pts</span>
                        </div>
                    </div>
                    <h6 class="text-white fw-bold mb-2">${escapeHtml(factor.title)}</h6>
                    <p class="text-secondary small mb-2"><strong class="text-light">Trigger Detected:</strong> ${escapeHtml(factor.description)}</p>
                    <div class="p-2 rounded bg-black bg-opacity-25 border border-secondary border-opacity-25 text-info small">
                        <strong><i class="bi bi-shield-exclamation me-1"></i> Why this matters:</strong> ${escapeHtml(factor.whyItMatters)}
                    </div>
                </div>
            `;
        }).join('');
    }

    // Safety Recommendations
    document.getElementById('safety-recommendations-box').textContent = data.safetyRecommendation;

    // Prep Vault Modal fields
    document.getElementById('vault-title').value = `Suspicious: ${data.jobTitle} at ${data.companyName}`;
}

function animateDial(score, riskLevel) {
    const scoreText = document.getElementById('score-text');
    const circle = document.getElementById('dial-circle');
    const badgeContainer = document.getElementById('verdict-badge-container');

    const totalCircumference = 565.48;
    const targetOffset = totalCircumference - (totalCircumference * score / 100);

    let strokeColor = '#10b981'; // safe
    let badgeHtml = '<span class="badge-safe fs-6"><i class="bi bi-check-circle-fill"></i> SAFE / GENUINE OPPORTUNITY</span>';

    if (riskLevel === 'MEDIUM') {
        strokeColor = '#f59e0b';
        badgeHtml = '<span class="badge-warning fs-6"><i class="bi bi-exclamation-circle-fill"></i> MODERATE RISK / VERIFY DETAILS</span>';
    } else if (riskLevel === 'HIGH') {
        strokeColor = '#ef4444';
        badgeHtml = '<span class="badge-danger fs-6"><i class="bi bi-exclamation-triangle-fill"></i> HIGH RISK / SUSPICIOUS POSTING</span>';
    } else if (riskLevel === 'CRITICAL') {
        strokeColor = '#dc2626';
        badgeHtml = '<span class="badge-critical fs-6"><i class="bi bi-x-octagon-fill"></i> CRITICAL FRAUD / SCAM ALERT</span>';
    }

    circle.style.stroke = strokeColor;
    badgeContainer.innerHTML = badgeHtml;

    // Animate score counter
    let current = 0;
    const duration = 1200;
    const steps = 40;
    const increment = score / steps;
    const intervalTime = duration / steps;

    const timer = setInterval(() => {
        current += increment;
        if (current >= score) {
            current = score;
            clearInterval(timer);
        }
        scoreText.textContent = Math.round(current);
    }, intervalTime);

    setTimeout(() => {
        circle.style.strokeDashoffset = targetOffset;
    }, 100);
}

function openVaultModal() {
    const user = CareerAuth.getUser();
    if (!user) {
        alert('Please login to save this scan into your Evidence Vault.');
        window.location.href = '/login.html?redirect=' + encodeURIComponent(window.location.pathname + window.location.search);
        return;
    }
    const modal = new bootstrap.Modal(document.getElementById('vaultModal'));
    modal.show();
}

async function saveToVault() {
    const user = CareerAuth.getUser();
    if (!user || !currentReportData) return;

    const title = document.getElementById('vault-title').value.trim();
    const notes = document.getElementById('vault-notes').value.trim();

    const formData = new FormData();
    formData.append('userId', user.userId);
    formData.append('scanId', currentReportData.scanId);
    formData.append('title', title);
    formData.append('companyName', currentReportData.companyName);
    formData.append('evidenceNotes', notes);

    try {
        const res = await fetch('/api/evidence', {
            method: 'POST',
            body: formData
        });
        const json = await res.json();
        if (json.success) {
            alert('Incident successfully logged in your Evidence Vault!');
            const modalEl = document.getElementById('vaultModal');
            const modal = bootstrap.Modal.getInstance(modalEl);
            modal.hide();
        } else {
            alert('Failed to save: ' + json.message);
        }
    } catch (e) {
        alert('Error saving to Evidence Vault.');
    }
}

document.addEventListener('DOMContentLoaded', loadReport);
