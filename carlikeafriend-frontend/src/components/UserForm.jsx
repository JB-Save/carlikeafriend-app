import { useUserForm } from '../hooks/useUserForm';
import { useNavigate } from 'react-router-dom';

// Componente del formulario para crear/editar usuarios
export const UserForm = ({ userToEdit, onUserSaved }) => {
  const navigate = useNavigate(); // <-- Usa useNavigate para la navegación

  const {
    userData,
    allRoles,
    isLoadingRole,
    roleError,
    error,
    isLoading,
    handleChange,
    handleCheckListChange,
    handleSubmit,
  } = useUserForm(userToEdit, onUserSaved);

  return (
    <form onSubmit={handleSubmit} className="container">
      <div className="mb-3">
        <label htmlFor="name" className="form-label">Nombre</label>
        <input
          type="text"
          id="name"
          name="name"
          value={userData.name}
          onChange={handleChange}
          className="form-control"
          required
          disabled={isLoading}
        />
      </div>
      <div className="mb-3">
        <label htmlFor="lastName" className="form-label">Apellido</label>
        <input
          type="text"
          id="lastName"
          name="lastName"
          value={userData.lastName}
          onChange={handleChange}
          className="form-control"
          required
          disabled={isLoading}
        />
      </div>
      <div className="mb-3">
        <label htmlFor="email" className="form-label">Email</label>
        <div className="input-group">
          <span className="input-group-text" id="inputGroupPrepend2">@</span>
          <input
            type="email"
            id="email"
            name="email"
            value={userData.email}
            onChange={handleChange}
            className="form-control"
            aria-describedby="inputGroupPrepend2"
            placeholder="name@domain.com"
            required
            disabled
          />
        </div>
      </div>
      <div className="mb-3">
        <label className="form-label">Roles</label>
        <div className="border my-2">
          {roleError &&
            (<div className="d-flex alert alert-danger text-center w-100">
              <p className="m-0"><strong>¡Error! </strong>{roleError}</p>
            </div>)}
          {isLoadingRole && <div className="text-center my-5"><div className="spinner-border text-primary" role="status"></div><p className="text-muted">Cargando los Roles...</p></div>}
          <div className="d-flex justify-content-start align-items-center w-100 flex-wrap">
            {allRoles.map((role, index) => (
              <div className="form-check mx-4 my-2" key={index} style={{ width: '165px' }}>
                <input
                  type="checkbox"
                  id={`role-${index}`}
                  name="roles"
                  value={role.id}
                  className="form-check-input"
                  checked={userData.roles.some(val => val === role.id)}
                  onChange={handleCheckListChange}
                  disabled={isLoading}
                />
                <label className="form-check-label" htmlFor={`role-${index}`}>
                  {role.name}
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
          onClick={() => navigate("/administration/user-list")}
          disabled={isLoading}
        ><i className="bi bi-arrow-left me-2"></i>
          Regresar
        </button>
        <button
          type="submit"
          className="btn btn-success rounded-3"
          disabled={isLoading}
        >
          {isLoading ? 'Guardando...' : userToEdit ? 'Actualizar Usuario' : ''}
        </button>
      </div>
    </form>
  );
}

