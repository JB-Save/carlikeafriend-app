export const handleUnauthorizedError = (response, navigate, logout, setModalMessage) => {

    // Verificar si la respuesta es 401
    if (response.status === 401) {
        // 1. Establecer mensaje amigable
        const message = "Tu sesión ha expirado. Por favor, inicia sesión de nuevo.";

        // 2. Activamos el modal directamente usando el contexto global
        if (setModalMessage) setModalMessage(message);

        // 3. Limpiamos sesión y navegamos
        logout();
        navigate("/signin", {
            replace: true,
            state: { from: window.location.pathname } // Guardamos la ruta actual
        });

        return true; // Error manejado
    }

    return false; // No es un error 401
}
