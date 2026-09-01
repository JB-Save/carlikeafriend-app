import { useState, useEffect, useRef, useMemo } from 'react';
import { PaginationControlsComponent } from './PaginationControlsComponent';
import { ProductCardComponent } from './ProductCardComponent';
import { useMessageModal } from '../context/MessageModalContext';
import { extractErrorMessage } from '../utils/extractErrorMessage';
import { API_CONFIG } from '../config/apiConfig';
import { useBooking } from '../context/BookingContext';
import { BookingSearchForm } from './BookingSearchForm';
import { validateAndCorrectBookingDates } from '../utils/dateHelpers';
import { format } from 'date-fns';

export const SearchSection = ({ productsPerPage, type }) => {
    const { setModalMessage } = useMessageModal();
    const [allCitiesWithBranches, setAllCitiesWithBranches] = useState([]);
    const [searchProducts, setSearchProducts] = useState([]);
    const [searchPage, setSearchPage] = useState(1);

    const { bookingData, updateBookingData } = useBooking();

    const [selectedPickupBranchName, setSelectedPickupBranchName] = useState(
        bookingData.pickupBranch || null
    );

    const [hasSearched, setHasSearched] = useState(!!bookingData.pickupBranch);
    const [generalError, setGeneralError] = useState(null);

    //Estados de carga ciudades con sucursales
    const [isLoadingCityWithBranch, setIsLoadingCityWithBranch] = useState(true);
    const CITY_WITH_BRANCHES_URL = API_CONFIG.CITIES_WITH_BRANCHES;

    //Estados de carga de productos
    const [isLoadingProduct, setIsLoadingProduct] = useState(true);
    const PRODUCT_URL = API_CONFIG.PRODUCTS_HOME_CATALOGUES;
    const PRODUCT_FILTER_URL = API_CONFIG.FILTERS;

    // Carga inicial de los datos de productos y ciudad/sucursal.
    useEffect(() => {
        const loadInitialData = async () => {
            // 1. Iniciamos estados de carga y reseteamos errores
            setIsLoadingCityWithBranch(true);
            setIsLoadingProduct(true);
            setGeneralError(null);

            try {
                // Las ciudades siempre se cargan para el formulario
                const citiesPromise = fetch(CITY_WITH_BRANCHES_URL, { method: 'GET' });

                let productsPromise;
                let isFilteredSearch = false;


                // Si el contexto ya tiene datos (ej. al volver del detalle), ejecutamos esa búsqueda
                if (bookingData.pickupBranch && bookingData.dateRange && bookingData.dateRange[0] && bookingData.dateRange[1]) {
                    isFilteredSearch = true;

                    // APLICACIÓN DE LA ARQUITECTURA DE CORRECCIÓN:
                    // Verificamos y corregimos silenciosamente si la fecha quedó en el pasado.
                    const corrections = validateAndCorrectBookingDates(
                        bookingData.dateRange,
                        bookingData.pickupTime,
                        bookingData.returnTime
                    );

                    // Variables de trabajo por defecto son las del contexto actual
                    let fetchDateRange = bookingData.dateRange;
                    let fetchPickupTime = bookingData.pickupTime;
                    let fetchReturnTime = bookingData.returnTime;

                    if (corrections) {
                        // Si hubo correcciones, usamos las fechas nuevas para el Fetch
                        fetchDateRange = corrections.dateRange;
                        fetchPickupTime = corrections.pickupTime;
                        fetchReturnTime = corrections.returnTime;

                        // Y sincronizamos inmediatamente el Contexto Global.
                        // Esto hará que BookingSearchForm se actualice visualmente para el usuario.
                        updateBookingData(corrections);
                    }

                    const pickupDateStr = format(fetchDateRange[0], 'yyyy-MM-dd');
                    const returnDateStr = format(fetchDateRange[1], 'yyyy-MM-dd');
                    const pickupTimeStr = format(fetchPickupTime, 'HH:mm:ss');
                    const returnTimeStr = format(fetchReturnTime, 'HH:mm:ss');

                    const formattedPickup = `${pickupDateStr}T${pickupTimeStr}`;
                    const formattedReturn = `${returnDateStr}T${returnTimeStr}`;


                    const URL = `${PRODUCT_FILTER_URL}?branchId=${bookingData.pickupBranch.id}&pickupDate=${formattedPickup}&returnDate=${formattedReturn}`;
                    productsPromise = fetch(URL, { method: 'GET' });
                    setHasSearched(true);
                    setSelectedPickupBranchName(bookingData.pickupBranch);
                } else {
                    // Si no hay contexto, carga general aleatoria
                    productsPromise = fetch(PRODUCT_URL, { method: 'GET' });
                    setHasSearched(false);
                }


                // 2. Lanzamos las peticiones en paralelo
                const [citiesRes, productsRes] = await Promise.all([citiesPromise, productsPromise]);

                // Validación de respuestas
                if (!citiesRes.ok || !productsRes.ok) {
                    const errorResponse = !citiesRes.ok ? citiesRes : productsRes;
                    const msg = await extractErrorMessage(errorResponse);
                    throw new Error(msg);
                };

                const [citiesData, productsData] = await Promise.all([
                    citiesRes.json(),
                    productsRes.json()
                ]);

                setAllCitiesWithBranches(citiesData);

                if (isFilteredSearch) {
                    setSearchProducts(productsData);
                } else {
                    // Mezclamos la flota aleatoria para el home inicial
                    const shuffledProducts = [...productsData].sort(() => 0.5 - Math.random());
                    setSearchProducts(shuffledProducts);
                }

            } catch (error) {
                console.error("Error en carga inicial de SearchSection: ", error);
                const message = error.message.includes("Failed to fetch") ? "No se pudo establecer conexión con el servidor." : error.message;
                setGeneralError(message || "Ocurrió un error inesperado.");
                setModalMessage("Ocurrió un problema en la aplicación.");
            } finally {
                setIsLoadingCityWithBranch(false);
                setIsLoadingProduct(false);
            }
        };
        loadInitialData();
    }, [CITY_WITH_BRANCHES_URL, PRODUCT_URL]);

    // Submit manejado por React Hook Form
    const onSearchSubmit = async (data) => {
        setHasSearched(true);
        setIsLoadingProduct(true);
        setGeneralError(null);

        try {

            // 1. Extraemos la parte de la fecha (YYYY-MM-DD) del calendario doble
            const pickupDateStr = format(data.dateRange[0], 'yyyy-MM-dd');
            const returnDateStr = format(data.dateRange[1], 'yyyy-MM-dd');

            // 2. Extraemos la parte de la hora (HH:mm:ss) de los selectores de tiempo
            const pickupTimeStr = format(data.pickupTime, 'HH:mm:ss');
            const returnTimeStr = format(data.returnTime, 'HH:mm:ss');

            // 3. Concatenamos con la 'T' para formar el estándar ISO 8601 que espera LocalDateTime
            // Resultado esperado: "2026-04-20T10:30:00"
            const formattedPickup = `${pickupDateStr}T${pickupTimeStr}`;
            const formattedReturn = `${returnDateStr}T${returnTimeStr}`;


            const URL = `${PRODUCT_FILTER_URL}?branchId=${data.pickupBranch.id}&pickupDate=${formattedPickup}&returnDate=${formattedReturn}`;

            const response = await fetch(URL, { method: 'GET' });

            if (response.ok) {
                const filteredData = await response.json();
                setSearchProducts(filteredData);
                setSelectedPickupBranchName(data.pickupBranch);
                setSearchPage(1);
            } else {
                const msg = await extractErrorMessage(response);
                throw new Error(msg);
            }
            document.getElementById('search-results')?.scrollIntoView({ behavior: 'smooth' });
        } catch (error) {
            console.error("Error al obtener productos: ", error);
            const message = error.message.includes("Failed to fetch") ? "No se pudo establecer conexión con el servidor." : error.message;
            setGeneralError(message || "Ocurrió un error inesperado.");
            setModalMessage("Ocurrió un problema en la aplicación.");
            setSearchProducts([]);
        } finally {
            setIsLoadingProduct(false);
        }
    };

    const totalPages = Math.ceil(searchProducts.length / productsPerPage);
    const currentSearchProductsDisplay = useMemo(() => {
        const startIndex = (searchPage - 1) * productsPerPage;
        return searchProducts.slice(startIndex, startIndex + productsPerPage);
    }, [searchProducts, searchPage, productsPerPage]);

    return (
        <section className="container mb-5">
            <BookingSearchForm
                citiesWithBranches={allCitiesWithBranches}
                onSearchSubmit={onSearchSubmit}
            />
            {/* SECCIÓN DE RESULTADOS */}
            <div className="mb-5" id="search-results">
                <div className="d-flex align-items-center justify-content-between mb-4 mt-2">
                    <h4 className="fw-bold mb-0 title-color">
                        {hasSearched ? `Resultados en ${selectedPickupBranchName ? selectedPickupBranchName.name : 'tu búsqueda'}` : 'Nuestra Flota Aleatoria'}
                    </h4>
                    <div className="px-3 py-1 rounded-pill fw-bold text-white shadow-sm" style={{ backgroundColor: '#6A5E9B', fontSize: '0.8rem' }}>
                        {searchProducts.length} vehículos
                    </div>
                </div>

                {generalError && <div className="alert alert-danger text-center rounded-3 shadow-sm">{generalError}</div>}

                <div className="product-listings-section">
                    {isLoadingProduct ? (
                        <div className="text-center py-5">
                            <div className="spinner-border" style={{ width: '3rem', height: '3rem' }} role="status"></div>
                            <p className="mt-3 fw-medium text-muted-custom">Actualizando catálogo...</p>
                        </div>
                    ) : (!currentSearchProductsDisplay || currentSearchProductsDisplay.length === 0) ? (
                        <div className="text-center py-5 bg-white rounded-4 shadow-sm border-0">
                            <i className="bi bi-car-front text-muted-custom fs-1 mb-3 d-block opacity-50"></i>
                            <h5 className="fw-bold text-muted-custom">Sin disponibilidad en el catálogo.</h5>
                            <p className="text-muted-custom">Prueba con otra sucursal o fechas diferentes.</p>
                        </div>
                    ) : (
                        <div className="row row-cols-1 row-cols-md-2 g-4">
                            {
                                currentSearchProductsDisplay.map(product => (
                                    <ProductCardComponent key={product.id} product={product} />
                                ))
                            }
                        </div>
                    )}

                    {searchProducts.length > productsPerPage && (
                        <PaginationControlsComponent
                            currentPage={searchPage}
                            totalPages={totalPages}
                            goToPage={setSearchPage}
                            type={type}
                        />
                    )}
                </div>
            </div>
        </section>
    );
}