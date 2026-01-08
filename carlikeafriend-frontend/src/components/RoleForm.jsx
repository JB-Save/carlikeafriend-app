import { useRoleForm } from '../hooks/useRoleForm';
import { useNavigate } from 'react-router-dom';

// Componente del formulario para crear/editar roles
export const RoleForm = ({ roleToEdit, onRoleSaved }) => {
  const navigate = useNavigate(); // <-- Usa useNavigate para la navegación

  const {
    roleData,
    allPermissions,
    isLoadingPermission,
    permissionError,
    error,
    isLoading,
    handleChange,
    handleCheckListChange,
    handleSubmit,
  } = useRoleForm(roleToEdit, onRoleSaved);

  return (
    <form onSubmit={handleSubmit} className="container">
      <div className="mb-3">
        <label htmlFor="name" className="form-label">Nombre</label>
        <input
          type="text"
          id="name"
          name="name"
          value={roleData.name}
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
          value={roleData.description}
          onChange={handleChange}
          rows="3"
          className="form-control"
          required
          disabled={isLoading}
        ></textarea>
      </div>
      <div className="mb-3">
        <label className="form-label">Permisos</label>
        <div className="border my-2">
          {permissionError &&
            (<div className="d-flex alert alert-danger text-center w-100">
              <p className="m-0"><strong>¡Error! </strong>{permissionError}</p>
            </div>)}
          {isLoadingPermission && <div className="text-center my-5"><div className="spinner-border text-primary" role="status"></div><p className="text-muted">Cargando permisos...</p></div>}
          <div className="d-flex justify-content-start align-items-center w-100 flex-wrap">
            {allPermissions.map((permission, index) => (
              <div className="form-check mx-4 my-2" key={index} style={{ width: '165px' }}>
                <input
                  type="checkbox"
                  id={`permission-${index}`}
                  name="permissions"
                  value={permission.id}
                  className="form-check-input"
                  checked={roleData.permissions.some(val => val === permission.id)}
                  onChange={handleCheckListChange}
                  disabled={isLoading}
                />
                <label className="form-check-label" htmlFor={`permission-${index}`}>
                  {permission.name}
                </label>
              </div>
            ))}
          </div>
        </div>
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
          onClick={() => navigate("/administration/role-list")}
          disabled={isLoading}
        ><i className="bi bi-arrow-left me-2"></i>
          Regresar
        </button>
        <button
          type="submit"
          className="btn btn-success rounded-3"
          disabled={isLoading}
        >
          {isLoading ? 'Guardando...' : roleToEdit ? 'Actualizar Rol' : 'Crear Rol'}
        </button>
      </div>
    </form>
  );
}

