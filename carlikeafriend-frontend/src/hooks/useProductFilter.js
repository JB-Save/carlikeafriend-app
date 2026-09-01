import { useCallback, useEffect, useState, useRef } from 'react'
import { useSearchParams } from 'react-router-dom';
import { useDebounce } from './useDebounce';
import { extractErrorMessage } from '../utils/extractErrorMessage';
import { calculateDynamicPrice, fetchFinancialData, calculateSliderLimitsPrice } from '../hooks/usePricing';
import { useBooking } from '../context/BookingContext';
import { validateAndCorrectBookingDates } from '../utils/dateHelpers';
import { format } from 'date-fns';
import { API_CONFIG } from '../config/apiConfig';

export const useProductFilter = () => {

  const { bookingData, updateBookingData } = useBooking();

  const [searchParams, setSearchParams] = useSearchParams();
  const [allCategories, setAllCategories] = useState([]);
  const [allFeatures, setAllFeatures] = useState([]);
  const [citiesWithBranches, setCitiesWithBranches] = useState([]);

  const [products, setProducts] = useState([]);
  const [productError, setproductError] = useState(null);
  const [isLoadingProduct, setIsLoadingProduct] = useState(false);

  const [selectedPickupBranchName, setSelectedPickupBranchName] = useState(null);
  const [hasSearched, setHasSearched] = useState(false);

  const [financialConfig, setFinancialConfig] = useState(null);
  const [transferFees, setTransferFees] = useState(null);
  const [financialError, setFinancialError] = useState(null);

  // Sincronización del ordenamiento inicial con la URL
  const [selectDefaultOption, setSelectDefaultOption] = useState(() => searchParams.get('sortBy') || 'price_asc');

  //Estados de carga categorías
  const [isLoadingCategory, setIsLoadingCategory] = useState(true);
  const [categoryError, setCategoryError] = useState(null);

  //Estados de carga características
  const [isLoadingFeature, setIsLoadingFeature] = useState(true);
  const [featureError, setFeatureError] = useState(null);

  //Estados de carga ciudades con sucursales
  const [isLoadingCityWithBranch, setIsLoadingCityWithBranch] = useState(true);
  const [cityWithBranchError, setCityWithBranchError] = useState(null);
  

  // Topes base del catálogo desde el Backend (Tarifa de 1 día)
  const [baseCatalogLimits, setBaseCatalogLimits] = useState({ min: 0, max: 0 });

  // Usamos useState en lugar de useMemo para que no se recalculen (encojan) al filtrar
  const [absoluteMinPrice, setAbsoluteMinPrice] = useState(0);
  const [absoluteMaxPrice, setAbsoluteMaxPrice] = useState(0);

  // Inicialización del estado de filtros desde los parámetros de la URL
  const [filteredProductsState, setFilteredProductsState] = useState(() => {
    return {
      categories: searchParams.get('categories') ? searchParams.get('categories').split(',').map(Number) : [],
      features: searchParams.get('features') ? searchParams.get('features').split(',').map(Number) : [],
      minPrice: searchParams.get('minPrice') ? Number(searchParams.get('minPrice')) : 0,
      maxPrice: searchParams.get('maxPrice') ? Number(searchParams.get('maxPrice')) : 0,
      sortBy: searchParams.get('sortBy') || 'price_asc'
    };
  });

  // Efecto para actualizar la URL cada vez que el estado de los filtros cambie
  useEffect(() => {
    const params = new URLSearchParams();
    if (filteredProductsState.categories.length > 0) params.set('categories', filteredProductsState.categories.join(','));
    if (filteredProductsState.features.length > 0) params.set('features', filteredProductsState.features.join(','));
    if (filteredProductsState.minPrice > 0) params.set('minPrice', filteredProductsState.minPrice.toString());
    if (filteredProductsState.maxPrice > 0) params.set('maxPrice', filteredProductsState.maxPrice.toString());
    if (filteredProductsState.sortBy) params.set('sortBy', filteredProductsState.sortBy);

    // replace: true evita que cada cambio de filtro genere una entrada nueva en el historial del botón "atrás"
    setSearchParams(params, { replace: true });
  }, [filteredProductsState, setSearchParams]);

  // Guardamos las condiciones anteriores para compararlas
  const prevBookingState = useRef({
    pickup: bookingData?.pickupBranch?.id,
    return: bookingData?.returnBranch?.id,
    start: bookingData?.dateRange?.[0]?.getTime(),
    end: bookingData?.dateRange?.[1]?.getTime()
  });

  // DETECTOR DE CAMBIOS Y CALCULADOR DE LÍMITES COMBINADO
  // Calcula los topes absolutos del Slider y actualiza los filtros instantáneamente sin depender del ciclo de renderizado.
  useEffect(() => {
    if (!baseCatalogLimits || baseCatalogLimits.max === 0) return;

    // 1. Extraer fechas y calcular límites efectivos (Escalado de precios)
    let pickupDate = null;
    let returnDate = null;
    if (bookingData?.dateRange?.[0] && bookingData?.dateRange?.[1]) {
      // CLONAMOS los objetos Date antes de usar setHours para evitar MUTAR el Contexto Global
      pickupDate = new Date(bookingData.dateRange[0]);
      returnDate = new Date(bookingData.dateRange[1]);
      // Aplicar las horas seleccionadas para precisión
      if (bookingData.pickupTime) {
        pickupDate.setHours(bookingData.pickupTime.getHours(), bookingData.pickupTime.getMinutes(), 0, 0);
      }
      if (bookingData.returnTime) {
        returnDate.setHours(bookingData.returnTime.getHours(), bookingData.returnTime.getMinutes(), 0, 0);
      }
    }

    let minEffective = baseCatalogLimits.min;
    let maxEffective = baseCatalogLimits.max;

    // Si hay fechas y config, escalamos los topes a "Presupuesto Total"
    if (pickupDate && returnDate && financialConfig) {
      minEffective = calculateSliderLimitsPrice(
        baseCatalogLimits.min,
        pickupDate,
        returnDate,
        bookingData?.pickupBranch?.id,
        bookingData?.returnBranch?.id,
        financialConfig,
        transferFees
      );

      maxEffective = calculateSliderLimitsPrice(
        baseCatalogLimits.max,
        pickupDate,
        returnDate,
        bookingData?.pickupBranch?.id,
        bookingData?.returnBranch?.id,
        financialConfig,
        transferFees
      );
    }

    // 2. REDONDEO MATEMÁTICO PERFECTO:
    // Forzamos a que el límite inferior sea un múltiplo exacto de 10.000 hacia abajo.
    // Esto garantiza que el <input type="range" step="10000"> no se rompa ni pierda valores.
    const roundMin = Math.floor(minEffective / 10000) * 10000;
    const roundMax = Math.ceil(maxEffective / 10000) * 10000;

    const newAbsMin = roundMin >= roundMax ? Math.max(0, roundMin - 10000) : roundMin;
    const newAbsMax = roundMin >= roundMax ? roundMax + 10000 : roundMax;

    setAbsoluteMinPrice(newAbsMin);
    setAbsoluteMaxPrice(newAbsMax);

    // 3. SINCRONIZACIÓN CON LOS FILTROS (Solución al fallo de los ceros)
    const currentPickup = bookingData?.pickupBranch?.id;
    const currentReturn = bookingData?.returnBranch?.id;
    const currentStart = bookingData?.dateRange?.[0]?.getTime();
    const currentEnd = bookingData?.dateRange?.[1]?.getTime();
    const prev = prevBookingState.current;

    // Si cambió alguna sucursal o las fechas (los días totales cambian)
    const isSearchContextChanged = (
      currentPickup !== prev.pickup ||
      currentReturn !== prev.return ||
      currentStart !== prev.start ||
      currentEnd !== prev.end
    );


    if (isSearchContextChanged) {
      // Si el usuario cambió la búsqueda, RESETEAMOS los sliders al nuevo rango total seguro (evitamos el 0).
      prevBookingState.current = {
        pickup: currentPickup, return: currentReturn, start: currentStart, end: currentEnd
      };

      setFilteredProductsState(prevState => ({
        ...prevState,
        minPrice: newAbsMin,
        maxPrice: newAbsMax
      }));
    } else {
      // Corrección de seguridad rutinaria (ej. si era 0 al cargar por primera vez o quedó fuera de límites)
      setFilteredProductsState(prevState => {
        let changed = false;
        let newMin = prevState.minPrice;
        let newMax = prevState.maxPrice;

        // Si es 0 o está fuera del nuevo límite calculado, forzamos corrección
        if (newMax === 0 || newMax > newAbsMax || newMax < newAbsMin) {
          newMax = newAbsMax;
          changed = true;
        }
        if (newMin === 0 || newMin < newAbsMin || newMin > newAbsMax) {
          newMin = newAbsMin;
          changed = true;
        }

        // Corrección de seguridad en caso de cruce
        if (newMin > newMax) {
          newMin = newAbsMin;
          newMax = newAbsMax;
          changed = true;
        }

        return changed ? {
          ...prevState,
          minPrice: newMin,
          maxPrice: newMax,
        } : prevState;
      });
    }
  }, [baseCatalogLimits, bookingData, financialConfig, transferFees]);

  // Usamos useDebounce para retrasar la llamada a la API y no saturarla
  const debouncedFilters = useDebounce(filteredProductsState, 500); // Espera 500ms

  // Carga inicial de los datos
  useEffect(() => {
    const loadInitialData = async () => {
      // 1. Iniciamos estados de carga y reseteamos errores
      setIsLoadingCategory(true);
      setIsLoadingFeature(true);
      setIsLoadingCityWithBranch(true);
      setCategoryError(null);
      setFeatureError(null);
      setCityWithBranchError(null);
      setFinancialError(null);

      try {
        // 2. Lanzamos las peticiones en paralelo
        const catalogsPromise = Promise.all([
          fetch(API_CONFIG.CATEGORIES, { method: 'GET' }),
          fetch(API_CONFIG.FEATURES, { method: 'GET' }),
          fetch(API_CONFIG.CITIES_WITH_BRANCHES, { method: 'GET' }),
          fetch(API_CONFIG.PRODUCT_PRICE_RANGE, { method: 'GET' })
        ]);

        // 3. Disparamos la petición financiera usando el Singleton (Caché)
        // Esto NO hará peticiones a la red si usePricing ya lo hizo primero (o viceversa)
        const financialPromise = fetchFinancialData();

        // Esperamos ambas
        const [[resCat, resFeat, resCity, resPriceRange], { config, fees }] = await Promise.all([
          catalogsPromise,
          financialPromise
        ]);

        // 4. Seteamos los datos financieros en el estado
        setFinancialConfig(config);
        setTransferFees(fees);

        // Procesar Rango de Precios 
        if (resPriceRange.ok) {
          const rangeData = await resPriceRange.json();
          setBaseCatalogLimits({ min: rangeData.minPrice, max: rangeData.maxPrice });
        }

        // 5. Procesar Categorías
        if (resCat.ok) {
          const catData = await resCat.json();
          setAllCategories(catData);
        } else {
          // Integración de extractErrorMessage para errores del servidor (400, 500, etc.)
          const catMsg = await extractErrorMessage(resCat);
          setCategoryError(catMsg);
        }

        // 6. Procesar Características
        if (resFeat.ok) {
          const featData = await resFeat.json();
          setAllFeatures(featData);
        } else {
          const featMsg = await extractErrorMessage(resFeat);
          setFeatureError(featMsg);
        }

        // 7. Procesar Ciudades/Sucursales
        if (resCity.ok) {
          const cityData = await resCity.json();
          setCitiesWithBranches(cityData);
        } else {
          const cityMsg = await extractErrorMessage(resCity);
          setCityWithBranchError(cityMsg);
        }

      } catch (error) {
        console.error("Error de red en carga inicial:", error);
        // Errores de conexión (cuando no hay respuesta del servidor)
        const networkMsg = "No se pudo establecer conexión con el servidor.";
        setCategoryError(networkMsg);
        setFeatureError(networkMsg);
        setCityError(networkMsg);
        // Asignamos un mensaje específico para la parte financiera
        setFinancialError("El cálculo dinámico no está disponible temporalmente. Mostrando tarifas base.");
      } finally {
        setIsLoadingCategory(false);
        setIsLoadingFeature(false);
        setIsLoadingCityWithBranch(false);
      }
    };

    loadInitialData();
  }, []);


  //Lógica de filtrado de productos
  useEffect(() => {
    const fetchProducts = async () => {
      setIsLoadingProduct(true);
      setproductError(null);

      //Mapeo de parámetros para Spring Boot
      const apiFilters = {};

      // Inyectar datos de búsqueda del formulario global
      if (bookingData.pickupBranch && bookingData.dateRange && bookingData.dateRange[0] && bookingData.dateRange[1]) {

        apiFilters.branchId = bookingData.pickupBranch.id;

        // Enviar la sucursal de entrega si el switch está encendido
        if (bookingData.differentReturnBranch && bookingData.returnBranch) {
          apiFilters.returnBranchId = bookingData.returnBranch.id;
        }

        // --- ARQUITECTURA DE CORRECCIÓN ---
        // Verificamos y corregimos silenciosamente si la fecha quedó en el pasado.
        const corrections = validateAndCorrectBookingDates(
          bookingData.dateRange,
          bookingData.pickupTime,
          bookingData.returnTime
        );

        let fetchDateRange = bookingData.dateRange;
        let fetchPickupTime = bookingData.pickupTime;
        let fetchReturnTime = bookingData.returnTime;

        if (corrections) {
          // Usar las fechas corregidas para la petición
          fetchDateRange = corrections.dateRange;
          fetchPickupTime = corrections.pickupTime;
          fetchReturnTime = corrections.returnTime;

          // Sincronizamos el Contexto para que el UI se actualice
          updateBookingData(corrections);
        }

        const pickupDateStr = format(fetchDateRange[0], 'yyyy-MM-dd');
        const returnDateStr = format(fetchDateRange[1], 'yyyy-MM-dd');
        const pickupTimeStr = format(fetchPickupTime, 'HH:mm:ss');
        const returnTimeStr = format(fetchReturnTime, 'HH:mm:ss');

        apiFilters.pickupDate = `${pickupDateStr}T${pickupTimeStr}`;
        apiFilters.returnDate = `${returnDateStr}T${returnTimeStr}`;
        setHasSearched(true);
        setSelectedPickupBranchName(bookingData.pickupBranch);
      }

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

      try {
        const response = await fetch(`${API_CONFIG.FILTERS}?${params.toString()}`, {
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
        const message = error.message.includes("Failed to fetch") ? "No se pudo establecer conexión con el servidor." : error.message;
        setproductError(message || "Ocurrió un error inesperado.");
      } finally {
        setIsLoadingProduct(false);
      }
    }
    fetchProducts();
  }, [debouncedFilters]); // Quitamos bookingData explícitamente para evitar ciclos infinitos causados por el updateBookingData


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
      sortBy: 'price_asc'
    });
    setSelectDefaultOption('price_asc');
  }, [absoluteMinPrice, absoluteMaxPrice]);

  return {
    allCategories,
    allFeatures,
    citiesWithBranches,
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
    financialError,
    selectDefaultOption,
    selectedPickupBranchName,
    hasSearched,
    resetFilters,
    handleCheckListChange,
    handlePriceChange,
    handleSortChange
  }
}
