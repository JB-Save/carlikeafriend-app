import { useLocation, useNavigate } from 'react-router-dom';
import { PolicyForm } from './PolicyForm';
import { useEffect, useState } from 'react';
import { useMessageModal } from '../context/MessageModalContext';

export const AddPolicyComponent = () => {
    const { setModalMessage } = useMessageModal(); // Hook para manejar el modal
    const [policyToEdit, setPolicyToEdit] = useState(null);
    const location = useLocation(); // Este hook te da acceso al objeto location, que contiene la URL actual y, lo más importante, el estado de navegación en location.state.
    const navigate = useNavigate(); // <-- Usa useNavigate para la navegación
    // Usa useEffect para actualizar el estado cuando cambie la ubicación (navegación)
    useEffect(() => {
        // Accede a los datos del estado de la navegación
        if (location.state && location.state.policyToEdit) { // Verificamos si existe el estado de navegación
            setPolicyToEdit(location.state.policyToEdit); // actualizamos el estado local
        } else {
            setPolicyToEdit(null); // Limpia el estado si no hay datos
        }
    }, [location]); // Vuelve a ejecutar cuando 'location' cambie

    // Esta función se puede usar para setear los datos o mostrar un mensaje de éxito
    const handlePolicySaved = () => {
        setModalMessage('¡Política guardada exitosamente!');
        setPolicyToEdit(null);
        navigate("/administration/policy-list");
    };

    return (

        <div className="row">
            <div className="col-12 col-md-10 mx-auto">
                <div className="card custom-card-shadow custom-card-background p-4 p-md-5">
                    <h4 className="fw-bold text-center form-title mb-4">
                        {policyToEdit ? 'Edición de Política' : 'Registro de Nueva Política'}
                    </h4>
                    <PolicyForm
                        policyToEdit={policyToEdit}
                        onPolicySaved={handlePolicySaved}
                    />
                </div>
            </div>
        </div>

    );
}
