// theme.js - Updated version
document.addEventListener('DOMContentLoaded', function() {
    const themeToggle = document.getElementById('theme-toggle');
    const prefersDarkScheme = window.matchMedia('(prefers-color-scheme: dark)');
    
    // Set initial theme based on localStorage or system preference
    let currentTheme = localStorage.getItem('theme') || 
                      (prefersDarkScheme.matches ? 'dark' : 'light');
    
    // Apply theme
    function applyTheme(theme) {
        document.documentElement.setAttribute('data-theme', theme);
        localStorage.setItem('theme', theme);
        updateToggleIcon(theme);
    }
    
    // Update toggle icon
    function updateToggleIcon(theme) {
        if (themeToggle) {
            themeToggle.innerHTML = theme === 'dark' ? '🌞' : '🌙';
            themeToggle.setAttribute('aria-label', theme === 'dark' ? 'Switch to light mode' : 'Switch to dark mode');
        }
    }
    
    // Toggle between light and dark
    function toggleTheme() {
        currentTheme = currentTheme === 'light' ? 'dark' : 'light';
        applyTheme(currentTheme);
    }
    
    // Initialize
    applyTheme(currentTheme);
    
    // Listen for system theme changes
    prefersDarkScheme.addListener(e => {
        if (!localStorage.getItem('theme')) {
            currentTheme = e.matches ? 'dark' : 'light';
            applyTheme(currentTheme);
        }
    });
    
    // Add click event if toggle exists
    if (themeToggle) {
        themeToggle.addEventListener('click', toggleTheme);
    }
});