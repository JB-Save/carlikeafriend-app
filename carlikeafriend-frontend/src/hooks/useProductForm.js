import { useState, useEffect, useRef, useCallback, useContext } from 'react';
import { UserContext } from '../context/UserContext';
import { extractErrorMessage } from '../utils/extractErrorMessage';
import { useNavigate } from 'react-router-dom';
import { handleUnauthorizedError } from '../utils/handleUnauthorizedError';
import { useMessageModal } from '../context/MessageModalContext';
import { API_CONFIG } from '../config/apiConfig';

export const useProductForm = (productToEdit, MAX_IMAGES, onProductSaved) => {

  const { token, logout } = useContext(UserContext);
  const { setModalMessage } = useMessageModal();

  const [newImages, setNewImages] = useState([]);
  const [existingImages, setExistingImages] = useState([]);
  const [imagesToDeleteIds, setImagesToDeleteIds] = useState([]);
  const [allCategories, setAllCategories] = useState([]);
  const [allFeatures, setAllFeatures] = useState([]);
  const [allMakes, setAllMakes] = useState([]);
  const [allPolicies, setAllPolicies] = useState([]);
  const [error, setError] = useState(null);
  const [isSubmittingForm, setIsSubmittingForm] = useState(false);
  const [imageUploadError, setImageUploadError] = useState(null);
  const fileInputRef = useRef(null);

  //Estados de carga categorías
  const [isLoadingCategory, setIsLoadingCategory] = useState(true);
  const [categoryError, setCategoryError] = useState(null);

  //Estados de carga características
  const [isLoadingFeature, setIsLoadingFeature] = useState(true);
  const [featureError, setFeatureError] = useState(null);

  //Estados de carga marcas
  const [isLoadingMake, setIsLoadingMake] = useState(true);
  const [makeError, setMakeError] = useState(null);
  
  //Estados de carga políticas
  const [isLoadingPolicy, setIsLoadingPolicy] = useState(true);
  const [policyError, setPolicyError] = useState(null);

  const navigate = useNavigate();
  const ALLOWED_MIMES = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'];
  const MAX_FILE_SIZE = API_CONFIG.MAX_FILE_SIZE; // 5 MB en bytes.

  useEffect(() => {

    const loadFormData = async () => {
      setIsLoadingCategory(true);
      setIsLoadingFeature(true);
      setIsLoadingMake(true);
      setIsLoadingPolicy(true);
      setCategoryError(null);
      setFeatureError(null);
      setMakeError(null);
      setPolicyError(null);
      try {
        // Lanzamos ambas peticiones en paralelo
        const [catRes, featRes, makeRes, polRes] = await Promise.all([
          fetch(API_CONFIG.CATEGORIES, {
            method: 'GET',
            headers: { 'Authorization': `Bearer ${token}` }
          }),
          fetch(API_CONFIG.FEATURES, {
            method: 'GET',
            headers: { 'Authorization': `Bearer ${token}` }
          }),
          fetch(API_CONFIG.MAKES, {
            method: 'GET',
            headers: { 'Authorization': `Bearer ${token}` }
          }),
          fetch(API_CONFIG.POLICIES, {
            method: 'GET',
            headers: { 'Authorization': `Bearer ${token}` }
          })
        ]);

        // Solo necesitamos verificar la autorización UNA VEZ (si una da 401, la otra probablemente también)
        if (handleUnauthorizedError(catRes, navigate, logout, setModalMessage) ||
          handleUnauthorizedError(featRes, navigate, logout, setModalMessage) ||
          handleUnauthorizedError(makeRes, navigate, logout, setModalMessage) ||
          handleUnauthorizedError(polRes, navigate, logout, setModalMessage)) {
          return;
        }

        // Procesar Categorías
        if (catRes.ok) {
          const catData = await catRes.json();
          setAllCategories(catData);
        } else {
          const catMsg = await extractErrorMessage(catRes);
          setCategoryError(catMsg);
        }

        // Procesar Características
        if (featRes.ok) {
          const featData = await featRes.json();
          setAllFeatures(featData);
        } else {
          const featMsg = await extractErrorMessage(featRes);
          setFeatureError(featMsg);
        }

        // Procesar Marcas
        if (makeRes.ok) {
          const makeData = await makeRes.json();
          setAllMakes(makeData);
        } else {
          const makeMsg = await extractErrorMessage(makeRes);
          setMakeError(makeMsg);
        }

        // Procesar Políticas
        if (polRes.ok) {
          const polData = await polRes.json();
          setAllPolicies(polData);
        } else {
          const polMsg = await extractErrorMessage(polRes);
          setPolicyError(polMsg);
        }

      } catch (error) {
        console.error("Error de red en carga inicial:", error);
        // Errores de conexión (cuando no hay respuesta del servidor)
        const networkMsg = "No se pudo establecer conexión con el servidor.";
        setCategoryError(networkMsg);
        setFeatureError(networkMsg);
        setMakeError(networkMsg);
        setPolicyError(networkMsg);
      } finally {
        setIsLoadingCategory(false);
        setIsLoadingFeature(false);
        setIsLoadingMake(false);
        setIsLoadingPolicy(false);
      }

    };

    if (token) loadFormData();

  }, [token, navigate, logout]);

  useEffect(() => {
    if (productToEdit) {
      setExistingImages(productToEdit.productImages);
    } else {
      resetImagesState();
    }
  }, [productToEdit]);

  // Calcula la cantidad de ranuras disponibles para nuevas imágenes
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
        ? `${ignoredFiles} archivo fue ignorado. `
        : `${ignoredFiles} archivos fueron ignorados. `

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
    setImagesToDeleteIds(prevIds => [...prevIds, imageId]);
    setImageUploadError(null);
  };

  // Maneja la eliminación de archivos de imágenes nuevas
  const handleRemoveNewImageFile = (file) => {
    const updatedNewImageFiles = newImages.filter(f => f.name !== file.name);
    setNewImages(updatedNewImageFiles);
    setImageUploadError(null);
  };

  // Función para resetear el formulario.
  const resetImagesState = useCallback(() => {
    setNewImages([]);
    setExistingImages([]);
    setImagesToDeleteIds([]);
    setImageUploadError(null);
    // Limpiar el input de archivos
    if (fileInputRef.current) {
      fileInputRef.current.value = '';
    }
  }, []);

  const submitProductData = async (data) => {
    setIsSubmittingForm(true);
    setError(null);
    setImageUploadError(null);

    // Verifica si el array de Imágenes está vacío
    if (existingImages.length === 0 && newImages.length === 0) {
      setError("Debes seleccionar al menos una imagen.");
      setIsSubmittingForm(false);
      return;
    }

    const payload = {
      ...data,
      makeId: Number(data.makeId),
      categories: data.categories.map(id => Number(id)),
      features: data.features.map(id => Number(id)),
      policies: data.policies.map(id => Number(id))
    };

    // Crea el objeto FormData para enviar datos y archivos
    const formData = new FormData();

    // Añade el objeto de producto como un JSON, ya que Spring lo espera con @RequestPart
    formData.append('product', new Blob([JSON.stringify(payload)], { type: 'application/json' }));

    // Lógica para manejar las imágenes dependiendo de si se crea o se actualiza
    if (productToEdit) {
      // Para la actualización (PUT), el servidor espera 'newImageFiles' y 'imagesToDelete'.
      if (newImages.length > 0) {
        newImages.forEach(file => {
          formData.append('newImageFiles', file);
        });
      }
      if (imagesToDeleteIds.length > 0) {
        // El nombre de la parte debe ser 'imagesToDelete' y se envía como un array serializado.
        formData.append('imagesToDelete', new Blob([JSON.stringify(imagesToDeleteIds)], { type: 'application/json' }));
      }
    } else {
      // Para la creación (POST), el servidor espera 'images'.
      newImages.forEach(file => {
        formData.append('images', file);
      });
    }

    try {

      const URL = productToEdit
        ? `${API_CONFIG.PRODUCTS}/${productToEdit.id}`
        : API_CONFIG.PRODUCTS;

      const method = productToEdit ? 'PUT' : 'POST';

      const response = await fetch(URL, {
        method: method,
        headers: {
          'Authorization': `Bearer ${token}`
        },
        body: formData
      });

      if (handleUnauthorizedError(response, navigate, logout, setModalMessage)) return; // Detener si fue un 401

      if (response.ok) {
        if (onProductSaved) onProductSaved();
        resetImagesState();
      } else {
        // Manejo de otros errores (400, 500, etc.)
        const msg = await extractErrorMessage(response);
        throw new Error(msg);
      }

    } catch (error) {
      console.error("Error guardando producto:", error);
      const message = error.message.includes("Failed to fetch") ? "No se pudo establecer conexión con el servidor." : error.message;
      setError(message || "Ocurrió un error inesperado");
    } finally {
      setIsSubmittingForm(false);
    }
  };

  return {
    allCategories,
    allFeatures,
    allMakes,
    allPolicies,
    isLoadingCategory,
    isLoadingFeature,
    isLoadingMake,
    isLoadingPolicy,
    categoryError,
    featureError,
    makeError,
    policyError,
    error,
    isSubmittingForm,
    newImages,
    existingImages,
    imageUploadError,
    availableSlots,
    canAddMoreImages,
    fileInputRef,
    handleNewImageChange,
    handleRemoveExistingImage,
    handleRemoveNewImageFile,
    submitProductData
  };
}