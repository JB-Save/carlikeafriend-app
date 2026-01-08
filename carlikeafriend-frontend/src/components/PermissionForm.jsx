import { usePermissionForm } from '../hooks/usePermissionForm';
import { useNavigate } from 'react-router-dom';

// Componente del formulario para crear/editar permisos
export const PermissionForm = ({ permissionToEdit, onPermissionSaved }) => {

  const navigate = useNavigate(); // <-- Usa useNavigate para la navegación

  const {
    permissionData,
    error,
    isLoading,
    handleChange,
    handleSubmit,
  } = usePermissionForm(permissionToEdit, onPermissionSaved);

  return (
    <form onSubmit={handleSubmit} className="container">
      <div className="mb-3">
        <label htmlFor="name" className="form-label">Nombre</label>
        <input
          type="text"
          id="name"
          name="name"
          value={permissionData.name}
          onChange={handleChange}
          className="form-control"
          required
          disabled={isLoading}
        />
      </div>
      <div className="mb-3">
        <label htmlFor="description" className="form-label">Descripción</label>
        <textarea
          id="description"
          name="description"
          value={permissionData.description}
          onChange={handleChange}
          rows="3"
          className="form-control"
          required
          disabled={isLoading}
        ></textarea>
      </div>

      {error && (
        <div className="alert alert-danger" role="alert">
          <strong>¡Error!</strong> {error}
        </div>
      )}
      <div className="d-flex justify-content-between">
        <button
          type="button"
          className="btn form-btn rounded-3"
          onClick={() => navigate("/administration/permission-list")}
          disabled={isLoading}
        ><i className="bi bi-arrow-left me-2"></i>
          Regresar
        </button>
        <button
          type="submit"
          className="btn btn-success rounded-3"
          disabled={isLoading}
        >
          {isLoading ? 'Guardando...' : permissionToEdit ? 'Actualizar Permiso' : 'Crear Permiso'}
        </button>
      </div>
    </form>
  );
}

