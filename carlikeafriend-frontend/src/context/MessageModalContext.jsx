import { createContext, useContext, useMemo, useState, useCallback } from "react";
import { MessageModalComponent } from "../components/MessageModalComponent";

// Se Crea el contexto
export const MessageModalContext = createContext(null);

// Componente proveedor que contendrá el estado y las funciones
export const MessageModalProvider = ({ children }) => {
    const [modalMessage, setModalMessage] = useState(null);

    const handleCloseModal = useCallback(() => {
        setModalMessage(null);
    }, []);

    const contextValue = useMemo(() => ({
        setModalMessage,
        handleCloseModal
    }), [handleCloseModal]);

    return (
        <MessageModalContext.Provider value={contextValue}>
            {children}
            {/* El componente del modal se renderiza aquí una sola vez */}
            <MessageModalComponent message={modalMessage} onClose={handleCloseModal} />
        </MessageModalContext.Provider>
    );
};

// Hook personalizado para usar el contexto de manera sencilla
export const useMessageModal = () => {
    const context = useContext(MessageModalContext);
    if (!context) {
        throw new Error('useMessageModal debe ser usado dentro de un MessageModalProvider');
    }
    return context;
}