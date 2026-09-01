export const getInitialPickupTime = () => {
    const now = new Date();
    const defaultTime = new Date(now);
    defaultTime.setHours(10, 0, 0, 0);

    // Si la hora actual es antes de las 10:00 a.m., es seguro usar las 10:00 a.m.
    if (now < defaultTime) {
        return defaultTime;
    }

    // Si ya pasaron las 10:00 a.m., calculamos el próximo bloque hábil (Paridad con getNextAvailableBlock)
    const nextBlock = new Date(now);
    const minutes = nextBlock.getMinutes();

    if (minutes < 30) {
        nextBlock.setMinutes(30, 0, 0);
    } else {
        nextBlock.setHours(nextBlock.getHours() + 1, 0, 0, 0);
    }

    return nextBlock;
};

export const calculateRentalDays = (pickup, returnDate) => {
    if (!pickup || !returnDate) return 0;

    // Diferencia en milisegundos convertida a minutos
    const diffInMinutes = (returnDate.getTime() - pickup.getTime()) / (1000 * 60);

    // 1440 minutos = 24 horas. Paridad exacta con el backend.
    return Math.max(1, Math.ceil(diffInMinutes / 1440.0));
};

/**
 * Valida si las fechas de reserva guardadas en el contexto han caducado o se cruzan.
 * Si es así, calcula y retorna las nuevas fechas válidas. Si están bien, retorna null.
 */
export const validateAndCorrectBookingDates = (dateRange, pickupTime, returnTime) => {
    if (!dateRange || !dateRange[0] || !dateRange[1] || !pickupTime || !returnTime) {
        return null; // Faltan datos, no se puede corregir
    }

    let corrected = false;
    let [pickupDate, returnDate] = dateRange;
    let newPickupTime = new Date(pickupTime);
    let newReturnTime = new Date(returnTime);

    const now = new Date();

    // BUFFER DE SEGURIDAD: 10 minutos de gracia. Si faltan menos de 10 minutos
    // para que la reserva expire, la forzamos al próximo bloque para que pase la red a tiempo.
    const timeWithBuffer = new Date(now.getTime() + 10 * 60000);

    // // 1. Validar si la recogida ya está en el pasado (o en zona de peligro)
    const pickupDateTime = new Date(pickupDate);
    pickupDateTime.setHours(newPickupTime.getHours(), newPickupTime.getMinutes(), 0, 0);

    if (pickupDateTime.getTime() < timeWithBuffer.getTime()) {
        const nextBlock = new Date(timeWithBuffer);
        const minutes = nextBlock.getMinutes();
        if (minutes < 30) {
            nextBlock.setMinutes(30, 0, 0);
        } else {
            nextBlock.setHours(nextBlock.getHours() + 1, 0, 0, 0);
        }

        newPickupTime = nextBlock;
        // Si el ajuste cruzó la medianoche, actualizamos también la fecha base
        pickupDate = new Date(nextBlock);
        pickupDate.setHours(0, 0, 0, 0);
        corrected = true;
    }

    // 2. Validar que la entrega siga siendo al menos 1 hora después
    const updatedPickupDateTime = new Date(pickupDate);
    updatedPickupDateTime.setHours(newPickupTime.getHours(), newPickupTime.getMinutes(), 0, 0);

    let returnDateTime = new Date(returnDate);
    returnDateTime.setHours(newReturnTime.getHours(), newReturnTime.getMinutes(), 0, 0);

    // Sumamos 1 hora en milisegundos a la recogida
    const minReturnDateTime = new Date(updatedPickupDateTime.getTime() + (60 * 60 * 1000));

    if (returnDateTime.getTime() < minReturnDateTime.getTime()) {
        newReturnTime = new Date(minReturnDateTime);
        returnDate = new Date(minReturnDateTime);
        returnDate.setHours(0, 0, 0, 0);
        corrected = true;
    }

    // 3. Validar el límite máximo absoluto de 30 días (Bloques de 24 horas)
    const finalPickupDateTime = new Date(pickupDate);
    finalPickupDateTime.setHours(newPickupTime.getHours(), newPickupTime.getMinutes(), 0, 0);

    const finalReturnDateTime = new Date(returnDate);
    finalReturnDateTime.setHours(newReturnTime.getHours(), newReturnTime.getMinutes(), 0, 0);

    const diffInMinutes = (finalReturnDateTime.getTime() - finalPickupDateTime.getTime()) / (1000 * 60);
    const totalDays = Math.ceil(diffInMinutes / 1440.0);

    if (totalDays > 30) {
        // Si excede los 30 días (por ej. 30 días y 30 minutos), forzamos la fecha de entrega
        // para que sea exactamente el máximo permitido (30 días de 24h) desde la recogida.
        const maxReturnDateTime = new Date(finalPickupDateTime.getTime() + (30 * 24 * 60 * 60 * 1000));

        newReturnTime = new Date(maxReturnDateTime);
        returnDate = new Date(newReturnTime);
        returnDate.setHours(0, 0, 0, 0); // Limpiamos la hora para que el componente DatePicker no se confunda
        corrected = true;
    }

    // Retornamos el objeto corregido solo si hubo mutaciones, de lo contrario null
    return corrected ? {
        dateRange: [pickupDate, returnDate],
        pickupTime: newPickupTime,
        returnTime: newReturnTime
    } : null;
};

