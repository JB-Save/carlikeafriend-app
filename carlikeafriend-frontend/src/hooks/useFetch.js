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

        if (!url) return
        try {
            const options = {
                method: method,
                headers: {
                    'Content-type': 'application/json; charset=UTF-8',
                },
                body: method == 'GET' || method == 'DELETE' ? null : JSON.stringify(bodyData)
            }
            const res = await fetch(url, options)
            if (res.status === 204) {
                setState({
                    data: { status: res.status },
                    isLoading: false,
                    error: null
                })
                return;
            }

            const data = await res.json()
            // Si la respuesta no fue OK (ej:, 404, 500), lanza un error.
            if (!res.ok) {
                const error = { message: data.message, status: res.status };
                throw error;
            }

            setState({
                data,
                isLoading: false,
                error: null
            })
        } catch (error) {
            setState({
                data: null,
                error,
                isLoading: false
            })
        }
    }, [state])

    return {
        data,
        isLoading,
        error,
        fetchData
    }

}


