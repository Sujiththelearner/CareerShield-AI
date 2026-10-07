/**
 * CareerShield AI - Evidence Vault Client Logic
 */

async function loadEvidence() {
    CareerAuth.requireAuth();

    const user = CareerAuth.getUser();
    if (!user) return;

    try {
        const res = await fetch(`/api/evidence/my?userId=${user.userId}`);
        const json = await res.json();

        const container = document.getElementById('evidence-container');
        if (json.success && json.data && json.data.length > 0) {
            container.innerHTML = json.data.map(item => {
                const reportBadge = item.reportedToAdmin 
                    ? '<span class="badge bg-danger"><i class="bi bi-shield-exclamation me-1"></i> Reported to Placement Cell</span>'
                    : '<span class="badge bg-secondary">Stored Privately</span>';

                let fileButton = '';
                if (item.filePath) {
                    fileButton = `
                        <a href="/uploads/evidence/${item.filePath}" target="_blank" class="btn btn-sm btn-outline-info">
                            <i class="bi bi-file-earmark-arrow-down me-1"></i> View Forensic Attachment
                        </a>
                    `;
                }

                return `
                    <div class="col-md-6 col-lg-4">
                        <div class="cs-card h-100 d-flex flex-column justify-content-between p-4 border-secondary border-opacity-25">
                            <div>
                                <div class="d-flex justify-content-between align-items-center mb-2">
                                    ${reportBadge}
                                    <span class="text-muted small">${new Date(item.createdAt).toLocaleDateString()}</span>
                                </div>
                                <h5 class="text-white fw-bold mb-1">${escapeHtml(item.title)}</h5>
                                <div class="text-info small mb-3"><i class="bi bi-building me-1"></i> ${escapeHtml(item.companyName || 'Unknown Entity')}</div>
                                <div class="p-3 bg-dark rounded border border-secondary border-opacity-25 text-secondary small mb-3" style="max-height: 120px; overflow-y: auto;">
                                    ${escapeHtml(item.evidenceNotes || 'No additional notes provided.')}
                                </div>
                            </div>
                            <div>
                                <div class="mb-3">${fileButton}</div>
                                <div class="d-flex justify-content-between align-items-center border-top border-secondary border-opacity-25 pt-3">
                                    <button class="btn btn-sm ${item.reportedToAdmin ? 'btn-outline-warning' : 'btn-outline-danger'}" onclick="toggleReport(${item.id})">
                                        <i class="bi bi-send me-1"></i> ${item.reportedToAdmin ? 'Cancel Alert' : 'Alert Cell'}
                                    </button>
                                    <button class="btn btn-sm btn-outline-secondary text-danger" onclick="deleteEvidence(${item.id})">
                                        <i class="bi bi-trash"></i>
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                `;
            }).join('');
        } else {
            container.innerHTML = `
                <div class="col-12 text-center text-muted py-5">
                    <i class="bi bi-safe fs-1 d-block mb-3 text-secondary"></i>
                    <h5 class="text-white fw-bold">Your Evidence Vault is Empty</h5>
                    <p class="text-secondary small">Whenever you scan a suspicious job or receive a dubious offer letter, save it here to preserve forensic records.</p>
                    <button class="btn btn-sm btn-shield-primary mt-2" data-bs-toggle="modal" data-bs-target="#newEvidenceModal">
                        <i class="bi bi-plus me-1"></i> Log First Incident
                    </button>
                </div>
            `;
        }
    } catch (e) {
        document.getElementById('evidence-container').innerHTML = `
            <div class="col-12 text-center text-danger py-5">Failed to connect to Java server for Evidence Vault.</div>
        `;
    }
}

document.getElementById('evidence-form').addEventListener('submit', async (e) => {
    e.preventDefault();

    const user = CareerAuth.getUser();
    if (!user) return;

    const btn = document.getElementById('btn-save-ev');
    btn.disabled = true;
    btn.innerHTML = '<span class="spinner-border spinner-border-sm me-1"></span> Storing...';

    const formData = new FormData();
    formData.append('userId', user.userId);
    formData.append('title', document.getElementById('ev-title').value.trim());
    formData.append('companyName', document.getElementById('ev-company').value.trim());
    formData.append('evidenceNotes', document.getElementById('ev-notes').value.trim());

    const fileInput = document.getElementById('ev-file');
    if (fileInput.files.length > 0) {
        formData.append('file', fileInput.files[0]);
    }

    try {
        const res = await fetch('/api/evidence', {
            method: 'POST',
            body: formData
        });
        const json = await res.json();

        if (json.success) {
            const modalEl = document.getElementById('newEvidenceModal');
            const modal = bootstrap.Modal.getInstance(modalEl);
            modal.hide();
            document.getElementById('evidence-form').reset();
            loadEvidence();
        } else {
            alert('Failed to store: ' + json.message);
        }
    } catch (err) {
        alert('Error uploading incident to Evidence Vault.');
    } finally {
        btn.disabled = false;
        btn.innerHTML = '<i class="bi bi-lock-fill me-1"></i> Store in Vault';
    }
});

async function toggleReport(id) {
    try {
        const res = await fetch(`/api/evidence/${id}/toggle-report`, { method: 'POST' });
        const json = await res.json();
        if (json.success) {
            loadEvidence();
        }
    } catch (e) {
        alert('Could not update reporting status.');
    }
}

async function deleteEvidence(id) {
    if (!confirm('Are you sure you want to delete this evidence record?')) return;

    const user = CareerAuth.getUser();
    if (!user) return;

    try {
        const res = await fetch(`/api/evidence/${id}?userId=${user.userId}`, { method: 'DELETE' });
        const json = await res.json();
        if (json.success) {
            loadEvidence();
        }
    } catch (e) {
        alert('Error deleting record.');
    }
}

document.addEventListener('DOMContentLoaded', loadEvidence);
