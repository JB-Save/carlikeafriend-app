import { useState, useEffect, useRef } from 'react';

export const useProductForm = (productToEdit, MAX_IMAGES, onProductSaved) => {
  const [productData, setProductData] = useState({
    name: '',
    description: '',
    price: ''
  });
  const [newImages, setNewImages] = useState([]);
  const [existingImages, setExistingImages] = useState([]);
  const [imagesToDeleteIds, setImagesToDeleteIds] = useState([]);
  const [error, setError] = useState(null);
  const [imageUploadError, setImageUploadError] = useState(null);
  const [loading, setLoading] = useState(false);
  const fileInputRef = useRef(null);

  const ALLOWED_MIMES = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'];

  useEffect(() => {
    if (productToEdit) {
      setProductData({
        name: productToEdit.name,
        description: productToEdit.description,
        price: productToEdit.price
      });
      setExistingImages(productToEdit.images);
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

  // Maneja la selección de nuevas imágenes
  const handleNewImageChange = (e) => {
    const files = Array.from(e.target.files);
    setImageUploadError(null);

    const validFiles = files.filter(file => ALLOWED_MIMES.includes(file.type));
    const invalidFiles = files.length - validFiles.length;

    if (invalidFiles > 0) {
      setImageUploadError('Se han ignorado uno o más archivos con tipos no permitidos.');
    }

    if (newImages.length + validFiles.length > availableSlots) {
      setImageUploadError(`No puedes seleccionar más de ${availableSlots} imagen(es).`);
      const slicedFiles = validFiles.slice(0, availableSlots);
      setNewImages(prevFiles => [...prevFiles, ...slicedFiles]);
    } else {
      setNewImages(prevFiles => [...prevFiles, ...validFiles]);
    }
  };

  // Maneja la eliminación de imágenes existentes
  const handleRemoveExistingImage = (imageId) => {
    const updatedImages = existingImages.filter(img => img.id !== imageId);
    setExistingImages(updatedImages);
    setImagesToDeleteIds(prevIds => [...prevIds, imageId]);
  };

  // Envía el formulario al backend
  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    setImageUploadError(null);

    // Crea el objeto FormData para enviar datos y archivos
    const formData = new FormData();

    // Añade el objeto de producto como un JSON, ya que Spring lo espera con @RequestPart
    formData.append('product', new Blob([JSON.stringify(productData)], { type: 'application/json' }));

    // Lógica para manejar las imágenes dependiendo de si se crea o se actualiza
    if (productToEdit) {
      // Para la actualización (PUT), el servidor espera 'newImages' y 'imagesToDeleteIds'.
      if (newImages.length > 0) {
        newImages.forEach(file => {
          formData.append('newImages', file);
        });
      }
      if (imagesToDeleteIds.length > 0) {
        // El nombre de la parte debe ser 'imagesToDeleteIds' y se envía como un array serializado.
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
      const url = productToEdit
        ? `http://localhost:8080/carlikeafriend/products/${productToEdit.id}`
        : 'http://localhost:8080/carlikeafriend/products';
        
      const method = productToEdit ? 'PUT' : 'POST';

      response = await fetch(url, {
        method: method,
        body: formData
        // No es necesario especificar el 'Content-Type' aquí, el navegador lo hará.
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || 'Ocurrió un error en el servidor.');
      }

      if (onProductSaved) {
        onProductSaved();
      }
    } catch (err) {
      console.error("Ocurrió un error al guardar el producto: " + err)
      setError("Ocurrió un error al guardar el producto. " + err);
    } finally {
      setLoading(false);
      setProductData({
        name: "",
        description: "",
        price: ""
      });
      setExistingImages([]);
      setImagesToDeleteIds([]);
      setNewImages([])
      // Limpiar el input de archivos
      if (fileInputRef.current) {
        fileInputRef.current.value = '';
      }

    }
  };

  return {
    productData,
    newImages,
    existingImages,
    error,
    loading,
    imageUploadError,
    availableSlots,
    canAddMoreImages,
    fileInputRef,
    handleChange,
    handleNewImageChange,
    handleRemoveExistingImage,
    handleSubmit,
  };
}