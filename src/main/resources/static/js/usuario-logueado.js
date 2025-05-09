function toggleDropdown() {
    const menu = document.getElementById('userMenu');
    menu.style.display = menu.style.display === 'block' ? 'none' : 'block';
}

// Cierra el menú si se hace clic fuera
window.addEventListener('click', function (e) {
    const menu = document.getElementById('userMenu');
    if (!e.target.closest('.user-dropdown')) {
        menu.style.display = 'none';
    }
});