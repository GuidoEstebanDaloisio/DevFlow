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

    // 1) Modificar texto con salto de línea y guardar texto original en data-nombre
    botones.forEach(boton => {
        const textoOriginal = boton.textContent.trim();
        boton.dataset.nombre = textoOriginal;

        const puedeKey = Object.keys(boton.dataset).find(k => k.startsWith("puede"));
        const deshabilitado = puedeKey && boton.dataset[puedeKey] === "false";

        if (boton.id === estadoActual) {
            boton.innerHTML = "Esta:<br>" + textoOriginal;
            boton.classList.add('estado-actual', 'seleccionado');
            if (deshabilitado || boton.disabled) {
                boton.classList.add('seleccionado-deshabilitado');
                boton.setAttribute('disabled', true);
            }
        } else {
            boton.innerHTML = "Mover a:<br>" + textoOriginal;
            if (deshabilitado) {
                boton.classList.add('deshabilitado');
                boton.setAttribute('disabled', true);
            }
        }
    });

    // 2) Al hacer click en “En progreso”, mostramos el date-picker si no hay fecha
    botonEnProgreso.addEventListener('click', event => {
        const tieneFechaInicio = botonEnProgreso.dataset.tieneFechaInicio === "true";
        if (!tieneFechaInicio) {
            event.preventDefault();
            fechaInputContainer.style.display = 'block';
            botonEnProgreso.classList.remove('seleccionado');
        }
    });

    // 3) Guardar Fecha de Inicio
    botonGuardarFecha.addEventListener('click', event => {
        if (!inputFecha.value) {
            event.preventDefault();
            alert("Por favor, ingrese una fecha válida.");
        } else {
            formGuardarFecha.submit();
        }
    });

    // 4) Al hacer click en “Finalizado”, mostramos date-picker sin importar si ya hay fecha
    botonCompletado.addEventListener('click', event => {
        event.preventDefault();
        fechaFinalContainer.style.display = 'block';
        botonCompletado.classList.remove('seleccionado');
    });

    // 5) Guardar Fecha Final
    botonGuardarFechaFinal.addEventListener('click', event => {
        if (!inputFechaFinal.value) {
            event.preventDefault();
            alert("Por favor, ingrese una fecha válida.");
        } else {
            formGuardarFechaFinal.submit();
        }
    });
});
