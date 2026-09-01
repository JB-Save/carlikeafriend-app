import { useState, useEffect, useRef, useCallback, useContext } from 'react';
import { UserContext } from '../context/UserContext';
import { extractErrorMessage } from '../utils/extractErrorMessage';
import { useNavigate } from 'react-router-dom';
import { handleUnauthorizedError } from '../utils/handleUnauthorizedError';
import { useMessageModal } from '../context/MessageModalContext';
import { API_CONFIG } from '../config/apiConfig';

export const useCategoryForm = (categoryToEdit, MAX_IMAGES, onCategorySaved) => {
  const { token, logout } = useContext(UserContext); // Obtener token
  const { setModalMessage } = useMessageModal();
  const [newImages, setNewImages] = useState([]);
  const [existingImages, setExistingImages] = useState([]);
  const [error, setError] = useState(null);
  const [imageUploadError, setImageUploadError] = useState(null);
  const [isSubmittingForm, setIsSubmittingForm] = useState(false);
  const fileInputRef = useRef(null);

  const navigate = useNavigate(); // <-- Usa useNavigate para la navegación
  const ALLOWED_MIMES = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'];
  const MAX_FILE_SIZE = API_CONFIG.MAX_FILE_SIZE; // 5 MB en bytes. 

  useEffect(() => {
    if (categoryToEdit) {
      const singleImageObjectAsList = new Array(categoryToEdit.categoryImage);
      setExistingImages(singleImageObjectAsList);
    } else {
      resetImagesState();
    }
  }, [categoryToEdit]);

  // Calcula la cantidad disponible para la nueva imagen
  const availableSlots = MAX_IMAGES - existingImages.length - newImages.length;
  const canAddMoreImages = availableSlots > 0;

  // Maneja la selección de nuevas imágenes
  const handleNewImageChange = (e) => {
    const files = Array.from(e.target.files);
    setImageUploadError(null);

    // Filtrar por tipo permitido
    const validTypeFiles = files.filter(file => ALLOWED_MIMES.includes(file.type));
    const invalidTypeFilesCount = files.length - validTypeFiles.length;

    // Filtrar por tamaño máximo (5MB)
    const validSizeFiles = validTypeFiles.filter(file => file.size <= MAX_FILE_SIZE);
    const oversizedFilesCount = validTypeFiles.length - validSizeFiles.length;

    let hasErrors = false;
    let errorMessage = '';

    if (invalidTypeFilesCount > 0) {
      errorMessage += invalidTypeFilesCount === 1
        ? `Se ha ignorado ${invalidTypeFilesCount} archivo con tipos no permitidos. `
        : `Se han ignorado ${invalidTypeFilesCount} archivos con tipos no permitidos. `;
      hasErrors = true;
    }

    if (oversizedFilesCount > 0) {
      const maxSizeMB = MAX_FILE_SIZE / (1024 * 1024);
      errorMessage += oversizedFilesCount === 1
        ? `Se ha ignorado ${oversizedFilesCount} archivo por exceder el tamaño máximo de ${maxSizeMB}MB. `
        : `Se han ignorado ${oversizedFilesCount} archivos por exceder el tamaño máximo de ${maxSizeMB}MB. `;
      hasErrors = true;
    }

    if (hasErrors) {
      setImageUploadError(errorMessage.trim());
    }

    // Validar slots disponibles
    const available = MAX_IMAGES - existingImages.length;
    const totalNewImagesAfterSelection = newImages.length + validSizeFiles.length;

    if (totalNewImagesAfterSelection > available) {
      const slotsRemaining = available - newImages.length;
      const filesToAdd = validSizeFiles.slice(0, slotsRemaining);
      let slotError = '';
      let ignoredFiles = files.length - slotsRemaining;
      let additionalErrorText = ignoredFiles === 1
        ? `${files.length - slotsRemaining} archivo fue ignorado. `
        : `${files.length - slotsRemaining} archivos fueron ignorados. `

      if (slotsRemaining > 0) {
        slotError = slotsRemaining === 1
          ? `Se seleccionó ${slotsRemaining} imagen adicional. ${additionalErrorText}`
          : `Se seleccionó ${slotsRemaining} imágenes adicionales. ${additionalErrorText}`
      }

      // Si ya hay un error de tipo/tamaño, lo combinamos.
      if (hasErrors) {
        setImageUploadError(errorMessage.trim() + ' ' + slotError);
      } else {
        setImageUploadError(slotError);
      }

      setNewImages(prevFiles => [...prevFiles, ...filesToAdd]);

      //Limpiar el input para permitir seleccionar los mismos archivos de nuevo si el usuario intenta subir más de los permitidos.
      if (fileInputRef.current) {
        fileInputRef.current.value = '';
      }
    } else {
      setNewImages(prevFiles => [...prevFiles, ...validSizeFiles]);
    }
  };

  // Maneja la eliminación de imágenes existentes
  const handleRemoveExistingImage = (imageId) => {
    const updatedImages = existingImages.filter(img => img.id !== imageId);
    setExistingImages(updatedImages);
    setImageUploadError(null);
  };

  // Maneja la eliminación de archivos de imágenes nuevas
  const handleRemoveNewImageFile = (file) => {
    const updatedNewImageFiles = newImages.filter(f => f.name !== file.name);
    setNewImages(updatedNewImageFiles);
    setImageUploadError(null);
  };

  const resetImagesState = useCallback(() => {
    setNewImages([]);
    setExistingImages([]);
    setImageUploadError(null);
    // Limpiar el input de archivos
    if (fileInputRef.current) {
      fileInputRef.current.value = '';
    }
  }, []);

  const submitCategoryData = async (data) => {
    setIsSubmittingForm(true);
    setError(null);
    setImageUploadError(null);

    // Verifica si el array de categorías está vacío
    if (existingImages.length === 0 && newImages.length === 0) {
      setError("Debes seleccionar al menos una imagen.");
      setIsSubmittingForm(false);
      return;
    }

    const payload = {
      ...data,
      baseDailyRate: Number(data.baseDailyRate),
      priority: Number(data.priority),
      baseDepositAmount: Number(data.baseDepositAmount)
    };

    // Crea el objeto FormData para enviar datos y archivos
    const formData = new FormData();

    // Añade el objeto de categoría como un JSON, ya que Spring lo espera con @RequestPart
    formData.append('category', new Blob([JSON.stringify(payload)], { type: 'application/json' }));

    // Lógica para manejar las imágenes dependiendo de si se crea o se actualiza
    if (categoryToEdit) {
      // Para la actualización (PUT), el servidor espera 'newImageFile'.
      if (newImages.length > 0) {
        newImages.forEach(file => {
          formData.append('newImageFile', file);
        });
      }
    } else {
      // Para la creación (POST), el servidor espera 'image'.
      newImages.forEach(file => {
        formData.append('imageFile', file);
      });
    }

    try {

      const URL = categoryToEdit
        ? `${API_CONFIG.CATEGORIES}/${categoryToEdit.id}`
        : API_CONFIG.CATEGORIES;

      const method = categoryToEdit ? 'PUT' : 'POST';

      const response = await fetch(URL, {
        method: method,
        headers: {
          'Authorization': `Bearer ${token}`
        },
        body: formData
        // No es necesario especificar el 'Content-Type' aquí, el navegador lo hará.
      });

      if (handleUnauthorizedError(response, navigate, logout, setModalMessage)) return; // Detener si fue un 401


      if (response.ok) {
        if (onCategorySaved) onCategorySaved();
        resetImagesState();
      } else {
        // Manejo de otros errores (400, 500, etc.)
        const msg = await extractErrorMessage(response);
        throw new Error(msg);
      }
    } catch (error) {
      console.error("Error guardando categoría: ", error);
      const message = error.message.includes("Failed to fetch") ? "No se pudo establecer conexión con el servidor." : error.message;
      setError(message || "Ocurrió un error inesperado");
    } finally {
      setIsSubmittingForm(false);
    }
  };

  return {
    newImages,
    existingImages,
    error,
    isSubmittingForm,
    imageUploadError,
    availableSlots,
    canAddMoreImages,
    fileInputRef,
    handleNewImageChange,
    handleRemoveExistingImage,
    handleRemoveNewImageFile,
    submitCategoryData
  };
}