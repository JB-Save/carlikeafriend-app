import { useLocation, useNavigate } from 'react-router-dom';
import { PermissionForm } from './PermissionForm';
import { useEffect, useState } from 'react';
import { useMessageModal } from '../context/MessageModalContext';

// Componente principal para la página del permiso
export const AddPermissionComponent = () => {
  const { setModalMessage } = useMessageModal(); // Hook para manejar el modal
  const [permissionToEdit, setPermissionToEdit] = useState(null);
  const location = useLocation(); // Este hook te da acceso al objeto location, que contiene la URL actual y, lo más importante, el estado de navegación en location.state.
  const navigate = useNavigate(); // <-- Usa useNavigate para la navegación

  // Usa useEffect para actualizar el estado cuando cambie la ubicación (navegación)
  useEffect(() => {
    // Accede a los datos del estado de la navegación
    if (location.state && location.state.permissionToEdit) { // Verificamos si existe el estado de navegación y si contiene la propiedad permissionToEdit
      setPermissionToEdit(location.state.permissionToEdit); // actualizamos el estado local con los datos del permiso
    } else {
      setPermissionToEdit(null); // Limpia el estado si no hay datos del permiso
    }
  }, [location]); // Vuelve a ejecutar cuando 'location' cambie

  // Esta función se puede usar para setear los datos del permiso o mostrar un mensaje de éxito
  const handlePermissionSaved = () => {
    setModalMessage('¡Permiso guardado exitosamente!');
    setPermissionToEdit(null);
    navigate("/administration/permission-list");
  };

  return (

    <div className="row">
      <div className="col-12 col-md-10 mx-auto">
        <div className="card custom-card-shadow custom-card-background p-4 p-md-5">
          <h4 className="fw-bold text-center form-title mb-4">
            {permissionToEdit ? 'Edición de Permiso' : 'Registro de Nuevo Permiso'}
          </h4>
          <PermissionForm
            permissionToEdit={permissionToEdit}
            onPermissionSaved={handlePermissionSaved}
          />
        </div>
      </div>
    </div>

  );
}
