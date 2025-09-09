import { useLocation } from 'react-router-dom';
import { ProductForm } from './ProductForm';
import { useEffect, useState } from 'react';
import { useMessageModal } from '../context/MessageModalContext';

// Componente principal para la página del producto
export const AddProductComponent = () => {
   const { setModalMessage } = useMessageModal(); // Hook para manejar el modal
  const [productToEdit, setProductToEdit] = useState(null);
  const location = useLocation(); // Este hook te da acceso al objeto location, que contiene la URL actual y, lo más importante, el estado de navegación en location.state.

  // Usa useEffect para actualizar el estado cuando cambie la ubicación (navegación)
  useEffect(() => {
    // Accede a los datos del estado de la navegación
    if (location.state && location.state.productToEdit) { // Verificamos si existe el estado de navegación y si contiene la propiedad productToEdit
      setProductToEdit(location.state.productToEdit); // actualizamos el estado local con los datos del producto
    } else {
      setProductToEdit(null); // Limpia el estado si no hay datos de producto
    }
  }, [location]); // Vuelve a ejecutar cuando 'location' cambie

  // Esta función se puede usar para setear los datos del producto o mostrar un mensaje de éxito
  const handleProductSaved = () => {
   setModalMessage('¡Producto guardado exitosamente!');
   setProductToEdit(null);
  };

  return (

    <div col="row">
      <div className="col-12 col-md-8 mx-auto">
        <div className="card card-shadow card-background p-4">
          <h1 className="fs-3 fw-bold text-center form-title mb-4">
            {productToEdit ? 'Editar Producto' : 'Crear Producto'}
          </h1>
          <ProductForm
            productToEdit={productToEdit}
            onProductSaved={handleProductSaved}
          />
        </div>
      </div>
    </div>

  );
}
