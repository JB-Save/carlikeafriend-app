import { useCallback, useState } from "react"

export const useFetch = () => {

    const [state, setState] = useState({
        data: null,
        isLoading: true,
        error: null
    })

    const { data, isLoading, error } = state

    // Utilizamos useCallback para memorizar la función fetchData.
    // Esto evita que la función se recree en cada renderizado.
    const fetchData = useCallback(async (url, method, bodyData = null) => {

        if (!url) return;

        // Reiniciamos estado de carga al iniciar una nueva petición
        setState(prev => ({ ...prev, isLoading: true, error: null }));

        try {
            const options = {
                method: method,
                headers: {
                    'Content-type': 'application/json; charset=UTF-8',
                },
                body: method == 'GET' || method == 'DELETE' ? null : JSON.stringify(bodyData)
            }

            const res = await fetch(url, options);

            // Manejo de 204 No Content
            if (res.status === 204) {
                setState({
                    data: { status: res.status },
                    isLoading: false,
                    error: null
                })
                return;
            }

            if (!res.ok) {

                let errorMessage = "Error en el servidor.";
                let rawBody = '';

                // Lógica de manejo de error 
                try {
                    // 1. Leer el body una sola vez como texto plano
                    rawBody = await res.text();

                    // 2. Intentar parsear como JSON
                    try {
                        const errorData = JSON.parse(rawBody);

                        // Prioridad: message > error > rawBody
                        if (errorData?.message) {
                            errorMessage = errorData.message;
                        } else if (errorData?.error) {
                            errorMessage = errorData.error;
                        } else {
                            errorMessage = rawBody; // fallback al texto original
                        }
                    } catch {
                        // 3. Si no es JSON válido, usar el texto tal cual
                        if (rawBody) errorMessage = rawBody;
                    }

                } catch (e) {
                    // Si falla la lectura del body por completo (ej. error de red)
                    console.error("Error leyendo el body o de red:", e);
                }

                // Lanzamos el error con el mensaje limpio y el estado HTTP
                throw { message: errorMessage, status: res.status };
            }


            // Si llegamos aquí, la respuesta es OK y es seguro parsear JSON
            const data = await res.json()

            setState({
                data,
                isLoading: false,
                error: null
            })
        } catch (error) {
            // Aquí 'error' ya tiene la estructura { message: "...", status: ... }
            // o es un error de red nativo
            setState({
                data: null,
                error,
                isLoading: false
            })
        }
    }, []);

    return {
        data,
        isLoading,
        error,
        fetchData
    }

}