// NUEVAS FUNCIONES DE VALIDACIÓN ABSOLUTA PARA LA INTERFAZ (UI)
/**
 * Evalúa cada opción del selector de horas (dropdown) para la Recogida.
 * Usa el tiempo absoluto para bloquear correctamente los horarios pasados en el mismo día.
 */
export const isTimeOptionValidForPickup = (pickupDate, timeOption, bufferMinutes = 10) => {
    if (!pickupDate || !timeOption) return true;

    const now = new Date();
    const timeWithBuffer = new Date(now.getTime() + bufferMinutes * 60000);

    // Fusionamos la fecha seleccionada con la opción de hora a evaluar
    const absoluteOption = new Date(pickupDate);
    absoluteOption.setHours(timeOption.getHours(), timeOption.getMinutes(), 0, 0);

    // Si la opción fusionada es mayor a nuestro margen, la hora es válida para mostrarse.
    return absoluteOption.getTime() >= timeWithBuffer.getTime();
};

/**
 * Evalúa cada opción del selector de horas (dropdown) para la Entrega.
 * Evaluando el tiempo de forma continua (Timestamp).
 */
export const isTimeOptionValidForReturn = (pickupDate, pickupTime, returnDate, timeOption, minHours = 1) => {
    if (!pickupDate || !pickupTime || !returnDate || !timeOption) return true;

    // 1. Construir Timestamp Absoluto de la Recogida exacta
    const absolutePickup = new Date(pickupDate);
    absolutePickup.setHours(pickupTime.getHours(), pickupTime.getMinutes(), 0, 0);

    // 2. Sumar el tiempo mínimo requerido (1 hora)
    const minAbsoluteReturn = new Date(absolutePickup.getTime() + (minHours * 60 * 60 * 1000));

    // 3. Fusionar la fecha de entrega con la opción de hora que el calendario quiere renderizar
    const absoluteOption = new Date(returnDate);
    absoluteOption.setHours(timeOption.getHours(), timeOption.getMinutes(), 0, 0);

    // 4. Solo se mostrará en la lista si cumple la regla matemática, sin importar en qué día caiga.
    return absoluteOption.getTime() >= minAbsoluteReturn.getTime();
};

// Helpers de formateo para mostrar datos de fechas de mis reservas
export const formatDateTime = (isoString) => {
    if (!isoString) return { date: 'N/A', time: '' };
    const dateObj = new Date(isoString);

    const date = dateObj.toLocaleDateString('es-CO', { day: 'numeric', month: 'short', year: 'numeric' });
    const time = dateObj.toLocaleTimeString('es-CO', { hour: '2-digit', minute: '2-digit', hour12: true });

    return { date, time };
};

export const formatPeriod = (startIso, endIso) => {
    if (!startIso || !endIso) return 'N/A';
    const start = new Date(startIso);
    const end = new Date(endIso);

    const options = { day: 'numeric', month: 'short' };
    const startStr = start.toLocaleDateString('es-CO', options);
    const endStr = end.toLocaleDateString('es-CO', { ...options, year: 'numeric' });

    return `${startStr} - ${endStr}`;
};

// Función auxiliar que extrae la fecha respetando la zona horaria local del navegador del usuario
export const formatToLocalDateString = (dateObj) => {
    if (typeof dateObj === 'string') return dateObj; // Si ya es string, devolverlo
    if (!dateObj) return null;

    const year = dateObj.getFullYear();
    const month = String(dateObj.getMonth() + 1).padStart(2, '0');
    const day = String(dateObj.getDate()).padStart(2, '0');

    return `${year}-${month}-${day}`;
};