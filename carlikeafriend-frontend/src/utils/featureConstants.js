// Agrupamos las características especiales para poder evaluarlas individualmente
export const SPECIAL_FEATURES = {
    PASAJERO: ['pasajero', 'pasajeros'],
    PUERTA: ['puerta', 'puertas'],
    EQUIPAJE: ['equipaje']
};

// Generamos un arreglo plano con todas las características que queremos ocultar en los filtros
export const EXCLUDED_FILTER_FEATURES = [
    ...SPECIAL_FEATURES.PASAJERO,
    ...SPECIAL_FEATURES.PUERTA,
    ...SPECIAL_FEATURES.EQUIPAJE
];