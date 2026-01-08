import { useCallback, useMemo } from "react";

export const useCurrencyFormatter = () => {
    //Hook para formatear montos numéricos a la moneda local (COP - Colombia).
    // Usamos useMemo para crear el formateador solo una vez.
    const formatter = useMemo(() => {
        return new Intl.NumberFormat('es-CO', {
            style: 'currency',
            currency: 'COP',
            minimumFractionDigits: 0,
        });
    }, []);

    const formatCurrency = useCallback((amount) => {
        if (amount === undefined || amount === null || isNaN(amount)) {
            return '$ 0';
        }
        return formatter.format(amount);
    }, [formatter]);

    return { formatCurrency };
}
