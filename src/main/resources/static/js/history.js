/**
 * CareerShield AI - Scan History Archive Client Logic
 */

let allScans = [];

async function loadHistory() {
    CareerAuth.requireAuth();

    const user = CareerAuth.getUser();
    if (!user) return;

    try {
        const res = await fetch(`/api/scans/my?userId=${user.userId}`);
        const json = await res.json();

        if (json.success && json.data) {
            allScans = json.data;
            renderTable(allScans);
        } else {
            document.getElementById('history-tbody').innerHTML = `
                <tr><td colspan="7" class="text-center text-muted py-4">No scans found.</td></tr>
            `;
        }
    } catch (err) {
        document.getElementById('history-tbody').innerHTML = `
            <tr><td colspan="7" class="text-center text-danger py-4">Error loading scan history from backend.</td></tr>
        `;
    }
}

function filterScans() {
    const query = document.getElementById('search-input').value.toLowerCase().trim();
    const level = document.getElementById('filter-level').value;

    const filtered = allScans.filter(scan => {
        const matchesQuery = scan.jobTitle.toLowerCase().includes(query) ||
                             scan.companyName.toLowerCase().includes(query);
        const matchesLevel = (level === 'ALL') || (scan.riskLevel === level);
        return matchesQuery && matchesLevel;
    });

    renderTable(filtered);
}

function renderTable(scans) {
    const tbody = document.getElementById('history-tbody');

    if (scans.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="7" class="text-center text-muted py-4">
                    <i class="bi bi-inbox fs-2 d-block mb-2"></i>
                    No scans match your criteria.
                </td>
            </tr>
        `;
        return;
    }

    tbody.innerHTML = scans.map(scan => {
        let badgeClass = 'badge-safe';
        if (scan.riskLevel === 'MEDIUM') badgeClass = 'badge-warning';
        else if (scan.riskLevel === 'HIGH' || scan.riskLevel === 'CRITICAL') badgeClass = 'badge-danger';

        const scoreColor = scan.overallSafetyScore >= 70 ? 'text-success' : (scan.overallSafetyScore >= 40 ? 'text-warning' : 'text-danger');

        return `
            <tr>
                <td class="fw-bold text-white">${escapeHtml(scan.jobTitle)}</td>
                <td class="text-info">${escapeHtml(scan.companyName)}</td>
                <td class="text-secondary small">${new Date(scan.createdAt).toLocaleDateString()}</td>
                <td><span class="fw-bold ${scoreColor}">${scan.overallSafetyScore} / 100</span></td>
                <td><span class="${badgeClass}">${scan.riskLevel}</span></td>
                <td><span class="badge bg-secondary">${scan.riskFactorCount} Flags</span></td>
                <td>
                    <div class="d-flex gap-2">
                        <a href="/report.html?id=${scan.id}" class="btn btn-sm btn-shield-outline py-1 px-2">
                            <i class="bi bi-eye"></i> Report
                        </a>
                        <button class="btn btn-sm btn-outline-danger py-1 px-2" onclick="deleteScan(${scan.id})">
                            <i class="bi bi-trash"></i>
                        </button>
                    </div>
                </td>
            </tr>
        `;
    }).join('');
}

async function deleteScan(scanId) {
    if (!confirm('Are you sure you want to delete this scan report?')) return;

    const user = CareerAuth.getUser();
    if (!user) return;

    try {
        const res = await fetch(`/api/scans/${scanId}?userId=${user.userId}`, { method: 'DELETE' });
        const json = await res.json();
        if (json.success) {
            allScans = allScans.filter(s => s.id !== scanId);
            filterScans();
        } else {
            alert('Failed to delete scan.');
        }
    } catch (e) {
        alert('Network error while deleting scan.');
    }
}

document.addEventListener('DOMContentLoaded', loadHistory);
