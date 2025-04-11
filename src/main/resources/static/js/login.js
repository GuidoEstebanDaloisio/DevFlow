document.addEventListener("DOMContentLoaded", function () {
    const form = document.querySelector("form");

    form.addEventListener("submit", function (e) {
        e.preventDefault();

        const nombre = document.getElementById("nombre").value;
        const contrasenia = document.getElementById("contrasenia").value;

        fetch("/api/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ nombre, contrasenia })
        })
            .then(response => {
                if (!response.ok) {
                    return response.text().then(text => { throw new Error(text); });
                }
                return response.json();
            })
            .then(data => {
                const rol = data.rol;

                if (rol === "ADMINISTRADOR") {
                    window.location.href = "/admin";
                } else if (rol === "GERENTE") {
                    window.location.href = "/gerente";
                } else if (rol === "CLIENTE") {
                    window.location.href = "/cliente";
                } else {
                    alert("Rol desconocido: " + rol);
                }
            })
            .catch(error => {
                alert("Error: " + error.message);
            });
    });
});
