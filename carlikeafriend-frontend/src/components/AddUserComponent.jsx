import { useLocation, useNavigate } from 'react-router-dom';
import { UserForm } from './UserForm';
import { useEffect, useState } from 'react';
import { useMessageModal } from '../context/MessageModalContext';

// Componente principal para la página del usuario
export const AddUserComponent = () => {
  const { setModalMessage } = useMessageModal(); // Hook para manejar el modal
  const [userToEdit, setUserToEdit] = useState(null);
  const location = useLocation(); // Este hook te da acceso al objeto location, que contiene la URL actual y, lo más importante, el estado de navegación en location.state.
  const navigate = useNavigate(); // <-- Usa useNavigate para la navegación
  // Usa useEffect para actualizar el estado cuando cambie la ubicación (navegación)
  useEffect(() => {
    // Accede a los datos del estado de la navegación
    if (location.state && location.state.userToEdit) { // Verificamos si existe el estado de navegación y si contiene la propiedad userToEdit
      setUserToEdit(location.state.userToEdit); // actualizamos el estado local con los datos del usuario
    } else {
      setUserToEdit(null); // Limpia el estado si no hay datos del usuario
    }
  }, [location]); // Vuelve a ejecutar cuando 'location' cambie

  // Esta función se puede usar para setear los datos del usuario o mostrar un mensaje de éxito
  const handleUserSaved = () => {
    setModalMessage('¡Usuario guardado exitosamente!');
    setUserToEdit(null);
    navigate("/administration/user-list");
  };

  return (

    <div className="row">
      <div className="col-12 col-xl-10 mx-auto">
        <div className="card custom-card-shadow custom-card-background p-4 p-md-5">
          <div className="d-flex align-items-center mb-4 pb-2 border-bottom">
            <i className="bi bi-person-gear display-6 text-primary me-3"></i>
            <h4 className="fw-bold form-title m-0">
              {userToEdit ? 'Edición de Perfil de Usuario' : ''}
            </h4>
          </div>
          <UserForm
            userToEdit={userToEdit}
            onUserSaved={handleUserSaved}
          />
        </div>
      </div>
    </div>
  );
}
