import { useEffect, useState } from "react"

export const useDebounce = (value, delay) => {

    const [debounceValue, setDebounceValue] = useState(value);

    useEffect(() => {
        // Establecer un temporizador para actualizar el valor debounced
        const handler = setTimeout(() => {
            setDebounceValue(value);
        }, delay);

        // Limpiar el temporizador si el valor cambia de nuevo (cancelar la llamada anterior)
        return () => {
            clearTimeout(handler);
        };

    }, [value, delay]);  // Solo se re-ejecuta si el valor o el delay cambian

    return debounceValue;

}
