import { createContext, useCallback, useContext, useMemo, useState } from 'react';
import { getInitialPickupTime } from '../utils/dateHelpers';

// 1. Creamos el contexto
export const BookingContext = createContext();

// 2. Proveedor del Contexto
export const BookingProvider = ({ children }) => {
  // Inicializamos valores por defecto (10:00 AM)
  const defaultTime = new Date();
  defaultTime.setHours(10, 0, 0, 0);

  // Estado global de la reserva
  const [bookingData, setBookingData] = useState({
    pickupBranch: null,
    returnBranch: null,
    differentReturnBranch: false,
    dateRange: [null, null],
    pickupTime: getInitialPickupTime(),
    returnTime: defaultTime
  });

  // Función para actualizar datos de manera segura mezclando con lo anterior
  const updateBookingData = useCallback((newData) => {
    setBookingData(prev => ({ ...prev, ...newData }));
  }, []);

  const contextValue = useMemo(() => ({
    bookingData,
    updateBookingData
  }), [bookingData, updateBookingData]);

  return (
    <BookingContext.Provider value={contextValue}>
      {children}
    </BookingContext.Provider>
  );
};

// 3. Custom Hook para consumir el contexto fácilmente
export const useBooking = () => {
  const context = useContext(BookingContext);
  if (!context) {
    throw new Error("useBooking debe ser usado dentro de un BookingProvider");
  }
  return context;
};
