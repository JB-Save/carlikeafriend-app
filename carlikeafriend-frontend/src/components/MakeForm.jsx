import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { useMakeForm } from '../hooks/useMakeForm';
import { useNavigate } from 'react-router-dom';
import { yupResolver } from '@hookform/resolvers/yup';
import { singleFieldSchema } from '../utils/validationSchema';

// Componente del formulario para crear/editar
export const MakeForm = ({ makeToEdit, onMakeSaved }) => {

  const navigate = useNavigate();

  const {
    error: apiError,
    isSubmittingForm: isLoading,
    submitMakeData
  } = useMakeForm(makeToEdit, onMakeSaved);

  const { register, handleSubmit, reset, formState: { errors } } = useForm({
    resolver: yupResolver(singleFieldSchema)
  });

  useEffect(() => {
    if (makeToEdit) {
      reset({
        name: makeToEdit.name
      });
    }
  }, [makeToEdit, reset]);

  return (
    <form onSubmit={handleSubmit(submitMakeData)} >
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

      {apiError && (
        <div className="alert alert-danger shadow-sm" role="alert">
          <strong><i className="bi bi-exclamation-triangle me-2"></i>¡Error!</strong> {apiError}
        </div>
      )}
      <div className="d-flex justify-content-between mt-5 pt-3 border-top">
        <button
          type="button"
          className="btn form-btn rounded-3 px-4"
          onClick={() => navigate("/administration/make-list")}
          disabled={isLoading}
        ><i className="bi bi-arrow-left me-2"></i>
          Regresar
        </button>
        <button
          type="submit"
          className="btn btn-success rounded-3 px-4 shadow-sm"
          disabled={isLoading}
        ><i className="bi bi-floppy me-1"></i>
          {isLoading ? 'Guardando...' : makeToEdit ? 'Actualizar Marca' : 'Registrar Marca'}
        </button>
      </div>
    </form>
  );
}

