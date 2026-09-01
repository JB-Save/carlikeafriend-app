import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { usePermissionForm } from '../hooks/usePermissionForm';
import { useNavigate } from 'react-router-dom';
import { yupResolver } from '@hookform/resolvers/yup';
import { permissionSchema } from '../utils/validationSchema';

// Componente del formulario para crear/editar permisos
export const PermissionForm = ({ permissionToEdit, onPermissionSaved }) => {

  const navigate = useNavigate();

  const {
    error: apiError,
    isSubmittingForm: isLoading,
    submitPermissionData
  } = usePermissionForm(permissionToEdit, onPermissionSaved);

  const { register, handleSubmit, reset, formState: { errors } } = useForm({
    resolver: yupResolver(permissionSchema)
  });

  useEffect(() => {
    if (permissionToEdit) {
      reset({
        name: permissionToEdit.name,
        description: permissionToEdit.description
      });
    }
  }, [permissionToEdit, reset]);

  return (
    <form onSubmit={handleSubmit(submitPermissionData)} >
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

      {apiError && (
        <div className="alert alert-danger shadow-sm" role="alert">
          <strong><i className="bi bi-exclamation-triangle me-2"></i>¡Error!</strong> {apiError}
        </div>
      )}
      <div className="d-flex justify-content-between mt-5 pt-3 border-top">
        <button
          type="button"
          className="btn form-btn rounded-3 px-4"
          onClick={() => navigate("/administration/permission-list")}
          disabled={isLoading}
        ><i className="bi bi-arrow-left me-2"></i>
          Regresar
        </button>
        <button
          type="submit"
          className="btn btn-success rounded-3 px-4 shadow-sm"
          disabled={isLoading}
        ><i className="bi bi-floppy me-1"></i>
          {isLoading ? 'Guardando...' : permissionToEdit ? 'Actualizar Permiso' : 'Registrar Permiso'}
        </button>
      </div>
    </form>
  );
}

