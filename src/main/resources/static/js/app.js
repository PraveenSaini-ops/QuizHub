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

// Copy Quiz Invite with Link & Password
function copyQuizInvite(quizTitle, accessCode, password) {
    const origin = window.location.origin;
    const shareUrl = `${origin}/quizzes/join/${accessCode}`;
    
    let text = `📝 Quiz Assessment: ${quizTitle}\n`;
    text += `🔗 Join Link: ${shareUrl}\n`;
    text += `🔑 Access Code: ${accessCode}\n`;
    if (password && password.trim() !== '') {
        text += `🔒 Passcode: ${password}\n`;
    } else {
        text += `🔓 Passcode: None (Open)\n`;
    }

    navigator.clipboard.writeText(text).then(() => {
        showToast('Invite link & password copied to clipboard!');
    }).catch(() => {
        prompt('Copy this invite:', text);
    });
}

function showToast(message) {
    let toast = document.getElementById('quizhub-toast');
    if (!toast) {
        toast = document.createElement('div');
        toast.id = 'quizhub-toast';
        toast.className = 'fixed bottom-6 right-6 bg-slate-900 dark:bg-slate-100 text-white dark:text-slate-900 px-4 py-3 rounded-xl shadow-2xl z-50 text-sm font-semibold flex items-center gap-2 transition-opacity duration-300 opacity-0 pointer-events-none';
        document.body.appendChild(toast);
    }
    toast.innerHTML = `<span class="material-symbols-outlined text-[20px] text-teal-400 dark:text-teal-600">check_circle</span><span>${message}</span>`;
    toast.classList.remove('opacity-0', 'pointer-events-none');
    toast.classList.add('opacity-100');
    setTimeout(() => {
        toast.classList.remove('opacity-100');
        toast.classList.add('opacity-0', 'pointer-events-none');
    }, 3000);
}

// Initialize on page load
document.addEventListener('DOMContentLoaded', () => {
    initTheme();
});
