// ============================================
// common.js — AccountShop Utilities
// ============================================

// ========================
// Toast Notifications
// ========================
function showToast(message, type) {
    type = type || 'info';
    var container = document.getElementById('toastContainer') || createToastContainer();
    var toast = document.createElement('div');
    toast.className = 'toast toast-' + type;

    var iconSvgs = {
        success: '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/></svg>',
        error: '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><line x1="15" y1="9" x2="9" y2="15"/><line x1="9" y1="9" x2="15" y2="15"/></svg>',
        warning: '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/><line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/></svg>',
        info: '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><line x1="12" y1="16" x2="12" y2="12"/><line x1="12" y1="8" x2="12.01" y2="8"/></svg>'
    };

    var titles = { success: 'Thành công', error: 'Lỗi', warning: 'Cảnh báo', info: 'Thông báo' };

    toast.innerHTML =
        '<div class="toast-icon">' + (iconSvgs[type] || iconSvgs.info) + '</div>' +
        '<div class="toast-body">' +
        '  <div class="toast-title">' + (titles[type] || titles.info) + '</div>' +
        '  <div class="toast-message">' + message + '</div>' +
        '</div>' +
        '<button class="toast-close" onclick="this.parentElement.remove()">&times;</button>' +
        '<div class="toast-progress"><div class="toast-progress-bar"></div></div>';

    container.appendChild(toast);
    requestAnimationFrame(function () { toast.classList.add('toast-show'); });

    setTimeout(function () {
        toast.classList.remove('toast-show');
        toast.classList.add('toast-hide');
        setTimeout(function () { toast.remove(); }, 400);
    }, 4000);
}

function createToastContainer() {
    var container = document.createElement('div');
    container.id = 'toastContainer';
    container.className = 'toastContainer';
    document.body.appendChild(container);
    return container;
}

// Auto-read flash messages on page load
document.addEventListener('DOMContentLoaded', function () {
    var successEl = document.getElementById('flashSuccess');
    var errorEl = document.getElementById('flashError');
    if (successEl) showToast(successEl.dataset.msg, 'success');
    if (errorEl) showToast(errorEl.dataset.msg, 'error');
});

// ========================
// Custom Confirm Modal
// (replaces browser confirm())
// ========================
const _confirmIcons = {
    warning: '<svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/><line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/></svg>',
    danger: '<svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><line x1="15" y1="9" x2="9" y2="15"/><line x1="9" y1="9" x2="15" y2="15"/></svg>',
    info: '<svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><line x1="12" y1="16" x2="12" y2="12"/><line x1="12" y1="8" x2="12.01" y2="8"/></svg>',
    success: '<svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/></svg>'
};

/**
 * Show a custom modal confirm dialog.
 * @param {string} message - Message to display
 * @param {Object} [opts] - Options
 * @param {string} [opts.title] - Dialog title (default: "Xác nhận")
 * @param {string} [opts.type] - Icon type: warning|danger|info|success (default: warning)
 * @param {string} [opts.confirmText] - Confirm button text (default: "Xác nhận")
 * @param {string} [opts.cancelText] - Cancel button text (default: "Hủy")
 * @param {boolean} [opts.danger] - If true, confirm button is red
 * @returns {Promise<boolean>} - true if confirmed, false if cancelled
 */
function showConfirm(message, opts = {}) {
    return new Promise((resolve) => {
        const type = opts.type || 'warning';
        const isDanger = opts.danger || false;
        const title = opts.title || 'Xác nhận';
        const confirmText = opts.confirmText || 'Xác nhận';
        const cancelText = opts.cancelText || 'Hủy';

        const backdrop = document.createElement('div');
        backdrop.className = 'confirmBackdrop';
        backdrop.innerHTML = `
            <div class="confirmDialog">
                <div class="confirmIcon confirmIcon--${type}">${_confirmIcons[type] || _confirmIcons.warning}</div>
                <div class="confirmTitle">${title}</div>
                <div class="confirmMessage">${message}</div>
                <div class="confirmActions">
                    ${cancelText ? `<button class="confirmBtn confirmBtn--cancel" data-action="cancel">${cancelText}</button>` : ''}
                    <button class="confirmBtn ${isDanger ? 'confirmBtn--danger' : 'confirmBtn--confirm'}" data-action="confirm">${confirmText}</button>
                </div>
            </div>`;

        function close(result) {
            backdrop.classList.add('hiding');
            setTimeout(() => { backdrop.remove(); }, 220);
            resolve(result);
        }

        const cancelBtn = backdrop.querySelector('[data-action="cancel"]');
        if (cancelBtn) cancelBtn.addEventListener('click', () => close(false));
        backdrop.querySelector('[data-action="confirm"]').addEventListener('click', () => close(true));
        backdrop.addEventListener('click', (e) => { if (e.target === backdrop) close(false); });
        document.addEventListener('keydown', function handler(e) {
            if (e.key === 'Escape') { close(false); document.removeEventListener('keydown', handler); }
        });

        document.body.appendChild(backdrop);
        backdrop.querySelector('[data-action="confirm"]').focus();
    });
}

