import { useLocation, useNavigate } from 'react-router-dom';
import { CityForm } from './CityForm';
import { useEffect, useState } from 'react';
import { useMessageModal } from '../context/MessageModalContext';

// Componente principal para la página de la ciudad
export const AddCityComponent = () => {
  const { setModalMessage } = useMessageModal(); // Hook para manejar el modal
  const [cityToEdit, setCityToEdit] = useState(null);
  const location = useLocation(); // Este hook te da acceso al objeto location, que contiene la URL actual y, lo más importante, el estado de navegación en location.state.
  const navigate = useNavigate(); // <-- Usa useNavigate para la navegación

  // Usa useEffect para actualizar el estado cuando cambie la ubicación (navegación)
  useEffect(() => {
    // Accede a los datos del estado de la navegación
    if (location.state && location.state.cityToEdit) { // Verificamos si existe el estado de navegación
      setCityToEdit(location.state.cityToEdit); // actualizamos el estado local
    } else {
      setCityToEdit(null); // Limpia el estado si no hay datos
    }
  }, [location]); // Vuelve a ejecutar cuando 'location' cambie

  // Esta función se puede usar para setear los datos o mostrar un mensaje de éxito
  const handleCitySaved = () => {
    setModalMessage('¡Ciudad guardada exitosamente!');
    setCityToEdit(null);
    navigate("/administration/city-list");
  };

  return (

    <div className="row">
      <div className="col-12 col-md-10 mx-auto">
        <div className="card custom-card-shadow custom-card-background p-4 p-md-5">
          <h4 className="fw-bold text-center form-title mb-4">
            {cityToEdit ? 'Edición de Ciudad' : 'Registro de Nueva Ciudad'}
          </h4>
          <CityForm
            cityToEdit={cityToEdit}
            onCitySaved={handleCitySaved}
          />
        </div>
      </div>
    </div>

  );
}
