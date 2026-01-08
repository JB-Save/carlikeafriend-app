import { useState, useEffect, useRef, useCallback, useContext } from 'react';
import { UserContext } from '../context/UserContext';
import { extractErrorMessage } from '../utils/extractErrorMessage';
import { useNavigate } from 'react-router-dom';
import { handleUnauthorizedError } from '../utils/handleUnauthorizedError';
import { useMessageModal } from '../context/MessageModalContext';
import { API_CONFIG } from '../config/apiConfig';

export const useProductForm = (productToEdit, MAX_IMAGES, onProductSaved) => {
  const [productData, setProductData] = useState({
    name: '',
    description: '',
    categories: [],
    features: [],
    price: ''
  });

  const { token, logout } = useContext(UserContext);
  const { setModalMessage } = useMessageModal();
  const [newImages, setNewImages] = useState([]);
  const [existingImages, setExistingImages] = useState([]);
  const [imagesToDeleteIds, setImagesToDeleteIds] = useState([]);
  const [allCategories, setAllCategories] = useState([]);
  const [allFeatures, setAllFeatures] = useState([]);
  const [error, setError] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const [imageUploadError, setImageUploadError] = useState(null);
  const fileInputRef = useRef(null);

  //Estados de carga categorías
  const [isLoadingCategory, setIsLoadingCategory] = useState(true);
  const [categoryError, setCategoryError] = useState(null);
  const CATEGORIES_URL = API_CONFIG.CATEGORIES;
  //Estados de carga características
  const [isLoadingFeature, setIsLoadingFeature] = useState(true);
  const [featureError, setFeatureError] = useState(null);
  const FEATURES_URL = API_CONFIG.FEATURES;

  const navigate = useNavigate(); // <-- Usa useNavigate para la navegación
  const ALLOWED_MIMES = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'];
  const MAX_FILE_SIZE = API_CONFIG.MAX_FILE_SIZE; // 5 MB en bytes.

  useEffect(() => {

    const loadFormData = async () => {
      setIsLoadingCategory(true);
      setIsLoadingFeature(true);
      setCategoryError(null);
      setFeatureError(null);
      try {
        // Lanzamos ambas peticiones en paralelo
        const [resCat, resFeat] = await Promise.all([
          fetch(CATEGORIES_URL, {
            method: 'GET',
            headers: { 'Authorization': `Bearer ${token}` }
          }),
          fetch(FEATURES_URL, {
            method: 'GET',
            headers: { 'Authorization': `Bearer ${token}` }
          })
        ]);

        // Solo necesitamos verificar la autorización UNA VEZ (si una da 401, la otra probablemente también)
        if (handleUnauthorizedError(resCat, navigate, logout, setModalMessage) ||
          handleUnauthorizedError(resFeat, navigate, logout, setModalMessage)) {
          return;
        }

        // Procesar Categorías
        if (resCat.ok) {
          const catData = await resCat.json();
          setAllCategories(catData);
        } else {
          const catMsg = await extractErrorMessage(resCat);
          setCategoryError(catMsg);
        }

        // Procesar Características
        if (resFeat.ok) {
          const featData = await resFeat.json();
          setAllFeatures(featData);
        } else {
          const featMsg = await extractErrorMessage(resFeat);
          setFeatureError(featMsg);
        }

      } catch (error) {
        console.error("Error cargando datos del formulario:", error);
        // Un error de red genérico para ambos si falla la conexión
        setCategoryError("Error de conexión.");
        setFeatureError("Error de conexión.");
      } finally {
        setIsLoadingCategory(false);
        setIsLoadingFeature(false);
      }

    };

    if (token) loadFormData();

  }, [token, navigate, logout]);

  useEffect(() => {
    if (productToEdit) {
      const categoriesIds = productToEdit.categories.map(category => category.id);
      const featuresIds = productToEdit.features.map(feature => feature.id);
      setProductData({
        name: productToEdit.name,
        description: productToEdit.description,
        categories: categoriesIds,
        features: featuresIds,
        price: productToEdit.price
      });
      setExistingImages(productToEdit.productImages);
    } else {
      resetForm();
    }

  }, [productToEdit]);

  // Calcula la cantidad de ranuras disponibles para nuevas imágenes
  const availableSlots = MAX_IMAGES - existingImages.length - newImages.length;
  const canAddMoreImages = availableSlots > 0;

  // Maneja cambios en los campos de texto
  const handleChange = (e) => {
    const { name, value } = e.target;
    setProductData(prevData => ({ ...prevData, [name]: value }));
  };

  //Maneja cambios en los checklists de categoría
  const handleCategoryCheckListChange = (e) => {
    const { value, checked, name } = e.target;
    const selectedCategoryId = parseInt(value);

    setProductData(prevData => {
      const currentList = prevData[name] || [];

      if (checked) {
        // Si el checkbox está marcado, agrega el valor a la lista
        return {
          ...prevData,
          [name]: [...currentList, selectedCategoryId]
        };
      } else {
        // Si no está marcada, filtra el objeto de categoría por su id.
        return {
          ...prevData,
          [name]: currentList.filter(item => item !== selectedCategoryId)
        };
      }
    });
  };

  //Maneja cambios en los checklists de características
  const handleFeatureCheckListChange = (e) => {
    const { value, checked, name } = e.target;
    const selectedFeatureId = parseInt(value);

    setProductData(prevData => {
      const currentList = prevData[name] || [];

      if (checked) {
        // Si el checkbox está marcado, agrega el valor a la lista
        return {
          ...prevData,
          [name]: [...currentList, selectedFeatureId]
        };
      } else {
        // Si no está marcada, filtra el objeto de característica por su id.
        return {
          ...prevData,
          [name]: currentList.filter(item => item !== selectedFeatureId)
        };
      }
    });
  };


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
  const resetForm = useCallback(() => {
    setProductData({
      name: '',
      description: '',
      categories: [],
      features: [],
      price: ''
    });
    setNewImages([]);
    setExistingImages([]);
    setImagesToDeleteIds([]);
    //setError(null);
    setImageUploadError(null);
    // Limpiar el input de archivos
    if (fileInputRef.current) {
      fileInputRef.current.value = '';
    }
  }, []);

  // Envía el formulario al backend
  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsLoading(true);
    setError(null);
    setImageUploadError(null);

    // Verifica si el array de categorías está vacío
    if (productData.categories.length === 0) {
      setError("Debes seleccionar al menos una categoría.");
      setIsLoading(false);
      return;
    }

    // Verifica si el array de características está vacío
    if (productData.features.length === 0) {
      setError("Debes seleccionar al menos una característica.");
      setIsLoading(false);
      return;
    }

    // Verifica si el array de Imágenes está vacío
    if (existingImages.length === 0 && newImages.length === 0) {
      setError("Debes seleccionar al menos una imagen.");
      setIsLoading(false);
      return;
    }

    // Crea el objeto FormData para enviar datos y archivos
    const formData = new FormData();

    // Añade el objeto de producto como un JSON, ya que Spring lo espera con @RequestPart
    formData.append('product', new Blob([JSON.stringify(productData)], { type: 'application/json' }));

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
      let response;
      const URL = productToEdit
        ? `${API_CONFIG.PRODUCTS}/${productToEdit.id}`
        : API_CONFIG.PRODUCTS;

      const method = productToEdit ? 'PUT' : 'POST';

      response = await fetch(URL, {
        method: method,
        headers: {
          'Authorization': `Bearer ${token}` // ¡AÑADIR TOKEN!
        },
        body: formData
        // No es necesario especificar el 'Content-Type' aquí, el navegador lo hará.
      });

      if (handleUnauthorizedError(response, navigate, logout, setModalMessage)) return; // Detener si fue un 401

      if (response.ok) {
        if (onProductSaved) onProductSaved();
        resetForm();
      } else {
        // Manejo de otros errores (400, 500, etc.)
        const msg = await extractErrorMessage(response);
        throw new Error(msg);
      }

    } catch (error) {
      console.error("Error guardando producto:", error);
      setError(error.message || "Ocurrió un error inesperado");
    } finally {
      setIsLoading(false);
    }
  };

  return {
    productData,
    newImages,
    existingImages,
    allCategories,
    allFeatures,
    isLoadingCategory,
    isLoadingFeature,
    categoryError,
    featureError,
    error,
    isLoading,
    imageUploadError,
    availableSlots,
    canAddMoreImages,
    fileInputRef,
    handleChange,
    handleCategoryCheckListChange,
    handleFeatureCheckListChange,
    handleNewImageChange,
    handleRemoveExistingImage,
    handleRemoveNewImageFile,
    handleSubmit,
  };
}