import { useLocation, useNavigate } from 'react-router-dom';
import { FeatureForm } from './FeatureForm';
import { useEffect, useState } from 'react';
import { useMessageModal } from '../context/MessageModalContext';

// Componente principal para la página de la Característica
export const AddFeatureComponent = () => {
  const { setModalMessage } = useMessageModal(); // Hook para manejar el modal
  const [featureToEdit, setFeatureToEdit] = useState(null);
  const location = useLocation(); // Este hook te da acceso al objeto location, que contiene la URL actual y, lo más importante, el estado de navegación en location.state.
  const navigate = useNavigate(); // <-- Usa useNavigate para la navegación
  // Usa useEffect para actualizar el estado cuando cambie la ubicación (navegación)
  useEffect(() => {
    // Accede a los datos del estado de la navegación
    if (location.state && location.state.featureToEdit) { // Verificamos si existe el estado de navegación y si contiene la propiedad featureToEdit
      setFeatureToEdit(location.state.featureToEdit); // actualizamos el estado local con los datos de la característica
    } else {
      setFeatureToEdit(null); // Limpia el estado si no hay datos de la característica
    }
  }, [location]); // Vuelve a ejecutar cuando 'location' cambie

  // Esta función se puede usar para setear los datos de la característica o mostrar un mensaje de éxito
  const handleFeatureSaved = () => {
    setModalMessage('¡Característica guardada exitosamente!');
    setFeatureToEdit(null);
    navigate("/administration/feature-list");
  };

  return (

    <div col="row">
      <div className="col-12 col-md-8 mx-auto">
        <div className="card card-shadow card-background p-4">
          <h1 className="fs-3 fw-bold text-center form-title mb-4">
            {featureToEdit ? 'Editar Característica' : 'Crear Característica'}
          </h1>
          <FeatureForm
            featureToEdit={featureToEdit}
            onFeatureSaved={handleFeatureSaved}
          />
        </div>
      </div>
    </div>

  );
}
