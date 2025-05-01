document.addEventListener("DOMContentLoaded", () => {
    const botones = Array.from(document.querySelectorAll('.boton-estado'));
    const estadoActual = document.querySelector("#estado-actual").value;

    const botonEnProgreso = document.querySelector('#EN_PROGRESO');
    const fechaInputContainer = document.querySelector('.input-fecha');
    const inputFecha = document.querySelector('#fecha-inicio');
    const botonGuardarFecha = document.querySelector('#guardar-fecha');
    const formGuardarFecha = document.getElementById('formGuardarFecha');

    const botonCompletado = document.querySelector('#COMPLETADO');
    const fechaFinalContainer = document.querySelector('.input-fecha-final');
    const inputFechaFinal = document.querySelector('#fecha-final');
    const botonGuardarFechaFinal = document.querySelector('#guardar-fecha-final');
    const formGuardarFechaFinal = document.getElementById('formGuardarFechaFinal');

    // 1) Guardar texto limpio en data-nombre para cada botón
    botones.forEach(boton => {
        boton.dataset.nombre = boton.textContent.trim();
    });

    // 2) Deshabilitar botones según atributos data-puede-*
    botones.forEach(boton => {
        const puedeKey = Object.keys(boton.dataset).find(k => k.startsWith("puede"));
        if (puedeKey && boton.dataset[puedeKey] === "false") {
            boton.classList.add('deshabilitado');
            boton.setAttribute('disabled', true);
        }
    });

    // 3) Marcar el botón del estado actual
    const botonEstadoActual = botones.find(boton => boton.id === estadoActual);
    if (botonEstadoActual) {
        botonEstadoActual.classList.add('seleccionado');
        if (botonEstadoActual.disabled) {
            botonEstadoActual.classList.add('seleccionado-deshabilitado');
        }
    }

    // 4) Al hacer click en “En progreso”, mostramos el date-picker
    botonEnProgreso.addEventListener('click', event => {
        const tieneFechaInicio = botonEnProgreso.dataset.tieneFechaInicio === "true";

        if (tieneFechaInicio) {
            return; // dejar que se envíe normalmente
        } else {
            event.preventDefault();
            fechaInputContainer.style.display = 'block';
            botonEnProgreso.classList.remove('seleccionado');
        }
    });

    // 5) Al hacer click en “Guardar Fecha”, validamos y dejamos que el form se envíe SOLO a /guardar-fecha
    botonGuardarFecha.addEventListener('click', event => {
        if (!inputFecha.value) {
            event.preventDefault();
            alert("Por favor, ingrese una fecha válida.");
            return;
        }
    });

    // 6) Al hacer click en “Finalizado”, mostramos date-picker sin importar si ya hay fecha
    botonCompletado.addEventListener('click', event => {
        event.preventDefault();
        fechaFinalContainer.style.display = 'block';
        botonCompletado.classList.remove('seleccionado');
    });

    // 7) Al hacer click en “Guardar Fecha Final”, validamos y dejamos enviar a /guardar-fecha-fin
    botonGuardarFechaFinal.addEventListener('click', event => {
        if (!inputFechaFinal.value) {
            event.preventDefault();
            alert("Por favor, ingrese una fecha válida.");
            return;
        }
    });
});
