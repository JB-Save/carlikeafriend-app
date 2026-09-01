import { useLocation, useNavigate } from 'react-router-dom';
import { ProductForm } from './ProductForm';
import { useEffect, useState } from 'react';
import { useMessageModal } from '../context/MessageModalContext';

// Componente principal para la página del producto
export const AddProductComponent = () => {
  const { setModalMessage } = useMessageModal(); // Hook para manejar el modal
  const [productToEdit, setProductToEdit] = useState(null);
  const location = useLocation(); // Este hook te da acceso al objeto location, que contiene la URL actual y, lo más importante, el estado de navegación en location.state.
  const navigate = useNavigate(); // <-- Usa useNavigate para la navegación
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
    navigate("/administration/product-list");
  };

  return (

    <div className="row">
      <div className="col-12 col-md-10 mx-auto">
        <div className="card custom-card-shadow custom-card-background p-4 p-md-5">
          <h4 className="fw-bold text-center form-title mb-4">
            {productToEdit ? 'Edición de Producto' : 'Registro de Nuevo Producto'}
          </h4>
          <ProductForm
            productToEdit={productToEdit}
            onProductSaved={handleProductSaved}
          />
        </div>
      </div>
    </div>

  );
}
