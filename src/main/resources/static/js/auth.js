/**
 * CareerShield AI - Client Authentication & Session Management Module
 */

const CareerAuth = {
    STORAGE_KEY: 'careershield_user',

    saveUser(userData) {
        localStorage.setItem(this.STORAGE_KEY, JSON.stringify(userData));
    },

    getUser() {
        const raw = localStorage.getItem(this.STORAGE_KEY);
        try {
            return raw ? JSON.parse(raw) : null;
        } catch (e) {
            return null;
        }
    },

    isLoggedIn() {
        return this.getUser() !== null;
    },

    logout() {
        localStorage.removeItem(this.STORAGE_KEY);
        window.location.href = '/login.html';
    },

    requireAuth() {
        if (!this.isLoggedIn()) {
            window.location.href = '/login.html?redirect=' + encodeURIComponent(window.location.pathname);
        }
    },

    updateNavbar() {
        const user = this.getUser();
        const navAuthContainer = document.getElementById('nav-auth-container');
        if (!navAuthContainer) return;

        if (user) {
            const isAdmin = user.role === 'ADMIN';
            navAuthContainer.innerHTML = `
                <li class="nav-item">
                    <a class="nav-link" href="/dashboard.html"><i class="bi bi-grid-fill me-1"></i> Dashboard</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="/scan.html"><i class="bi bi-shield-check me-1"></i> Scan Job</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="/history.html"><i class="bi bi-clock-history me-1"></i> History</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="/evidence.html"><i class="bi bi-safe-fill me-1"></i> Evidence Vault</a>
                </li>
                ${isAdmin ? `
                <li class="nav-item">
                    <a class="nav-link text-warning" href="/admin.html"><i class="bi bi-shield-lock me-1"></i> Admin</a>
                </li>` : ''}
                <li class="nav-item dropdown ms-lg-2">
                    <a class="btn btn-shield-outline dropdown-toggle py-1 px-3" href="#" role="button" data-bs-toggle="dropdown">
                        <i class="bi bi-person-circle me-1 text-info"></i> ${escapeHtml(user.fullName.split(' ')[0])}
                    </a>
                    <ul class="dropdown-menu dropdown-menu-dark dropdown-menu-end shadow">
                        <li><span class="dropdown-item-text text-muted small">${escapeHtml(user.email)}</span></li>
                        <li><span class="dropdown-item-text badge bg-primary ms-3">${user.role}</span></li>
                        <li><hr class="dropdown-divider"></li>
                        <li><a class="dropdown-item" href="/profile.html"><i class="bi bi-person me-2"></i> Profile</a></li>
                        <li><a class="dropdown-item" href="/safety-tips.html"><i class="bi bi-lightbulb me-2"></i> Safety Guide</a></li>
                        <li><hr class="dropdown-divider"></li>
                        <li><a class="dropdown-item text-danger" href="javascript:void(0)" onclick="CareerAuth.logout()"><i class="bi bi-box-arrow-right me-2"></i> Logout</a></li>
                    </ul>
                </li>
            `;
        } else {
            navAuthContainer.innerHTML = `
                <li class="nav-item">
                    <a class="nav-link" href="/safety-tips.html"><i class="bi bi-lightbulb me-1"></i> Scams Guide</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="/login.html"><i class="bi bi-box-arrow-in-right me-1"></i> Login</a>
                </li>
                <li class="nav-item ms-lg-2">
                    <a class="btn btn-shield-primary py-1 px-3" href="/register.html"><i class="bi bi-person-plus me-1"></i> Register</a>
                </li>
            `;
        }
    }
};

function escapeHtml(text) {
    if (!text) return '';
    return text.replace(/[&<>"']/g, function(m) {
        return { '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#039;' }[m];
    });
}

document.addEventListener('DOMContentLoaded', () => {
    CareerAuth.updateNavbar();
});
