import { useLocation, useNavigate } from 'react-router-dom';
import { MakeForm } from './MakeForm';
import { useEffect, useState } from 'react';
import { useMessageModal } from '../context/MessageModalContext';

// Componente principal para la página de la marca
export const AddMakeComponent = () => {
  const { setModalMessage } = useMessageModal(); // Hook para manejar el modal
  const [makeToEdit, setMakeToEdit] = useState(null);
  const location = useLocation(); // Este hook te da acceso al objeto location, que contiene la URL actual y, lo más importante, el estado de navegación en location.state.
  const navigate = useNavigate(); // <-- Usa useNavigate para la navegación

  // Usa useEffect para actualizar el estado cuando cambie la ubicación (navegación)
  useEffect(() => {
    // Accede a los datos del estado de la navegación
    if (location.state && location.state.makeToEdit) { // Verificamos si existe el estado de navegación
      setMakeToEdit(location.state.makeToEdit); // actualizamos el estado local
    } else {
      setMakeToEdit(null); // Limpia el estado si no hay datos
    }
  }, [location]); // Vuelve a ejecutar cuando 'location' cambie

  // Esta función se puede usar para setear los datos o mostrar un mensaje de éxito
  const handleMakeSaved = () => {
    setModalMessage('¡Marca del producto guardada exitosamente!');
    setMakeToEdit(null);
    navigate("/administration/make-list");
  };

  return (

    <div className="row">
      <div className="col-12 col-md-10 mx-auto">
        <div className="card custom-card-shadow custom-card-background p-4 p-md-5">
          <h4 className="fw-bold text-center form-title mb-4">
            {makeToEdit ? 'Edición de Marca' : 'Registro de Nueva Marca'}
          </h4>
          <MakeForm
            makeToEdit={makeToEdit}
            onMakeSaved={handleMakeSaved}
          />
        </div>
      </div>
    </div>

  );
}
