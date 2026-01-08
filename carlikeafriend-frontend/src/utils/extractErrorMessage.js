
// Función utilitaria que extrae el mensaje de error de un objeto Response.
export const extractErrorMessage = async (response) => {
    let errorMessage = 'Error en el servidor.';

    try {
        // Leer el body una sola vez
        const rawBody = await response.text();
        // Intentar parsear como JSON
        try {
            const errorData = JSON.parse(rawBody);

            if (errorData?.message) {
                errorMessage = errorData.message;
            } else if (errorData?.error) {
                errorMessage = errorData.error;
            } else {
                errorMessage = rawBody; // fallback al texto original
            }
        } catch {
            // Si no es JSON válido, usar el texto tal cual
            if (rawBody) errorMessage = rawBody;
        }
    } catch (e) {
        // Si ocurre algún error inesperado al leer el body
        console.error("Error leyendo el body o de red:", e);
    }

    return errorMessage;

};
