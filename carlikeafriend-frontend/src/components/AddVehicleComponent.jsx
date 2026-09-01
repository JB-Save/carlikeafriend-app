import { useLocation, useNavigate } from 'react-router-dom';
import { VehicleForm } from './VehicleForm';
import { useEffect, useState } from 'react';
import { useMessageModal } from '../context/MessageModalContext';

export const AddVehicleComponent = () => {
    const { setModalMessage } = useMessageModal(); // Hook para manejar el modal
    const [vehicleToEdit, setVehicleToEdit] = useState(null);
    const location = useLocation(); // Este hook te da acceso al objeto location, que contiene la URL actual y, lo más importante, el estado de navegación en location.state.
    const navigate = useNavigate(); // <-- Usa useNavigate para la navegación
    // Usa useEffect para actualizar el estado cuando cambie la ubicación (navegación)
    useEffect(() => {
        // Accede a los datos del estado de la navegación
        if (location.state && location.state.vehicleToEdit) { // Verificamos si existe el estado de navegación
            setVehicleToEdit(location.state.vehicleToEdit); // actualizamos el estado local
        } else {
            setVehicleToEdit(null); // Limpia el estado si no hay datos
        }
    }, [location]); // Vuelve a ejecutar cuando 'location' cambie

    // Esta función se puede usar para setear los datos o mostrar un mensaje de éxito
    const handleVehicleSaved = () => {
        setModalMessage('¡Vehículo guardado exitosamente!');
        setVehicleToEdit(null);
        navigate("/administration/vehicle-list");
    };

    return (

        <div className="row">
            <div className="col-12 col-md-10 mx-auto">
                <div className="card custom-card-shadow custom-card-background p-4 p-md-5">
                    <h4 className="fw-bold text-center form-title mb-4">
                        {vehicleToEdit ? 'Edición de Vehículo' : 'Registro de Nuevo Vehículo'}
                    </h4>
                    <VehicleForm
                        vehicleToEdit={vehicleToEdit}
                        onVehicleSaved={handleVehicleSaved}
                    />
                </div>
            </div>
        </div>

    );
}
