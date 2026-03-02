// ============================================
// admin.js — Admin Panel JS Utilities
// ============================================

document.addEventListener('DOMContentLoaded', function () {
    // ========================
    // Sidebar Active State
    // ========================
    const currentPath = window.location.pathname;
    document.querySelectorAll('.nav-item').forEach(link => {
        const href = link.getAttribute('href');
        if (href && currentPath.startsWith(href) && href !== '/admin/dashboard') {
            link.classList.add('active');
        }
    });

    // ========================
    // Data Tables — Delete Confirmation
    // ========================
    document.querySelectorAll('[data-action="delete"]').forEach(btn => {
        btn.addEventListener('click', async function (e) {
            e.preventDefault();
            const target = this.getAttribute('data-target');
            const name = this.getAttribute('data-name') || 'mục này';
            const confirmed = await showConfirm(`Bạn có chắc chắn muốn xóa "${name}"?`, {
                title: 'Xác nhận xóa', type: 'danger', danger: true, confirmText: 'Xóa'
            });
            if (confirmed) {
                const form = document.createElement('form');
                form.method = 'POST';
                form.action = target;
                // CSRF token
                const csrfMeta = document.querySelector('meta[name="_csrf"]');
                if (csrfMeta) {
                    const input = document.createElement('input');
                    input.type = 'hidden';
                    input.name = '_csrf';
                    input.value = csrfMeta.content;
                    form.appendChild(input);
                }
                const methodInput = document.createElement('input');
                methodInput.type = 'hidden';
                methodInput.name = '_method';
                methodInput.value = 'DELETE';
                form.appendChild(methodInput);
                document.body.appendChild(form);
                form.submit();
            }
        });
    });

    // ========================
    // Toggle Product Active / User Ban
    // ========================
    document.querySelectorAll('[data-action="toggle"]').forEach(btn => {
        btn.addEventListener('click', async function (e) {
            e.preventDefault();
            const target = this.getAttribute('data-target');
            try {
                const response = await fetch(target, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        [getCsrfHeader()]: getCsrfToken()
                    }
                });
                if (response.ok) {
                    location.reload();
                } else {
                    showToast('Có lỗi xảy ra', 'error');
                }
            } catch (err) {
                showToast('Lỗi kết nối', 'error');
            }
        });
    });

    // ========================
    // Quick Search in Tables
    // ========================
    const tableSearch = document.getElementById('adminTableSearch');
    if (tableSearch) {
        tableSearch.addEventListener('input', function () {
            const query = this.value.toLowerCase();
            const rows = document.querySelectorAll('.data-table tbody tr');
            rows.forEach(row => {
                const text = row.textContent.toLowerCase();
                row.style.display = text.includes(query) ? '' : 'none';
            });
        });
    }

    // ========================
    // Stat Cards Animation
    // ========================
    const statCards = document.querySelectorAll('.stat-card');
    statCards.forEach((card, index) => {
        card.style.opacity = '0';
        card.style.transform = 'translateY(20px)';
        setTimeout(() => {
            card.style.transition = 'all 0.4s ease';
            card.style.opacity = '1';
            card.style.transform = 'translateY(0)';
        }, 100 + index * 80);
    });

    // ========================
    // Charts placeholder
    // ========================
    const chartContainers = document.querySelectorAll('[data-chart]');
    chartContainers.forEach(container => {
        // Future: integrate Chart.js
        container.style.minHeight = '200px';
    });

    // ========================
    // Mobile Sidebar Toggle
    // ========================
    const mobileToggle = document.getElementById('mobileToggle');
    const sidebar = document.getElementById('sidebar');
    if (mobileToggle && sidebar) {
        mobileToggle.addEventListener('click', () => {
            sidebar.classList.toggle('mobile-open');
        });

        // Close sidebar when clicking outside on mobile
        document.addEventListener('click', (e) => {
            if (window.innerWidth <= 1024 && sidebar.classList.contains('mobile-open')) {
                if (!sidebar.contains(e.target) && !mobileToggle.contains(e.target)) {
                    sidebar.classList.remove('mobile-open');
                }
            }
        });
    }

    // ========================
    // Notification Bell
    // ========================
    const notifBtn = document.querySelector('.topbar-icon-btn');
    if (notifBtn) {
        notifBtn.addEventListener('click', () => {
            const dot = notifBtn.querySelector('.notif-dot');
            if (dot) dot.style.display = 'none';
            // Future: show notification dropdown
        });
    }
});
