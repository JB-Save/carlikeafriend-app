import { useState, useEffect, useContext } from "react";
import { ImageGalleryModal } from "../components/ImageGalleryModal";
import { useNavigate, useParams } from "react-router-dom";
import { useMessageModal } from "../context/MessageModalContext";
import { useCurrencyFormatter } from "../hooks/useCurrencyFormatter";
import { API_CONFIG } from "../config/apiConfig";
import { useBooking } from "../context/BookingContext";
import { BookingSearchForm } from "../components/BookingSearchForm";
import { format, addMonths } from 'date-fns';
import { extractErrorMessage } from "../utils/extractErrorMessage";
import { usePricing } from "../hooks/usePricing";
import { validateAndCorrectBookingDates } from "../utils/dateHelpers";
import { SPECIAL_FEATURES } from "../utils/featureConstants";
import { ShareModalComponent } from "../components/ShareModalComponent";
import { UserContext } from "../context/UserContext";
import { useSocialShare } from "../hooks/useSocialShare";
import { ProductReviewsComponent } from "../components/ProductReviewsComponent";
import "../styles/MainStyle.css";
import "../styles/ProductDetailStyle.css";

export const ProductDetailsComponent = () => {
    const { token, isAuthenticated } = useContext(UserContext);
    const { setModalMessage } = useMessageModal();
    const { formatCurrency } = useCurrencyFormatter();
    const { id } = useParams(); // <-- Obtener el ID de la URL  

    // 1. Estados de carga inicial
    const [singleProduct, setSingleProduct] = useState(null)
    const [allCitiesWithBranches, setAllCitiesWithBranches] = useState([]);
    const [isInitialLoading, setIsInitialLoading] = useState(true);
    const [initialError, setInitialError] = useState(null);

    // 2. Estados de Disponibilidad
    const { bookingData, updateBookingData } = useBooking();
    const [blockedDates, setBlockedDates] = useState([]);
    const [loadingAvailability, setLoadingAvailability] = useState(false);
    const [rangeError, setRangeError] = useState(null);
    const [availabilityError, setAvailabilityError] = useState(null);

    // Estado para forzar el re-renderizado profundo del formulario
    const [formKey, setFormKey] = useState(0);

    const [modalOpen, setModalOpen] = useState(false);
    const [shareModalOpen, setShareModalOpen] = useState(false);

    const navigate = useNavigate();

    const { pricingDetails, isLoadingPricing } = usePricing(singleProduct, bookingData);
    const { logShareInteraction } = useSocialShare(singleProduct?.id);

    // Este useEffect se ejecuta cuando cambie el id.
    useEffect(() => {
        const loadInitialData = async () => {
            if (!id) return;
            setIsInitialLoading(true);
            setInitialError(null);

            try {
                const PRODUCT_URL = `${API_CONFIG.PRODUCTS}/${id}`; // <-- Usar el ID para construir la URL
                const CITY_WITH_BRANCHES_URL = API_CONFIG.CITIES_WITH_BRANCHES;

                const [productRes, branchesRes] = await Promise.all([
                    fetch(PRODUCT_URL, { method: 'GET' }),
                    fetch(CITY_WITH_BRANCHES_URL, { method: 'GET' })
                ]);

                if (!productRes.ok || !branchesRes.ok) {
                    const errorResponse = !productRes.ok ? productRes : branchesRes;
                    const msg = await extractErrorMessage(errorResponse);
                    throw new Error(msg);
                }

                const [productData, branchesData] = await Promise.all([
                    productRes.json(),
                    branchesRes.json()
                ]);

                setSingleProduct(productData);
                setAllCitiesWithBranches(branchesData);
            } catch (error) {
                console.error("Error en carga inicial:", error);
                const message = error.message.includes("Failed to fetch") ? "No se pudo establecer conexión con el servidor." : error.message;
                setInitialError(message);
                setModalMessage("Ocurrió un problema en la aplicación.");
            } finally {
                setIsInitialLoading(false);
            }
        };
        loadInitialData();
    }, [id]);

    // Función extraída para poder ser llamada desde el botón de reintento
    const fetchBlockedDates = async () => {
        setLoadingAvailability(true);
        setAvailabilityError(null);
        setRangeError(null);

        try {
            const start = new Date();
            // Sumamos 6 meses usando date-fns de forma segura
            const end = addMonths(start, 6);

            // Formateamos en la ZONA HORARIA LOCAL del usuario (YYYY-MM-DD)
            const startStr = format(start, 'yyyy-MM-dd');
            const endStr = format(end, 'yyyy-MM-dd');

            const BLOCKED_DATES_URL = `${API_CONFIG.RESERVATIONS}/${singleProduct.id}/blocked-dates?branchId=${bookingData.pickupBranch.id}&startDate=${startStr}&endDate=${endStr}`;

            const res = await fetch(BLOCKED_DATES_URL, { method: 'GET' });

            if (res.ok) {
                const responseData = await res.json();
                setBlockedDates(responseData.blockedDates);
            } else {
                const msg = await extractErrorMessage(res);
                throw new Error(msg);
            }
        } catch (error) {
            console.error("Error al obtener las fechas bloqueadas:", error);
            setAvailabilityError("No se puede obtener la información de disponibilidad en este momento.");
        } finally {
            setLoadingAvailability(false);
        }
    };

    // Efecto que dispara la consulta cuando cambian las dependencias
    useEffect(() => {
        if (singleProduct?.id && bookingData?.pickupBranch?.id) {
            fetchBlockedDates();
        } else {
            setBlockedDates([]);
            setAvailabilityError(null);
        }
    }, [singleProduct?.id, bookingData?.pickupBranch?.id]);

    const isRangeBlocked = (startDate, endDate, blockedDatesArray) => {
        if (!startDate || !endDate || !blockedDatesArray || blockedDatesArray.length === 0) {
            return false;
        }

        let current = new Date(startDate);
        current.setHours(0, 0, 0, 0);
        const endD = new Date(endDate);
        endD.setHours(0, 0, 0, 0);

        // Convertimos los strings a fechas una sola vez por rendimiento
        const blockedDatesObjects = blockedDatesArray.map(dateStr => {
            return new Date(`${dateStr}T00:00:00`).getTime();
        });

        while (current <= endD) {
            if (blockedDatesObjects.includes(current.getTime())) {
                return true; // Encontró un cruce de fechas
            }
            current.setDate(current.getDate() + 1);
        }
        return false;
    };

    const handleReserveSubmit = () => {
        // 1. Validaciones de presencia
        if (!bookingData.pickupBranch) {
            setModalMessage("Por favor selecciona una sucursal de recogida.");
            return;
        }

        if (bookingData.differentReturnBranch && !bookingData.returnBranch) {
            setModalMessage("Por favor selecciona una sucursal de entrega.");
            return;
        }

        if (!bookingData.dateRange[0] || !bookingData.dateRange[1]) {
            setModalMessage("Por favor selecciona un rango de fechas de alquiler válido.");
            return;
        }
        if (rangeError || availabilityError) {
            setModalMessage("Corrige la disponibilidad antes de continuar.");
            return;
        }

        // 2. Revisamos si las fechas ya caducaron mientras el usuario leía la página
        const corrections = validateAndCorrectBookingDates(
            bookingData.dateRange,
            bookingData.pickupTime,
            bookingData.returnTime
        );

        if (corrections) {
            // Refrescamos el formulario y los precios inyectando los datos válidos al contexto
            updateBookingData(corrections);

            // Al incrementar la key, React destruye y recrea el formulario,
            // forzando a react-hook-form a leer los nuevos defaultValues del contexto instantáneamente.
            setFormKey(prevKey => prevKey + 1);

            // Avisamos al usuario y bloqueamos la navegación para que revise el nuevo precio/horas
            setModalMessage("Las fechas han sido ajustadas automáticamente para cumplir con nuestras políticas (Máximo 30 días o fecha caducada).");
            return;
        }

        // 3. Verificación contra fechas bloqueadas del calendario backend
        const datesAreBlocked = isRangeBlocked(
            bookingData.dateRange[0],
            bookingData.dateRange[1],
            blockedDates
        );

        if (datesAreBlocked) {
            setModalMessage("Las fechas que tienes seleccionadas no están disponibles para este vehículo. Por favor, modifícalas en el formulario.");
            return;
        }

        // 4. Verificamos que el usuario esté logueado
        if (!isAuthenticated || !token) {
            setModalMessage("Debes iniciar sesión para reservar. Si no estás registrado(a), crea una cuenta.");
            navigate('/signin');
            return;
        }

        // 5. Todo perfecto, pasamos al checkout
        navigate('/reservation-checkout');
    };

    const handleReviewAdded = async () => {
        try {

            const response = await fetch(`${API_CONFIG.PRODUCTS}/${id}`, {
                method: 'GET'
            });

            if (!response.ok) {
                throw new Error('No se pudieron actualizar las estadísticas del producto');
            }

            const updatedProduct = await response.json();

            // Actualizamos el estado principal para que la interfaz refleje el nuevo promedio y contador
            setSingleProduct(updatedProduct);

        } catch (error) {
            console.error("Error: ", error);
        }
    };

    const imagePath = (item, index) => {
        return item?.productImages?.[index]?.imagePath
            ? `${API_CONFIG.PRODUCT_IMAGES_BASE}${item.productImages[index].imagePath}`
            : 'https://placehold.co/400x250/E0F2FE/3B82F6?text=No+Imagen';
    };


    // Manejar errores de imágen
    const handleImageError = (e) => {
        e.target.onerror = null; // Prevenir loop infinito
        e.target.src = 'https://placehold.co/400x250/E0F2FE/3B82F6?text=Imagen+No+Disponible';
    };

    // Manejar errores del ícono
    const handleIconError = (e) => {
        e.target.onerror = null;
        e.target.src = 'https://placehold.co/48x48/E0F2FE/3B82F6?text=:(';
    };

    const showModal = () => {
        setModalOpen(true);
    };

    const showShareModal = async () => {
        if (!isAuthenticated || !token) {
            setModalMessage("Debes iniciar sesión para compartir productos.");
            navigate('/signin');
            return;
        }

        const productUrl = `${window.location.origin}/product-details/${singleProduct.id}`;
        const shareData = {
            title: `Renta un ${singleProduct.name}`,
            text: `¡Mira este increíble ${singleProduct.name} que encontré para rentar!`,
            url: productUrl
        };

        // Verificamos si el navegador soporta el menú nativo
        if (navigator.share && navigator.canShare && navigator.canShare(shareData)) {
            try {
                // Registramos la métrica de forma asíncrona sin bloquear la UI
                logShareInteraction('NATIVE_MOBILE', 'Interacción nativa desde dispositivo móvil.');

                // Desplegamos el menú nativo del celular
                await navigator.share(shareData);
            } catch (error) {
                // Si el usuario simplemente cierra el menú sin compartir, da un 'AbortError'
                // Lo ignoramos. Si es un error real, abrimos el modal.
                if (error.name !== 'AbortError') {
                    console.error('Error al usar Web Share API:', error);
                    setShareModalOpen(true);
                }
            }
        } else {
            // FLUJO DE ESCRITORIO: El navegador no soporta menú nativo, abrimos el modal
            setShareModalOpen(true);
        }
    };


    if (isInitialLoading) {
        return (
            <div className="d-flex flex-column justify-content-center align-items-center min-vh-100">
                <div className="spinner-border" role="status">
                </div>
                <p className="mt-3 product-detail-text-muted">Cargando producto y sucursales...</p>
            </div>
        );
    }


    if (initialError || !singleProduct) {
        return (
            <div className="d-flex flex-column align-items-center justify-content-center min-vh-100 p-4">
                <div className="alert alert-danger text-center w-100 max-w-md">{initialError || "Error desconocido."}</div>
                <div className="text-center product-detail-text-muted mt-3">
                    No hay detalle disponible del producto.
                </div>
            </div>
        );
    }


    return (
        <>
            <main className="min-vh-100 container-fluid py-3 py-md-4">
                {/* Header Superior */}
                <div className="container header-product-datail d-flex flex-column flex-sm-row justify-content-sm-between align-items-center mt-2 mb-4 sticky-top px-3 px-md-4 py-2">
                    <h1 className="h4 mb-2 mb-sm-0 product-title text-center text-sm-start w-100 text-truncate">{singleProduct.name}</h1>
                    <button onClick={() => navigate(-1)} className="btn back-btn rounded-pill d-flex align-items-center px-3 shadow-sm flex-shrink-0">
                        <i className="bi bi-arrow-left me-2"></i> Volver
                    </button>
                </div>

                {/* Contenido Principal Responsivo */}
                <section className="container detail-content-section p-3 p-md-4 p-lg-5 rounded-4 mb-5">
                    <div className="row mb-4">
                        <div className="col-12">
                            {/* Estructura Original de Imágenes */}
                            <div className="row g-2 g-md-3 align-items-stretch">
                                {/* Imagen Principal */}
                                <div className="col-12 col-md-6 position-relative d-flex flex-column">
                                    <div className="main-image-wrapper shadow rounded-3 overflow-hidden">
                                        <div className="position-absolute top-0 start-0 m-2 m-md-3 z-3 d-flex align-items-center bg-white bg-opacity-75 p-1 p-md-2 rounded-3 shadow-sm">
                                            <div className="d-flex align-items-center px-1 px-md-2 py-1 me-1 me-md-2">
                                                <i className="bi bi-star-fill text-warning me-1"></i>
                                                <span className="fw-bold fs-6" style={{ color: '#2e2e84' }}>
                                                    {singleProduct.averageRating?.toFixed(1) || "0.0"}
                                                </span>
                                            </div>
                                            <div className="ps-1 ps-md-2 border-start product-detail-text-muted small">
                                                {singleProduct.totalReviews || 0} Reviews
                                            </div>
                                        </div>
                                        <button
                                            onClick={showShareModal}
                                            className="btn position-absolute top-0 end-0 m-3 p-1 bg-white shadow-sm z-3 d-flex align-items-center justify-content-center"
                                            style={{ width: '35px', height: '35px', opacity: 0.9 }}
                                        >
                                            <i className="bi bi-share"
                                                style={{ fontSize: '1.2rem', transform: 'translateY(2px)' }}></i>
                                        </button>
                                        <img
                                            src={imagePath(singleProduct, 0)}
                                            className="gallery-main-image main-image-responsive"
                                            alt={singleProduct.name}
                                            onError={handleImageError}
                                        />
                                    </div>
                                </div>

                                {/* Miniaturas */}
                                <div className="col-12 col-md-6 d-none d-md-block">
                                    <div className="row g-2 g-md-3 h-100">
                                        {singleProduct.productImages.slice(1, 5).map((image, index) => (
                                            <div className="col-6" key={index}>
                                                <img
                                                    src={imagePath(singleProduct, index + 1)}
                                                    className="img-fluid gallery-thumbnail w-100 h-100 object-fit-cover shadow-sm rounded-3"
                                                    alt={`Miniatura ${index + 2}`}
                                                    onError={handleImageError}
                                                    style={{ aspectRatio: '4/3' }}
                                                />
                                            </div>
                                        ))}
                                    </div>
                                </div>
                            </div>

                            {/* Botón Ver Más */}
                            <div className="d-flex justify-content-end justify-content-md-end mt-3">
                                <button className="btn btn-link product-detail-text-primary text-decoration-none fw-bold p-0 fs-5" onClick={showModal}>
                                    Ver más <i className="bi bi-plus-lg ms-1"></i>
                                </button>
                            </div>
                        </div>
                    </div>

                    {/* Descripción y Características*/}
                    <div className="mt-4 mt-md-5">
                        <p className="product-detail-text mb-4">{singleProduct.description}</p>

                        <h3 className="fw-bold mt-4 mt-md-5 mb-3 product-feature-text border-bottom pb-2 fs-4">
                            Características del vehículo
                        </h3>

                        {/* Grilla de Características */}
                        <div className="row row-cols-1 row-cols-sm-2 row-cols-md-3 row-cols-lg-4 g-3 g-md-4 mt-2">
                            {singleProduct.features.map((feature, index) => {
                                const IMAGE_URL = feature?.icon?.imagePath
                                    ? `${API_CONFIG.FEATURE_IMAGES_BASE}${feature.icon.imagePath}`
                                    : 'https://placehold.co/48x48/E0F2FE/3B82F6?text=?';

                                const getQuantity = () => {
                                    const featureName = feature.name.toLowerCase();

                                    if (SPECIAL_FEATURES.PASAJERO.includes(featureName)) {
                                        return singleProduct.passengerCapacity || '4';
                                    }
                                    if (SPECIAL_FEATURES.PUERTA.includes(featureName)) {
                                        return singleProduct.numberOfDoors || '4';
                                    }
                                    if (SPECIAL_FEATURES.EQUIPAJE.includes(featureName)) {
                                        return singleProduct.baggageCapacity || '2';
                                    }
                                    return null;
                                };

                                const quantity = getQuantity();

                                return (
                                    <div className="col d-flex align-items-center" key={index}>
                                        <div className="p-2 rounded-3 bg-white shadow-sm me-3 flex-shrink-0" style={{ width: '48px', height: '48px' }}>
                                            <img src={IMAGE_URL}
                                                alt={feature.name}
                                                className="w-100 h-100 object-fit-contain"
                                                onError={handleIconError}
                                            />
                                        </div>
                                        <div className="product-detail-text fw-semibold text-break">{feature.name} {quantity ? `(${quantity})` : ''}</div>
                                    </div>
                                );
                            })}
                        </div>
                    </div>

                    {/* Bloque de Políticas */}
                    <div className="container mt-5 py-4 border-top">
                        <div className="row">
                            <div className="col-12">
                                <h3 className="fw-bold product-policy-title mb-4 fs-4" style={{ textDecoration: 'underline', textDecorationColor: '#1f88e6', textUnderlineOffset: '10px' }}>
                                    Políticas de uso y precauciones
                                </h3>
                            </div>
                        </div>
                        {/* Distribución en columnas responsivas: 1 columna en móvil, 3 en escritorio */}
                        <div className="row row-cols-1 row-cols-md-3 g-4">
                            {singleProduct.policies.map((policy, index) => (
                                <div className="col" key={index}>
                                    <div className="h-100">
                                        <h5 className="fw-bold product-detail-text-primary">{policy.name}</h5>
                                        <p className="product-detail-text product-detail-text-muted small">
                                            {policy.content || "Consulte en oficina los detalles específicos de esta política para su vehículo seleccionado."}
                                        </p>
                                    </div>
                                </div>
                            ))}
                        </div>
                    </div>

                    {/* === VERIFICACIÓN DE DISPONIBILIDAD === */}
                    <div className="mt-5 bg-light p-4 p-md-5 rounded-4 border">
                        <h3 className="fw-bold mb-4 border-bottom pb-3 fs-4" style={{ color: '#2e2e84' }}>
                            <i className="bi bi-calendar-check me-2"></i> Verifica Disponibilidad
                        </h3>

                        {!bookingData.pickupBranch && (
                            <div className="alert alert-info d-flex align-items-center" role="alert">
                                <i className="bi bi-info-circle-fill me-3 fs-4"></i>
                                <div>
                                    <strong>¿Dónde recogerás el vehículo?</strong> <br />
                                    Para mostrarte las fechas disponibles para este modelo, por favor selecciona la sucursal de recogida.
                                </div>
                            </div>
                        )}

                        {rangeError && (
                            <div className="alert alert-danger d-flex align-items-center animate-in fade-in" role="alert">
                                <i className="bi bi-exclamation-triangle-fill me-3 fs-4"></i>
                                <div>{rangeError}</div>
                            </div>
                        )}

                        {/* MANEJO DE ERROR DE CONEXIÓN CON REINTENTO */}
                        {availabilityError && (
                            <div className="alert alert-warning d-flex flex-column flex-sm-row justify-content-between align-items-center animate-in fade-in shadow-sm" role="alert" style={{ borderLeft: '5px solid #1f88e6' }}>
                                <div className="d-flex align-items-center mb-3 mb-sm-0">
                                    <i className="bi bi-wifi-off me-3 fs-3 text-warning"></i>
                                    <span className="fw-semibold text-dark">{availabilityError}</span>
                                </div>
                                <button
                                    onClick={fetchBlockedDates}
                                    className="btn btn-outline-dark fw-bold rounded-pill d-flex align-items-center gap-2"
                                    disabled={loadingAvailability}
                                >
                                    <i className="bi bi-arrow-clockwise"></i> Reintentar
                                </button>
                            </div>
                        )}

                        <BookingSearchForm
                            key={formKey}
                            citiesWithBranches={allCitiesWithBranches}
                            isCompact={false}
                            isDetailView={true}
                            blockedDates={blockedDates}
                            onRangeError={setRangeError}
                        />

                        {loadingAvailability && (
                            <div className="text-center mt-4">
                                <div className="spinner-border spinner-border-sm" role="status"></div>
                                <span className="ms-2 product-detail-text-muted fw-bold">Consultando inventario en la sucursal seleccionada...</span>
                            </div>
                        )}
                    </div>

                    {/* Footer de Reserva */}
                    <div className="d-flex flex-column flex-md-row justify-content-between align-items-center mt-5 pt-4 pb-3 border-top px-md-4 gap-4">
                        {/* Contenedor del Desglose Financiero */}
                        <div className="flex-grow-1 w-100">
                            {isLoadingPricing || !pricingDetails ? (
                                <div className="spinner-border" role="status"></div>
                            ) : (
                                <div className="d-flex flex-column">
                                    {!pricingDetails.hasDates ? (
                                        // VISTA POR DEFECTO (Sin fechas)
                                        <div className="text-center text-lg-start">
                                            <p className="text-uppercase mb-1 fw-semibold tracking-wider product-detail-text-muted" style={{ fontSize: '0.75rem' }}>
                                                Tarifa Base Sugerida
                                            </p>
                                            <div className="d-flex align-items-baseline justify-content-center justify-content-lg-start gap-2">
                                                <span className="fs-2 fw-extrabold" style={{ color: '#2e2e84', letterSpacing: '-0.5px' }}>
                                                    {formatCurrency(singleProduct.price)}
                                                </span>
                                                <span className="product-detail-text-muted fs-5 fw-medium">/ día</span>
                                            </div>
                                            <small className="product-detail-text-muted d-block mt-1">Selecciona fechas para ver el costo total con impuestos.</small>
                                        </div>
                                    ) : (
                                        // VISTA CON FECHAS (Desglose real)
                                        <div className="bg-white p-3 rounded-3 shadow-sm border">
                                            <h6 className="fw-bold text-uppercase mb-3" style={{ color: '#2e2e84', fontSize: '0.8rem' }}>
                                                Resumen de {pricingDetails.rentalDays} día(s) de alquiler
                                            </h6>
                                            <div className="d-flex justify-content-between mb-1 small product-detail-text-muted">
                                                <span>Tarifa Base:</span>
                                                <span>{formatCurrency(pricingDetails.baseCost)}</span>
                                            </div>
                                            <div className="d-flex justify-content-between mb-1 small product-detail-text-muted">
                                                <span>Seguro (Básico):</span>
                                                <span>{formatCurrency(pricingDetails.insuranceCost)}</span>
                                            </div>
                                            {pricingDetails.transferFeeAmount > 0 && (
                                                <div className="d-flex justify-content-between mb-1 small text-danger">
                                                    <span>Tarifa por devolución en otra sucursal:</span>
                                                    <span>+ {formatCurrency(pricingDetails.transferFeeAmount)}</span>
                                                </div>
                                            )}
                                            <div className="d-flex justify-content-between mb-2 pb-2 border-bottom small product-detail-text-muted">
                                                <span>Impuestos (IVA):</span>
                                                <span>{formatCurrency(pricingDetails.taxAmount)}</span>
                                            </div>
                                            <div className="d-flex justify-content-between align-items-center">
                                                <span className="fw-bold" style={{ color: '#2e2e84' }}>Total Estimado:</span>
                                                <span className="fs-4 fw-extrabold" style={{ color: '#1f88e6' }}>
                                                    {formatCurrency(pricingDetails.total)}
                                                </span>
                                            </div>
                                        </div>
                                    )}
                                </div>
                            )}
                        </div>
                        <div className="flex-shrink-0 w-100 text-lg-end" style={{ maxWidth: '300px' }}>
                            <button
                                onClick={handleReserveSubmit}
                                className="btn reservation-btn rounded-pill shadow-sm w-100 px-4 py-3 fw-bold"
                                disabled={loadingAvailability || availabilityError}
                            >
                                CONTINUAR RESERVA <i className="bi bi-arrow-right ms-2"></i>
                            </button>
                            <div className="text-center mt-2">
                                <small className="product-detail-text-muted" style={{ fontSize: '0.7rem' }}>Añade extras y mejora la cobertura en el siguiente paso.</small>
                            </div>
                        </div>
                    </div>
                    {/* === SECCIÓN DE RESEÑAS === */}
                    <ProductReviewsComponent
                        productId={singleProduct.id}
                        onReviewAdded={handleReviewAdded}
                    />
                </section>
            </main>
            {modalOpen && <ImageGalleryModal product={singleProduct} onClose={() => setModalOpen(false)} />}
            {shareModalOpen && <ShareModalComponent product={singleProduct} onClose={() => setShareModalOpen(false)} />}
        </>
    );
}
