// Theme Toggle Logic
function initTheme() {
    const savedTheme = localStorage.getItem('quizhub-theme');
    if (savedTheme === 'dark' || (!savedTheme && window.matchMedia('(prefers-color-scheme: dark)').matches)) {
        document.documentElement.classList.add('dark');
    } else {
        document.documentElement.classList.remove('dark');
    }
}

function toggleTheme() {
    if (document.documentElement.classList.contains('dark')) {
        document.documentElement.classList.remove('dark');
        localStorage.setItem('quizhub-theme', 'light');
    } else {
        document.documentElement.classList.add('dark');
        localStorage.setItem('quizhub-theme', 'dark');
    }
}

// Global Search Shortcut ⌘K / Ctrl+K
document.addEventListener('keydown', (e) => {
    if ((e.metaKey || e.ctrlKey) && e.key === 'k') {
        e.preventDefault();
        const searchInput = document.getElementById('global-search-input');
        if (searchInput) {
            searchInput.focus();
            searchInput.select();
        }
    }
});

// Initialize on page load
document.addEventListener('DOMContentLoaded', () => {
    initTheme();
});
