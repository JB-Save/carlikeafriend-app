import { ImageUploadInput } from './ImageUploadInput';
import { ImagePreviewList } from './ImagePreviewList';
import { useCategoryForm } from '../hooks/useCategoryForm';
import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { useNavigate } from 'react-router-dom';
import { yupResolver } from '@hookform/resolvers/yup';
import { categorySchema } from '../utils/validationSchema';
import { API_CONFIG } from '../config/apiConfig';

// Componente del formulario para crear/editar categorías
export const CategoryForm = ({ categoryToEdit, onCategorySaved }) => {
  const MAX_IMAGES = API_CONFIG.MAX_IMAGES_FOR_CATEGORIES;
  const URL = API_CONFIG.CATEGORY_IMAGES_BASE;
  const navigate = useNavigate(); // <-- Usa useNavigate para la navegación

  const {
    newImages,
    existingImages,
    error: apiError,
    isSubmittingForm: isLoading,
    imageUploadError,
    availableSlots,
    canAddMoreImages,
    fileInputRef,
    handleNewImageChange,
    handleRemoveExistingImage,
    handleRemoveNewImageFile,
    submitCategoryData
  } = useCategoryForm(categoryToEdit, MAX_IMAGES, onCategorySaved);

  const { register, handleSubmit, reset, formState: { errors } } = useForm({
    resolver: yupResolver(categorySchema)
  });

  useEffect(() => {
    if (categoryToEdit) {
      reset({
        name: categoryToEdit.name,
        description: categoryToEdit.description,
        baseDailyRate: categoryToEdit.baseDailyRate,
        priority: categoryToEdit.priority,
        baseDepositAmount: categoryToEdit.baseDepositAmount
      });
    }
  }, [categoryToEdit, reset]);

  return (
    <form onSubmit={handleSubmit(submitCategoryData)} >
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
      <div className="row">
        <div className="col-md-4 mb-3">
          <label htmlFor="baseDailyRate" className="form-label fw-bold">Tarifa diaria base</label>
          <input
            type="number"
            id="baseDailyRate"
            step="0.01"
            className={`form-control ${errors.baseDailyRate ? 'is-invalid' : ''}`}
            {...register('baseDailyRate')}
            disabled={isLoading}
          />
          {errors.baseDailyRate && <div className="invalid-feedback">{errors.baseDailyRate.message}</div>}
        </div>
        <div className="col-md-4 mb-3">
          <label htmlFor="priority" className="form-label fw-bold">Prioridad</label>
          <input
            type="number"
            id="priority"
            step="1"
            className={`form-control ${errors.priority ? 'is-invalid' : ''}`}
            {...register('priority')}
            disabled={isLoading}
          />
          {errors.priority && <div className="invalid-feedback">{errors.priority.message}</div>}
        </div>

        <div className="col-md-4 mb-3">
          <label htmlFor="baseDepositAmount" className="form-label fw-bold">Depósito base</label>
          <input
            type="number"
            id="baseDepositAmount"
            step="0.01"
            className={`form-control ${errors.baseDepositAmount ? 'is-invalid' : ''}`}
            {...register('baseDepositAmount')}
            disabled={isLoading}
          />
          {errors.baseDepositAmount && <div className="invalid-feedback">{errors.baseDepositAmount.message}</div>}
        </div>
      </div>

      <ImagePreviewList
        URL={URL}
        images={existingImages}
        onRemoveImage={handleRemoveExistingImage}
      />

      <ImageUploadInput
        maxImages={MAX_IMAGES}
        availableSlots={availableSlots}
        newImages={newImages}
        onFileChange={handleNewImageChange}
        fileInputRef={fileInputRef}
        uploadError={imageUploadError}
        canAddMoreImages={canAddMoreImages}
        onDeleteUploadedFile={handleRemoveNewImageFile}
      />

      {apiError && (
        <div className="alert alert-danger shadow-sm" role="alert">
          <strong><i className="bi bi-exclamation-triangle me-2"></i>¡Error!</strong> {apiError}
        </div>
      )}
      <div className="d-flex justify-content-between mt-5 pt-3 border-top">
        <button
          type="button"
          className="btn form-btn rounded-3 px-4"
          onClick={() => navigate("/administration/category-list")}
          disabled={isLoading}
        ><i className="bi bi-arrow-left me-2"></i>
          Regresar
        </button>
        <button
          type="submit"
          className="btn btn-success rounded-3 px-4 shadow-sm"
          disabled={isLoading}
        ><i className="bi bi-floppy me-1"></i>
          {isLoading ? 'Guardando...' : categoryToEdit ? 'Actualizar Categoría' : 'Registrar Categoría'}
        </button>
      </div>
    </form>
  );
}

