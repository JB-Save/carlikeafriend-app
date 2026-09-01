import { useLocation, useNavigate } from 'react-router-dom';
import { CategoryForm } from './CategoryForm';
import { useEffect, useState } from 'react';
import { useMessageModal } from '../context/MessageModalContext';

// Componente principal para la página de la Categoría
export const AddCategoryComponent = () => {
  const { setModalMessage } = useMessageModal(); // Hook para manejar el modal
  const [categoryToEdit, setCategoryToEdit] = useState(null);
  const location = useLocation(); // Este hook te da acceso al objeto location, que contiene la URL actual y, lo más importante, el estado de navegación en location.state.
  const navigate = useNavigate(); // <-- Usa useNavigate para la navegación
  // Usa useEffect para actualizar el estado cuando cambie la ubicación (navegación)
  useEffect(() => {
    // Accede a los datos del estado de la navegación
    if (location.state && location.state.categoryToEdit) { // Verificamos si existe el estado de navegación y si contiene la propiedad categoryToEdit
      setCategoryToEdit(location.state.categoryToEdit); // actualizamos el estado local con los datos de la Categoría
    } else {
      setCategoryToEdit(null); // Limpia el estado si no hay datos de la Categoría
    }
  }, [location]); // Vuelve a ejecutar cuando 'location' cambie

  // Esta función se puede usar para setear los datos de la Categoría o mostrar un mensaje de éxito
  const handleCategorySaved = () => {
    setModalMessage('¡Categoría guardada exitosamente!');
    setCategoryToEdit(null);
    navigate("/administration/category-list");
  };

  return (

    <div className="row">
      <div className="col-12 col-md-10 mx-auto">
        <div className="card custom-card-shadow custom-card-background p-4 p-md-5">
          <h4 className="fw-bold text-center form-title mb-4">
            {categoryToEdit ? 'Edición de Categoría' : 'Registro de Nueva Categoría'}
          </h4>
          <CategoryForm
            categoryToEdit={categoryToEdit}
            onCategorySaved={handleCategorySaved}
          />
        </div>
      </div>
    </div>

  );
}
