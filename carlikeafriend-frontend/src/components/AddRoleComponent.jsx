import { useLocation, useNavigate } from 'react-router-dom';
import { RoleForm } from './RoleForm';
import { useEffect, useState } from 'react';
import { useMessageModal } from '../context/MessageModalContext';

// Componente principal para la página del rol
export const AddRoleComponent = () => {
  const { setModalMessage } = useMessageModal(); // Hook para manejar el modal
  const [roleToEdit, setRoleToEdit] = useState(null);
  const location = useLocation(); // Este hook te da acceso al objeto location, que contiene la URL actual y, lo más importante, el estado de navegación en location.state.
  const navigate = useNavigate(); // <-- Usa useNavigate para la navegación
  // Usa useEffect para actualizar el estado cuando cambie la ubicación (navegación)
  useEffect(() => {
    // Accede a los datos del estado de la navegación
    if (location.state && location.state.roleToEdit) { // Verificamos si existe el estado de navegación y si contiene la propiedad roleToEdit
      setRoleToEdit(location.state.roleToEdit); // actualizamos el estado local con los datos del rol
    } else {
      setRoleToEdit(null); // Limpia el estado si no hay datos del rol
    }
  }, [location]); // Vuelve a ejecutar cuando 'location' cambie

  // Esta función se puede usar para setear los datos del rol o mostrar un mensaje de éxito
  const handleRoleSaved = () => {
    setModalMessage('¡Rol guardado exitosamente!');
    setRoleToEdit(null);
    navigate("/administration/role-list");
  };

  return (

    <div className="row">
      <div className="col-12 col-md-10 mx-auto">
        <div className="card custom-card-shadow custom-card-background p-4 p-md-5">
          <h4 className="fw-bold text-center form-title mb-4">
            {roleToEdit ? 'Edición de Rol' : 'Registro de Nuevo Rol'}
          </h4>
          <RoleForm
            roleToEdit={roleToEdit}
            onRoleSaved={handleRoleSaved}
          />
        </div>
      </div>
    </div>

  );
}
