import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { useRoleForm } from '../hooks/useRoleForm';
import { useNavigate } from 'react-router-dom';
import { yupResolver } from '@hookform/resolvers/yup';
import { roleSchema } from '../utils/validationSchema';

// Componente del formulario para crear/editar roles
export const RoleForm = ({ roleToEdit, onRoleSaved }) => {
  const navigate = useNavigate();

  const {
    allPermissions,
    isLoadingPermission,
    permissionError,
    error: apiError,
    isSubmittingForm,
    submitRoleData
  } = useRoleForm(roleToEdit, onRoleSaved);

  const { register, handleSubmit, reset, formState: { errors } } = useForm({
    resolver: yupResolver(roleSchema),
    defaultValues: { permissions: [] }
  });

  useEffect(() => {
    if (roleToEdit && !isLoadingPermission) {
      reset({
        name: roleToEdit.name,
        description: roleToEdit.description,
        permissions: roleToEdit.permissions.map(permission => permission.id.toString())
      });
    }
  }, [roleToEdit, isLoadingPermission, reset]);

  const isLoading = isSubmittingForm || isLoadingPermission;

  return (
    <form onSubmit={handleSubmit(submitRoleData)}>
      <div className="mb-3">
        <label htmlFor="name" className="form-label fw-bold">Nombre</label>
        <input
          type="text"
          id="name"
          className={`form-control ${errors.name ? 'is-invalid' : ''}`}
          {...register('name')}
          disabled={isLoading}
        />
        {errors.name && <div className="invalid-feedback">{errors.name.message}</div>}
      </div>
      <div className="mb-3">
        <label htmlFor="description" className="form-label fw-bold">Descripción</label>
        <textarea
          id="description"
          rows="3"
          className={`form-control ${errors.description ? 'is-invalid' : ''}`}
          {...register('description')}
          disabled={isLoading}
        ></textarea>
        {errors.description && <div className="invalid-feedback">{errors.description.message}</div>}
      </div>
      <fieldset className="mb-4 mt-4 border-0 p-0">
        <legend className="form-label fw-bold float-none w-auto mb-2 m-0 fs-6 lh-base">Permisos</legend>
        <div className={`border p-3 rounded-2 bg-light shadow-sm ${errors.categories ? 'border-danger' : ''}`}>
          {permissionError &&
            (<div className="alert alert-danger p-2">
              <small><strong>¡Error! </strong>{permissionError}</small>
            </div>)}
          {isLoadingPermission ? (
            <div className="text-center my-3"><div className="spinner-border spinner-border-sm" role="status"></div><p className="admin-panel-text-muted mt-2">Cargando permisos...</p></div>
          ) : (
            <div className="row g-2">
              {allPermissions.map((permission) => (
                <div className="col-12 col-sm-6 col-xl-4" key={permission.id}>
                  <div className="form-check">
                    <input
                      type="checkbox"
                      id={`permission-${permission.id}`}
                      value={permission.id.toString()}
                      className="form-check-input"
                      {...register('permissions')}
                      disabled={isLoading}
                    />
                    <label className="form-check-label user-select-none" htmlFor={`permission-${permission.id}`}>
                      {permission.name}
                    </label>
                  </div>
                </div>
              ))}
            </div>
          )}
          {errors.permissions && <small className="text-danger mt-2 d-block">{errors.permissions.message}</small>}
        </div>
      </fieldset>

      {apiError && (
        <div className="alert alert-danger shadow-sm" role="alert">
          <strong><i className="bi bi-exclamation-triangle me-2"></i>¡Error!</strong> {apiError}
        </div>
      )}
      <div className="d-flex justify-content-between mt-5 pt-3 border-top">
        <button
          type="button"
          className="btn form-btn rounded-3 px-4"
          onClick={() => navigate("/administration/role-list")}
          disabled={isLoading}
        ><i className="bi bi-arrow-left me-2"></i>
          Regresar
        </button>
        <button
          type="submit"
          className="btn btn-success rounded-3 px-4 shadow-sm"
          disabled={isLoading}
        ><i className="bi bi-floppy me-1"></i>
          {isLoading ? 'Guardando...' : roleToEdit ? 'Actualizar Rol' : 'Registrar Rol'}
        </button>
      </div>
    </form>
  );
}

