import { useLocation, useNavigate } from 'react-router-dom';
import { TransferFeeForm } from './TransferFeeForm';
import { useEffect, useState } from 'react';
import { useMessageModal } from '../context/MessageModalContext';

export const AddTransferFeeComponent = () => {
    const { setModalMessage } = useMessageModal(); // Hook para manejar el modal
    const [transferFeeToEdit, setTransferFeeToEdit] = useState(null);
    const location = useLocation(); // Este hook te da acceso al objeto location, que contiene la URL actual y, lo más importante, el estado de navegación en location.state.
    const navigate = useNavigate(); // <-- Usa useNavigate para la navegación
    // Usa useEffect para actualizar el estado cuando cambie la ubicación (navegación)
    useEffect(() => {
        // Accede a los datos del estado de la navegación
        if (location.state && location.state.transferFeeToEdit) { // Verificamos si existe el estado de navegación
            setTransferFeeToEdit(location.state.transferFeeToEdit); // actualizamos el estado local
        } else {
            setTransferFeeToEdit(null); // Limpia el estado si no hay datos
        }
    }, [location]); // Vuelve a ejecutar cuando 'location' cambie

    // Esta función se puede usar para setear los datos o mostrar un mensaje de éxito
    const handleTransferFeeSaved = () => {
        setModalMessage('¡Tarifa de transferencia guardada exitosamente!');
        setTransferFeeToEdit(null);
        navigate("/administration/transferFee-list");
    };

    return (

        <div className="row">
            <div className="col-12 col-md-10 mx-auto">
                <div className="card custom-card-shadow custom-card-background p-4 p-md-5">
                    <h4 className="fw-bold text-center form-title mb-4">
                        {transferFeeToEdit ? 'Edición de Tarifa' : 'Registro de Nueva Tarifa'}
                    </h4>
                    <TransferFeeForm
                        transferFeeToEdit={transferFeeToEdit}
                        onTransferFeeSaved={handleTransferFeeSaved}
                    />
                </div>
            </div>
        </div>

    );
}
