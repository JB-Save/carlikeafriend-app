import { useCallback, useEffect, useMemo, useState } from 'react'
import { useFetch } from './useFetch';
import { useLocation } from 'react-router-dom';
import { useDebounce } from './useDebounce';
import { extractErrorMessage } from '../utils/extractErrorMessage';
import { API_CONFIG } from '../config/apiConfig';

export const useProductFilter = () => {

  const location = useLocation(); // Este hook te da acceso al objeto location, que contiene la URL actual y, lo más importante, el estado de navegación en location.state.
  const [allCategories, setAllCategories] = useState([]);
  const [allFeatures, setAllFeatures] = useState([]);
  const [products, setProducts] = useState([]);
  const [productError, setproductError] = useState(null);
  const [isLoadingProduct, setIsLoadingProduct] = useState(false);
  const [selectDefaultOption, setSelectDefaultOption] = useState('price_asc');

  //Estados de carga categorías
  const { data: categoryData, isLoading: isLoadingCategory, error: catError, fetchData: useFetchCategory } = useFetch();
  const [categoryError, setCategoryError] = useState(null);
  const CATEGORIES_URL = API_CONFIG.CATEGORIES;

  //Estados de carga características
  const { data: featureData, isLoading: isLoadingFeature, error: featError, fetchData: useFetchFeature } = useFetch();
  const [featureError, setFeatureError] = useState(null);
  const FEATURES_URL = API_CONFIG.FEATURES;

  // Usamos useState en lugar de useMemo para que no se recalculen (encojan) al filtrar
  const [absoluteMinPrice, setAbsoluteMinPrice] = useState(0);
  const [absoluteMaxPrice, setAbsoluteMaxPrice] = useState(0);

  // Actualización del rango mínimo y máximo
  useEffect(() => {
    if (!products || products.length === 0) return;

    const prices = products.map(product => product.price);
    const batchMin = Math.min(...prices);
    const batchMax = Math.max(...prices);
    const roundMax = Math.ceil(batchMax / 10000) * 10000; // Redondeo UX

    // Actualizar MÍNIMO solo si encontramos un precio más bajo que el histórico (o si es la primera carga)
    setAbsoluteMinPrice(prev => {
      if (prev === 0) return batchMin; // Primera carga
      return batchMin < prev ? batchMin : prev; // Solo bajamos, nunca subimos el mínimo
    });

    // Actualizar MÁXIMO solo si encontramos un precio más alto que el histórico
    setAbsoluteMaxPrice(prev => {
      if (prev === 0) return roundMax; // Primera carga
      return roundMax > prev ? roundMax : prev; // Solo subimos, nunca bajamos el máximo
    });

  }, [products]);

  //Estado de los filtros seleccionados
  const [filteredProductsState, setFilteredProductsState] = useState({
    categories: [],
    features: [],
    minPrice: 0,
    maxPrice: 0,
    sortBy: ''
  });

  // Si el usuario tiene el filtro en 0 (ej. reset o navegación), lo visualizamos con los valores absolutos
  useEffect(() => {
    if (absoluteMaxPrice > 0) {
      setFilteredProductsState(prevState => {
        // Si el maxPrice es 0, significa que no hay filtro activo, así que adoptamos el rango total
        if (prevState.maxPrice === 0) {
          return {
            ...prevState,
            minPrice: absoluteMinPrice,
            maxPrice: absoluteMaxPrice,
          };
        }
        return prevState;
      });
    }
  }, [absoluteMinPrice, absoluteMaxPrice]);

  // Usamos useDebounce para retrasar la llamada a la API y no saturarla
  const debouncedFilters = useDebounce(filteredProductsState, 500); // Espera 500ms

  const PRODUCT_FILTER_URL = API_CONFIG.FILTER;

  //Manejo de navegación desde otras páginas (ej. Home -> click en categoría)
  useEffect(() => {
    // Accede a los datos del estado de la navegación
    if (location.state && location.state.filterCategoryId) { // Verificamos si existe el estado de navegación y si contiene la propiedad filterCategoryId
      //setActiveDefaultCategory(location.state.filterCategoryId); // actualizamos el estado local con los datos
      setFilteredProductsState({
        categories: [location.state.filterCategoryId],
        features: [],
        minPrice: 0,
        maxPrice: 0,
        sortBy: ''
      });
    }
  }, [location]);


  // Carga inicial de los datos de categorías y características.
  useEffect(() => {
    useFetchCategory(CATEGORIES_URL, 'GET');
    useFetchFeature(FEATURES_URL, 'GET')
  }, []);


  // Procesa los datos una vez que han sido recibidos.
  useEffect(() => {
    if (categoryData) {
      setAllCategories(categoryData);
    } else if (catError) {
      console.error(catError);
      setCategoryError(catError.message || "Ocurrió un error inesperado");
    }

    if (featureData) {
      setAllFeatures(featureData);
    } else if (featError) {
      console.error(featError);
      setFeatureError(featError.message || "Ocurrió un error inesperado");
    }

  }, [categoryData, featureData, catError, featError]);

  //Lógica de filtrado de productos
  useEffect(() => {
    setIsLoadingProduct(false);
    setproductError(null);

    const fetchProducts = async () => {
      setIsLoadingProduct(true);

      //Mapeo de parámetros para Spring Boot
      const apiFilters = {};

      // Mapeamos 'categories' (React) a 'categoryIds' (Spring Boot @RequestParam)
      if (debouncedFilters.categories && debouncedFilters.categories.length > 0) {
        apiFilters.categoryIds = debouncedFilters.categories;
      };

      // Mapeamos 'features' (React) a 'featureIds' (Spring Boot @RequestParam)
      if (debouncedFilters.features && debouncedFilters.features.length > 0) {
        apiFilters.featureIds = debouncedFilters.features;
      }

      // Solo enviamos filtros de precio si son válidos y diferentes al rango completo inicial
      if (debouncedFilters.minPrice > 0) apiFilters.minPrice = debouncedFilters.minPrice;
      if (debouncedFilters.maxPrice > 0) apiFilters.maxPrice = debouncedFilters.maxPrice;

      // Enviamos el filtro de ordenamiento si no está vacío
      if (debouncedFilters.sortBy) apiFilters.sortBy = debouncedFilters.sortBy;

      // Construcción segura de Query Params
      const params = new URLSearchParams();
      Object.keys(apiFilters).forEach(key => {
        const value = apiFilters[key];
        if (Array.isArray(value)) {
          // Spring Boot acepta listas separadas por coma: ?categoryIds=1,2,3
          params.append(key, value.join(','));
        } else {
          params.append(key, value);
        }
      });

      const URL = `${PRODUCT_FILTER_URL}?${params.toString()}`;

      try {
        const response = await fetch(URL, {
          method: 'GET',
        });

        if (response.ok) {
          const productData = await response.json();
          setProducts(productData);
        } else {
          const msg = await extractErrorMessage(response);
          throw new Error(msg);
        }
      } catch (error) {
        console.error("Error al obtener productos: ", error);
        setproductError(error.message || "Ocurrió un error inesperado.");
      } finally {
        setIsLoadingProduct(false);
      }
    }
    fetchProducts();
  }, [debouncedFilters]); // Solo se ejecuta cuando los filtros "debounced" cambian


  // Handler universal para checklists y quick tabs
  // Recibe directamente el ID, el estado (checked) y el nombre del filtro ('categories' o 'features')
  const handleCheckListChange = useCallback((id, checked, name) => {

    // Aseguramos que el ID sea un número entero válido
    const selectedId = Number(id);

    if (isNaN(selectedId) || selectedId === 0) {
      console.error(`Intento de cambio de filtro con ID inválido para ${name}:`, id);
      return;
    }

    setFilteredProductsState(prevData => {
      const currentList = prevData[name] || [];

      if (checked) {
        // Agregar si está marcado y no existe
        if (!currentList.includes(selectedId)) {
          return {
            ...prevData,
            [name]: [...currentList, selectedId]
          };
        }
      } else {
        // Remover si no está marcado
        return {
          ...prevData,
          [name]: currentList.filter(item => item !== selectedId)
        };
      }
      return prevData;
    });
  }, []);

  // Maneja los cambios del slider de precio
  const handlePriceChange = useCallback((type, value) => {
    const numericValue = Number(value);
    if (isNaN(numericValue)) return;

    setFilteredProductsState(prevData => ({
      ...prevData,
      [type]: numericValue
    }));

  }, []);

  // handler para el ordenamiento
  const handleSortChange = useCallback((value) => {
    setSelectDefaultOption(value);
    setFilteredProductsState(prevData => ({
      ...prevData, sortBy: value
    }));
  }, []);

  // Función para resetear los filtros.
  const resetFilters = useCallback(() => {
    setFilteredProductsState({
      categories: [],
      features: [],
      minPrice: absoluteMinPrice,
      maxPrice: absoluteMaxPrice,
      sortBy: ''
    });
    setSelectDefaultOption('price_asc');
  }, [absoluteMinPrice, absoluteMaxPrice]);

  return {
    allCategories,
    allFeatures,
    isLoadingCategory,
    categoryError,
    isLoadingFeature,
    featureError,
    products,
    productError,
    isLoadingProduct,
    filteredProducts: filteredProductsState,
    absoluteMinPrice, // Exportar el rango absoluto
    absoluteMaxPrice, // Exportar el rango absoluto
    selectDefaultOption,
    resetFilters,
    handleCheckListChange,
    handlePriceChange,
    handleSortChange
  }
}
