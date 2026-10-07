/**
 * CareerShield AI - Student Dashboard Client Logic
 */

let riskChartInstance = null;

async function initDashboard() {
    CareerAuth.requireAuth();

    const user = CareerAuth.getUser();
    if (!user) return;

    document.getElementById('dash-welcome').textContent = `Welcome Back, ${user.fullName}`;
    document.getElementById('dash-college').textContent = `College: ${user.collegeName || 'National Placement Cell'}`;

    try {
        // 1. Fetch Dashboard Stats
        const statsRes = await fetch(`/api/dashboard/stats?userId=${user.userId}`);
        const statsJson = await statsRes.json();

        if (statsJson.success && statsJson.data) {
            const data = statsJson.data;
            document.getElementById('stat-total').textContent = data.totalScans;
            document.getElementById('stat-safe').textContent = data.safeScans;
            document.getElementById('stat-suspicious').textContent = data.suspiciousScans;
            document.getElementById('stat-high-risk').textContent = data.highRiskScans;

            renderRiskChart(data.safeScans, data.suspiciousScans, data.highRiskScans);
        }

        // 2. Fetch Recent Scans
        const recentRes = await fetch(`/api/scans/recent?userId=${user.userId}`);
        const recentJson = await recentRes.json();

        const tbody = document.getElementById('recent-scans-tbody');
        if (recentJson.success && recentJson.data && recentJson.data.length > 0) {
            tbody.innerHTML = recentJson.data.map(scan => {
                let badgeClass = 'badge-safe';
                if (scan.riskLevel === 'MEDIUM') badgeClass = 'badge-warning';
                else if (scan.riskLevel === 'HIGH' || scan.riskLevel === 'CRITICAL') badgeClass = 'badge-danger';

                return `
                    <tr>
                        <td class="fw-bold text-white">${escapeHtml(scan.jobTitle)}</td>
                        <td class="text-info">${escapeHtml(scan.companyName)}</td>
                        <td class="text-secondary small">${new Date(scan.createdAt).toLocaleDateString()}</td>
                        <td>
                            <span class="fw-bold ${scan.overallSafetyScore >= 70 ? 'text-success' : (scan.overallSafetyScore >= 40 ? 'text-warning' : 'text-danger')}">
                                ${scan.overallSafetyScore} / 100
                            </span>
                        </td>
                        <td><span class="${badgeClass}">${scan.riskLevel}</span></td>
                        <td>
                            <a href="/report.html?id=${scan.id}" class="btn btn-sm btn-shield-outline py-1 px-3">
                                <i class="bi bi-eye me-1"></i> View Report
                            </a>
                        </td>
                    </tr>
                `;
            }).join('');
        } else {
            tbody.innerHTML = `
                <tr>
                    <td colspan="6" class="text-center text-muted py-4">
                        <i class="bi bi-inbox fs-2 d-block mb-2"></i>
                        No job scans yet. Ready to evaluate your first posting?
                        <div class="mt-2">
                            <a href="/scan.html" class="btn btn-sm btn-shield-primary"><i class="bi bi-plus me-1"></i> Run Quick Scan</a>
                        </div>
                    </td>
                </tr>
            `;
        }
    } catch (e) {
        console.error('Error fetching dashboard details:', e);
    }
}

function renderRiskChart(safe, suspicious, highRisk) {
    const ctx = document.getElementById('riskDistributionChart').getContext('2d');
    
    // Default values if no scans yet
    const dataVals = (safe === 0 && suspicious === 0 && highRisk === 0) 
        ? [1, 0, 0] 
        : [safe, suspicious, highRisk];

    if (riskChartInstance) {
        riskChartInstance.destroy();
    }

    riskChartInstance = new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: ['Safe / Low Risk', 'Needs Review', 'High Risk / Blocked'],
            datasets: [{
                data: dataVals,
                backgroundColor: [
                    '#10b981', // Safe green
                    '#f59e0b', // Amber
                    '#ef4444'  // Red
                ],
                borderColor: '#0d1730',
                borderWidth: 3,
                hoverOffset: 4
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'bottom',
                    labels: {
                        color: '#94a3b8',
                        font: { size: 11 },
                        padding: 12
                    }
                }
            },
            cutout: '70%'
        }
    });
}

document.addEventListener('DOMContentLoaded', initDashboard);