/**
 * Show a custom alert modal (replaces browser alert()).
 * @param {string} message
 * @param {string} [type] - info|success|warning|danger
 */
function showAlert(message, type = 'info') {
    const titles = { info: 'Thông báo', success: 'Thành công', warning: 'Cảnh báo', danger: 'Lỗi' };
    return showConfirm(message, {
        title: titles[type] || titles.info,
        type: type,
        confirmText: 'OK',
        cancelText: '',
    }).then(() => { });
}

// ========================
// Confirm Action (legacy wrapper)
// ========================
async function confirmAction(message, callback) {
    const result = await showConfirm(message);
    if (result) callback();
}

// ========================
// Format Currency VND
// ========================
function formatPrice(amount) {
    if (amount == null) return '0₫';
    return new Intl.NumberFormat('vi-VN').format(amount) + '₫';
}

// ========================
// CSRF Token for AJAX
// ========================
function getCsrfToken() {
    const meta = document.querySelector('meta[name="_csrf"]');
    return meta ? meta.content : '';
}

function getCsrfHeader() {
    const meta = document.querySelector('meta[name="_csrf_header"]');
    return meta ? meta.content : 'X-CSRF-TOKEN';
}

// ========================
// Fetch Helper with CSRF
// ========================
async function fetchApi(url, options = {}) {
    const defaults = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    const csrfToken = getCsrfToken();
    if (csrfToken) {
        defaults.headers[getCsrfHeader()] = csrfToken;
    }

    const config = { ...defaults, ...options };
    if (options.headers) {
        config.headers = { ...defaults.headers, ...options.headers };
    }

    try {
        const response = await fetch(url, config);
        if (!response.ok) {
            const errorData = await response.json().catch(() => ({}));
            throw new Error(errorData.message || `HTTP error! status: ${response.status}`);
        }
        return await response.json();
    } catch (error) {
        showToast(error.message || 'Có lỗi xảy ra', 'error');
        throw error;
    }
}

// ========================
// Dark Mode Persistence + Icon Toggle
// ========================
const _sunIcon = '<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="5"/><line x1="12" y1="1" x2="12" y2="3"/><line x1="12" y1="21" x2="12" y2="23"/><line x1="4.22" y1="4.22" x2="5.64" y2="5.64"/><line x1="18.36" y1="18.36" x2="19.78" y2="19.78"/><line x1="1" y1="12" x2="3" y2="12"/><line x1="21" y1="12" x2="23" y2="12"/><line x1="4.22" y1="19.78" x2="5.64" y2="18.36"/><line x1="18.36" y1="5.64" x2="19.78" y2="4.22"/></svg>';
const _moonIcon = '<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z"/></svg>';

function updateThemeIcon() {
    const btn = document.getElementById('themeToggle');
    if (!btn) return;
    const isDark = document.body.classList.contains('dark-mode');
    btn.innerHTML = isDark ? _sunIcon : _moonIcon;
    btn.title = isDark ? 'Chế độ sáng' : 'Chế độ tối';
}

(function initDarkMode() {
    const saved = localStorage.getItem('accountshop-dark-mode');
    if (saved === 'true') {
        document.body.classList.add('dark-mode');
    } else if (saved === 'false') {
        document.body.classList.remove('dark-mode');
    }
    // Update icon after DOM ready
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', updateThemeIcon);
    } else {
        updateThemeIcon();
    }
})();

const themeToggle = document.getElementById('themeToggle');
if (themeToggle) {
    // Remove the inline onclick from default.html
    themeToggle.removeAttribute('onclick');
    themeToggle.addEventListener('click', function () {
        const isDark = document.body.classList.toggle('dark-mode');
        localStorage.setItem('accountshop-dark-mode', isDark);
        updateThemeIcon();
    });
}

// ========================
// Dropdown Close on Click Outside
// ========================
document.addEventListener('click', function (e) {
    if (!e.target.closest('.userMenu')) {
        document.querySelectorAll('.dropdown.show').forEach(d => d.classList.remove('show'));
    }
});

// ========================
// Keyboard Shortcut: / for search
// ========================
document.addEventListener('keydown', function (e) {
    if (e.key === '/' && !['INPUT', 'TEXTAREA', 'SELECT'].includes(document.activeElement.tagName)) {
        e.preventDefault();
        const searchInput = document.querySelector('.searchInput');
        if (searchInput) searchInput.focus();
    }
});

console.log('⚡ AccountShop loaded');

