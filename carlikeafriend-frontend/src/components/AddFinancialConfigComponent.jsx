import { useLocation, useNavigate } from 'react-router-dom';
import { FinancialConfigForm } from './FinancialConfigForm';
import { useEffect, useState } from 'react';
import { useMessageModal } from '../context/MessageModalContext';

// Componente principal para la página de configuración financiera
export const AddFinancialConfigComponent = () => {
  const { setModalMessage } = useMessageModal(); // Hook para manejar el modal
  const navigate = useNavigate(); // <-- Usa useNavigate para la navegación

  // Esta función se puede usar para mostrar un mensaje de éxito
  const handleFinancialConfigSaved = () => {
    setModalMessage('¡Configuración financiera guardada exitosamente!');
    navigate("/administration/");
  };

  return (

    <div className="row">
      <div className="col-12 col-md-10 mx-auto">
        <div className="card custom-card-shadow custom-card-background p-4 p-md-5">
          <h4 className="fw-bold text-center form-title mb-4">
            Edición de Configuración Financiera
          </h4>
          <FinancialConfigForm
            onFinancialConfigSaved={handleFinancialConfigSaved}
          />
        </div>
      </div>
    </div>
  );
}
