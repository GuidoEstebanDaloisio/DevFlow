function mostrarAlerta(mensaje) {
    alert(mensaje);
}

// Detecta si en la URL hay un parámetro 'error' y lanza una alerta
document.addEventListener("DOMContentLoaded", function() {
    const params = new URLSearchParams(window.location.search);
    if (params.has('error')) {
        mostrarAlerta(params.get('error'));
    }
});