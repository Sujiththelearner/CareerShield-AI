/**
 * CareerShield AI - Admin Dashboard Client Logic
 */

async function initAdmin() {
    CareerAuth.requireAuth();

    const user = CareerAuth.getUser();
    if (!user || user.role !== 'ADMIN') {
        alert('Access Restricted: Administrator privileges required.');
        window.location.href = '/dashboard.html';
        return;
    }

    try {
        // 1. Fetch Admin Metrics
        const metricsRes = await fetch('/api/admin/metrics');
        const metricsJson = await metricsRes.json();

        if (metricsJson.success && metricsJson.data) {
            const data = metricsJson.data;
            document.getElementById('adm-total-scans').textContent = data.totalScans;
            document.getElementById('adm-safe-scans').textContent = data.safeScans;
            document.getElementById('adm-high-scans').textContent = data.highRiskScans;
            document.getElementById('adm-evidence-count').textContent = data.evidenceCount;
        }

        // 2. Fetch Reported Evidence
        const evRes = await fetch('/api/admin/evidence');
        const evJson = await evRes.json();
        const evTbody = document.getElementById('adm-evidence-tbody');

        if (evJson.success && evJson.data && evJson.data.length > 0) {
            evTbody.innerHTML = evJson.data.map(item => {
                let fileBtn = '<span class="text-muted small">None</span>';
                if (item.filePath) {
                    fileBtn = `<a href="/uploads/evidence/${item.filePath}" target="_blank" class="btn btn-sm btn-outline-info py-0 px-2"><i class="bi bi-paperclip"></i> View Proof</a>`;
                }

                return `
                    <tr>
                        <td class="fw-bold text-white">${escapeHtml(item.title)}</td>
                        <td class="text-warning">${escapeHtml(item.companyName || 'Unknown')}</td>
                        <td class="text-secondary small">${new Date(item.createdAt).toLocaleDateString()}</td>
                        <td class="text-secondary small" style="max-width: 300px;">${escapeHtml(item.evidenceNotes || '')}</td>
                        <td>${fileBtn}</td>
                    </tr>
                `;
            }).join('');
        } else {
            evTbody.innerHTML = `
                <tr><td colspan="5" class="text-center text-muted py-4">No student incident reports logged at this time.</td></tr>
            `;
        }

        // 3. Fetch Platform Scans
        const scansRes = await fetch('/api/admin/scans');
        const scansJson = await scansRes.json();
        const scansTbody = document.getElementById('adm-scans-tbody');

        if (scansJson.success && scansJson.data && scansJson.data.length > 0) {
            scansTbody.innerHTML = scansJson.data.map(scan => {
                let badgeClass = 'badge-safe';
                if (scan.riskLevel === 'MEDIUM') badgeClass = 'badge-warning';
                else if (scan.riskLevel === 'HIGH' || scan.riskLevel === 'CRITICAL') badgeClass = 'badge-danger';

                return `
                    <tr>
                        <td class="fw-bold text-white">${escapeHtml(scan.jobTitle)}</td>
                        <td class="text-info">${escapeHtml(scan.companyName)}</td>
                        <td><span class="fw-bold">${scan.overallSafetyScore} / 100</span></td>
                        <td><span class="${badgeClass}">${scan.riskLevel}</span></td>
                        <td class="text-secondary small">${new Date(scan.createdAt).toLocaleDateString()}</td>
                        <td>
                            <a href="/report.html?id=${scan.id}" class="btn btn-sm btn-shield-outline py-0 px-2">
                                Inspect
                            </a>
                        </td>
                    </tr>
                `;
            }).join('');
        } else {
            scansTbody.innerHTML = `
                <tr><td colspan="6" class="text-center text-muted py-4">No recent scans on platform.</td></tr>
            `;
        }
    } catch (e) {
        console.error('Error fetching admin data:', e);
    }
}

document.addEventListener('DOMContentLoaded', initAdmin);
