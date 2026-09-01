import { ImageUploadInput } from './ImageUploadInput';
import { ImagePreviewList } from './ImagePreviewList';
import { useFeatureForm } from '../hooks/useFeatureForm';
import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { useNavigate } from 'react-router-dom';
import { yupResolver } from '@hookform/resolvers/yup';
import { singleFieldSchema } from '../utils/validationSchema';
import { API_CONFIG } from '../config/apiConfig';

// Componente del formulario para crear/editar características
export const FeatureForm = ({ featureToEdit, onFeatureSaved }) => {
  const MAX_IMAGES = API_CONFIG.MAX_IMAGES_FOR_FEATURES;
  const URL = API_CONFIG.FEATURE_IMAGES_BASE;
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
    submitFeatureData
  } = useFeatureForm(featureToEdit, MAX_IMAGES, onFeatureSaved);

  const { register, handleSubmit, reset, formState: { errors } } = useForm({
    resolver: yupResolver(singleFieldSchema)
  });

  useEffect(() => {
    if (featureToEdit) {
      reset({
        name: featureToEdit.name
      });
    }
  }, [featureToEdit, reset]);

  return (
    <form onSubmit={handleSubmit(submitFeatureData)} >
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
          onClick={() => navigate("/administration/feature-list")}
          disabled={isLoading}
        ><i className="bi bi-arrow-left me-2"></i>
          Regresar
        </button>
        <button
          type="submit"
          className="btn btn-success rounded-3 px-4 shadow-sm"
          disabled={isLoading}
        ><i className="bi bi-floppy me-1"></i>
          {isLoading ? 'Guardando...' : featureToEdit ? 'Actualizar Característica' : 'Registrar Característica'}
        </button>
      </div>
    </form>
  );
}

