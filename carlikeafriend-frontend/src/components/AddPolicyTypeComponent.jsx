import { useLocation, useNavigate } from 'react-router-dom';
import { PolicyTypeForm } from './PolicyTypeForm';
import { useEffect, useState } from 'react';
import { useMessageModal } from '../context/MessageModalContext';

// Componente principal para la página de Tipo de Política
export const AddPolicyTypeComponent = () => {
    const { setModalMessage } = useMessageModal(); // Hook para manejar el modal
    const [policyTypeToEdit, setPolicyTypeToEdit] = useState(null);
    const location = useLocation(); // Este hook te da acceso al objeto location, que contiene la URL actual y, lo más importante, el estado de navegación en location.state.
    const navigate = useNavigate(); // <-- Usa useNavigate para la navegación
    // Usa useEffect para actualizar el estado cuando cambie la ubicación (navegación)
    useEffect(() => {
        // Accede a los datos del estado de la navegación
        if (location.state && location.state.policyTypeToEdit) { // Verificamos si existe el estado de navegación y si contiene la propiedad
            setPolicyTypeToEdit(location.state.policyTypeToEdit); // actualizamos el estado local con los datos
        } else {
            setPolicyTypeToEdit(null); // Limpia el estado si no hay datos
        }
    }, [location]); // Vuelve a ejecutar cuando 'location' cambie

    // Esta función se puede usar para setear los datos o mostrar un mensaje de éxito
    const handlePolicyTypeSaved = () => {
        setModalMessage('¡Tipo de Política guardado exitosamente!');
        setPolicyTypeToEdit(null);
        navigate("/administration/policyType-list");
    };

    return (

        <div className="row">
            <div className="col-12 col-md-10 mx-auto">
                <div className="card custom-card-shadow custom-card-background p-4 p-md-5">
                    <h4 className="fw-bold text-center form-title mb-4">
                        {policyTypeToEdit ? 'Edición de tipo de Política' : 'Registro de Nuevo Tipo de Política'}
                    </h4>
                    <PolicyTypeForm
                        policyTypeToEdit={policyTypeToEdit}
                        onPolicyTypeSaved={handlePolicyTypeSaved}
                    />
                </div>
            </div>
        </div>

    );
}
