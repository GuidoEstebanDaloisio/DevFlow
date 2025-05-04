document.addEventListener("DOMContentLoaded", () => {
    const btnCambiar = document.getElementById("btn-cambiar-contrasenia");
    const campoContrasenia = document.getElementById("campo-contrasenia");

    btnCambiar.addEventListener("click", () => {
        campoContrasenia.style.display = "block";
        btnCambiar.style.display = "none";
    });
});
