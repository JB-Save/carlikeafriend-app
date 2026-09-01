import React from 'react'
import { useEffect } from 'react';

export const ToastNotification = ({ show, message, type, onClose }) => {

    // Auto-cierre opcional después de 3 segundos
    useEffect(() => {
        if (show) {
            const timer = setTimeout(() => onClose(), 3000);
            return () => clearTimeout(timer);
        }
    }, [show, onClose]);

    if (!message) return null;

    const iconClass = type === 'success' ? 'bi-check-circle-fill text-success' : 'bi-exclamation-triangle-fill text-danger';

    return (
        <div className="toast-container position-fixed bottom-0 end-0 p-3" style={{ zIndex: 1100 }}>
            <div className={`toast align-items-center text-white bg-dark border-0 ${show ? 'show' : 'hide'}`} role="alert">
                <div className="d-flex">
                    <div className="toast-body">
                        <i className={`bi ${iconClass} me-2`}></i>
                        {message}
                    </div>
                    <button type="button" className="btn-close btn-close-white me-2 m-auto" onClick={onClose}></button>
                </div>
            </div>
        </div>
    );
}


