import React, { useState, useEffect, useMemo } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useCurrencyFormatter } from '../hooks/useCurrencyFormatter';
import { API_CONFIG } from '../config/apiConfig';
import { extractErrorMessage } from '../utils/extractErrorMessage';
import { handleUnauthorizedError } from '../utils/handleUnauthorizedError';
import { useMessageModal } from '../context/MessageModalContext';
import { UserContext } from '../context/UserContext';
import { useContext } from 'react';
import { useDebounce } from '../hooks/useDebounce';
import { formatDateTime, formatPeriod } from '../utils/dateHelpers'
import { PaginationControlsComponent } from './PaginationControlsComponent';
import { ToastNotification } from './ToastNotification';
import { ReviewModalComponent } from './ReviewModalComponent';

const getStatusBadgeClass = (status) => {
    switch (status) {
        case 'CONFIRMED': return 'bg-primary';
        case 'IN_PROGRESS': return 'bg-warning text-dark';
        case 'COMPLETED': return 'bg-success';
        case 'CANCELLED': return 'bg-danger';
        default: return 'bg-secondary';
    }
};

export const MyReservationsComponent = ({ itemsPerPage, type }) => {
    const { token, logout } = useContext(UserContext);
    const { setModalMessage } = useMessageModal();
    const { formatCurrency } = useCurrencyFormatter();
    const [activeTab, setActiveTab] = useState('upcoming');
    const [searchTerm, setSearchTerm] = useState('');
    const [reservations, setReservations] = useState([]);

    const [searchPage, setSearchPage] = useState(1);
    const [showToast, setShowToast] = useState(false);
    const [toastType, setToastType] = useState("success");
    const [loading, setLoading] = useState(false);
    const [toastMessage, setToastMessage] = useState(null);

    const navigate = useNavigate();

    const [reviewModalConfig, setReviewModalConfig] = useState({
        isOpen: false,
        productId: null,
        reservationId: null // Guardamos el ID de la reserva para actualizar la vista
    });

    /* useEffect con AbortController para llamadas seguras a la API (Evitar Condiciones de Carrera en el Fetch)
    // al cambiar rápidamente entre las pestañas "Próximas" e "Historial",
    // las respuestas de la API podrían llegar en un orden diferente al que se solicitaron. Si la red es lenta,
    //  la respuesta de una pestaña anterior podría sobrescribir los datos de la pestaña actual. 
    */
    useEffect(() => {

        const controller = new AbortController();
        const signal = controller.signal;

        const fetchReservations = async () => {
            setLoading(true);
            setToastMessage(null);

            try {
                const URL = `${API_CONFIG.RESERVATIONS}/me?type=${activeTab}`;

                const response = await fetch(URL, {
                    method: 'GET',
                    headers: { 'Authorization': `Bearer ${token}` },
                    signal // Pasamos el signal de cancelación
                });


                if (handleUnauthorizedError(response, navigate, logout, setModalMessage)) return;

                if (response.ok) {
                    const data = await response.json();
                    // Aseguramos que data sea un array
                    setReservations(Array.isArray(data) ? data : []);
                    setSearchPage(1); // Reiniciar paginación al cambiar de pestaña
                    setSearchTerm(''); // Limpiar búsqueda al cambiar de pestaña                   
                } else {
                    const msg = await extractErrorMessage(response);
                    throw new Error(msg);
                }

            } catch (error) {
                // Ignorar el error si fue causado por el AbortController
                if (error.name === 'AbortError') return;

                console.error("Error en carga de datos: ", error);
                const message = error.message.includes("Failed to fetch") ? "No se pudo establecer conexión con el servidor." : error.message;
                setToastMessage(message || "Ocurrió un error inesperado.");
                setToastType("failure");
                setShowToast(true);
                setReservations([]);
            } finally {
                // Solo quitar el loading si la petición no fue abortada
                if (!signal.aborted) {
                    setLoading(false);
                }
            }
        };

        if (token) {
            fetchReservations();
        }

        // Cleanup function: cancela el fetch si el componente se desmonta o cambia el tab
        return () => {
            controller.abort();
        };

    }, [activeTab, token, navigate, logout, setModalMessage]);

    //Debounce para el input de búsqueda (espera 500ms antes de filtrar)
    const debouncedSearch = useDebounce(searchTerm, 500);

    // Paginación al buscar
    useEffect(() => {
        setSearchPage(1);
    }, [debouncedSearch]);

    // Lógica de filtrado y búsqueda
    const filteredReservations = useMemo(() => {
        if (!debouncedSearch) return reservations;

        const term = debouncedSearch.toLowerCase();
        return reservations.filter(res =>
            res.id.toLowerCase().includes(term) ||
            (res.productNameSnapshot || '').toLowerCase().includes(term) ||
            (res.status || '').toLowerCase().includes(term)
        );
    }, [reservations, debouncedSearch]);

    // Lógica de paginación
    const totalPages = Math.ceil(filteredReservations.length / itemsPerPage);
    const paginatedReservations = filteredReservations.slice(
        (searchPage - 1) * itemsPerPage, searchPage * itemsPerPage
    );

    // Renderizado de la celda de acciones para evitar repetición
    const renderActions = (res) => {
        if (activeTab === 'upcoming') {
            return (
                <button className="btn btn-outline-danger btn-sm rounded-pill px-3 fw-semibold w-100">
                    <i className="bi bi-x-circle me-1"></i> Cancelar
                </button>
            );
        }
        // Consumimos el atributo booleano calculado eficientemente en el backend
        const hasReviewed = res.hasReviewed;

        return (
            <button
                className={`btn btn-sm w-100 rounded-pill px-3 fw-semibold ${hasReviewed ? 'btn-light my-reservation-block-text-muted border' : 'my-reservation-block-btn-detail'}`}
                disabled={hasReviewed}
                onClick={() => setReviewModalConfig({ isOpen: true, productId: res.productId, reservationId: res.id })}
            >
                <i className={`bi ${hasReviewed ? 'bi-star-fill text-warning' : 'bi-star'} me-1`}></i>
                {hasReviewed ? 'Reseñado' : 'Calificar'}
            </button>
        );
    };

    // Marca la reserva como reseñada en el frontend
    const handleReviewSuccess = () => {
        setReservations(prevReservations =>
            prevReservations.map(res =>
                res.id === reviewModalConfig.reservationId
                    ? { ...res, hasReviewed: true }
                    : res
            )
        );
    };

    return (
        <div className="w-100">
            {/* Cabecera */}
            <div className="border-bottom pb-2 mb-4 d-flex flex-column flex-md-row justify-content-between align-items-md-end gap-3">
                <div>
                    <h3 className="h4 fw-bold my-reservation-block-title-color mb-0">
                        <i className="bi bi-car-front-fill me-2 text-success"></i> Mis Reservas
                    </h3>
                    <p className="my-reservation-block-text-muted small mb-0">Visualiza el estado de tus alquileres activos o revisa tu historial.</p>
                </div>
                {/* Input de Búsqueda */}
                <div className="position-relative" style={{ minWidth: '250px' }}>
                    <label htmlFor="search-input" className="visually-hidden">
                        Buscar reserva o auto
                    </label>
                    <i className="bi bi-search position-absolute top-50 start-0 translate-middle-y ms-3 my-reservation-block-text-muted"></i>
                    <input
                        id='search-input'
                        type="text"
                        className="form-control rounded-pill ps-5 bg-light"
                        placeholder="Buscar reserva, auto..."
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                    />
                </div>
            </div>

            {/* Selector de pestañas */}
            <ul className="nav nav-tabs border-0 mb-4 bg-light p-1 rounded-3 d-flex flex-nowrap">
                <li className="nav-item flex-fill text-center">
                    <button
                        className={`nav-link w-100 border-0 rounded-3 py-2 fw-semibold small text-nowrap ${activeTab === 'upcoming' ? 'bg-white shadow-sm my-reservation-block-text-primary' : 'my-reservation-block-text-muted bg-transparent'}`}
                        onClick={() => setActiveTab('upcoming')}
                    >
                        Próximas y Activas
                    </button>
                </li>
                <li className="nav-item flex-fill text-center">
                    <button
                        className={`nav-link w-100 border-0 rounded-3 py-2 fw-semibold small text-nowrap ${activeTab === 'past' ? 'bg-white shadow-sm my-reservation-block-text-primary' : 'my-reservation-block-text-muted bg-transparent'}`}
                        onClick={() => setActiveTab('past')}
                    >
                        Historial de Viajes
                    </button>
                </li>
            </ul>
            {/* Manejo de Estados  */}
            {loading ? (
                <div className="text-center py-5">
                    <div className="spinner-border text-primary" role="status">
                        <span className="visually-hidden">Cargando...</span>
                    </div>
                    <p className="my-reservation-block-text-muted mt-2 small">Cargando tus reservas...</p>
                </div>
            ) : reservations.length === 0 ? (
                <div className="text-center py-5 bg-light rounded-3 border">
                    <i className="bi bi-calendar-x my-reservation-block-text-muted display-4"></i>
                    <h5 className="mt-3 fw-bold text-dark">No tienes reservas en esta sección</h5>
                    <p className="my-reservation-block-text-muted small">¿Planeas un nuevo viaje? Encuentra el vehículo ideal para tu aventura.</p>
                    <Link to="/" className="btn my-reservation-block-btn-detail btn-sm rounded-pill px-4 mt-2 shadow-sm">
                        Buscar Vehículos
                    </Link>
                </div>
            ) :
                filteredReservations.length === 0 ? (
                    <div className="text-center py-5 bg-light rounded-3 border">
                        <i className="bi bi-search my-reservation-block-text-muted display-4"></i>
                        <h5 className="mt-3 fw-bold text-dark">No se encontraron resultados</h5>
                        <p className="my-reservation-block-text-muted small">No hay coincidencias para "{searchTerm}".</p>
                    </div>
                ) : (
                    <>
                        {/* VISTA MÓVIL (Tarjetas) - Visible solo en pantallas pequeñas */}
                        <div className="d-block d-md-none">
                            {paginatedReservations.map((res) => {
                                const pickup = formatDateTime(res.pickupDatetime);
                                const dropoff = formatDateTime(res.returnDatetime);
                                return (
                                    <div key={res.id} className="card shadow-sm border-0 mb-3 rounded-3">
                                        <div className="card-header bg-white border-bottom-0 d-flex justify-content-between align-items-center pt-3 pb-0">
                                            <span className="fw-bold text-dark small text-truncate" style={{ maxWidth: '180px' }}>{res.id}</span>
                                            <span className={`badge rounded-pill ${getStatusBadgeClass(res.reservationStatus)} fw-normal`}>
                                                {res.reservationStatus}
                                            </span>
                                        </div>
                                        <div className="card-body">
                                            <h6 className="fw-bold mb-0">{res.productNameSnapshot || 'Vehículo asignado'}</h6>
                                            <p className="my-reservation-block-text-muted small mb-3">Placa: {res.vehicleLicensePlateSnapshot || 'En asignación'}</p>

                                            {activeTab === 'upcoming' ? (
                                                <div className="row g-2 small mb-3">
                                                    <div className="col-6 border-end">
                                                        <div className="my-reservation-block-text-muted fw-semibold mb-1">Retiro</div>
                                                        <div className="fw-bold text-dark">{pickup.date}</div>
                                                        <div><i className="bi bi-clock my-reservation-block-text-muted"></i> {pickup.time}</div>
                                                        <div className="text-truncate my-reservation-block-text-muted" title={res.pickupBranchNameSnapshot}>
                                                            <i className="bi bi-geo-alt"></i> {res.pickupBranchNameSnapshot}
                                                        </div>
                                                    </div>
                                                    <div className="col-6 ps-3">
                                                        <div className="my-reservation-block-text-muted fw-semibold mb-1">Devolución</div>
                                                        <div className="fw-bold text-dark">{dropoff.date}</div>
                                                        <div><i className="bi bi-clock text-muted"></i> {dropoff.time}</div>
                                                        <div className="text-truncate text-muted" title={res.returnBranchNameSnapshot}>
                                                            <i className="bi bi-geo-alt"></i> {res.returnBranchNameSnapshot}
                                                        </div>
                                                    </div>
                                                </div>
                                            ) : (
                                                <div className="d-flex justify-content-between align-items-center bg-light p-2 rounded mb-3 small">
                                                    <div>
                                                        <i className="bi bi-calendar-range my-reservation-block-text-muted me-1"></i>
                                                        {formatPeriod(res.pickupDatetime, res.returnDatetime)}
                                                    </div>
                                                    <div className="fw-bold text-dark">
                                                        {formatCurrency(res.totalPrice)}
                                                    </div>
                                                </div>
                                            )}
                                            {renderActions(res)}
                                        </div>
                                    </div>
                                );
                            })}
                        </div>

                        {/* VISTA ESCRITORIO (Tabla) - Visible solo en md hacia arriba */}
                        <div className="d-none d-md-block table-responsive">
                            <table className="table table-hover align-middle bg-white border rounded-3 overflow-hidden shadow-sm">
                                <thead className="table-light">
                                    <tr>
                                        <th className="small text-muted fw-semibold">Reserva</th>
                                        <th className="small text-muted fw-semibold">Vehículo</th>
                                        {activeTab === 'upcoming' ? (
                                            <>
                                                <th className="small text-muted fw-semibold">Retiro</th>
                                                <th className="small text-muted fw-semibold">Devolución</th>
                                            </>
                                        ) : (
                                            <>
                                                <th className="small text-muted fw-semibold">Período</th>
                                                <th className="small text-muted fw-semibold">Costo Total</th>
                                            </>
                                        )}
                                        <th className="small text-muted fw-semibold text-center">Acciones</th>
                                    </tr>
                                </thead>
                                <tbody className="border-top-0">
                                    {paginatedReservations.map((res) => {
                                        const pickup = formatDateTime(res.pickupDatetime);
                                        const dropoff = formatDateTime(res.returnDatetime);
                                        return (
                                            <tr key={res.id}>
                                                <td style={{ width: '160px' }}>
                                                    <div className="fw-bold text-dark text-truncate" style={{ maxWidth: '140px' }} title={res.id}>
                                                        {res.id}
                                                    </div>
                                                    <span className={`badge rounded-pill ${getStatusBadgeClass(res.reservationStatus)} fw-normal`}>
                                                        {res.reservationStatus}
                                                    </span>
                                                </td>
                                                <td>
                                                    <div className="fw-semibold">{res.productNameSnapshot || 'Vehículo'}</div>
                                                    <small className="my-reservation-block-text-muted">Placa: {res.vehicleLicensePlateSnapshot || 'En asignación'}</small>
                                                </td>
                                                {activeTab === 'upcoming' ? (
                                                    <>
                                                        <td>
                                                            <div className="small fw-semibold">{pickup.date}</div>
                                                            <div className="small my-reservation-block-text-muted"><i className="bi bi-clock"></i> {pickup.time}</div>
                                                            <div className="small my-reservation-block-text-muted text-truncate" style={{ maxWidth: '150px' }} title={res.pickupBranchNameSnapshot}>
                                                                <i className="bi bi-geo-alt"></i> {res.pickupBranchNameSnapshot}
                                                            </div>
                                                        </td>
                                                        <td>
                                                            <div className="small fw-semibold">{dropoff.date}</div>
                                                            <div className="small my-reservation-block-text-muted"><i className="bi bi-clock"></i> {dropoff.time}</div>
                                                            <div className="small my-reservation-block-text-muted text-truncate" style={{ maxWidth: '150px' }} title={res.returnBranchNameSnapshot}>
                                                                <i className="bi bi-geo-alt"></i> {res.returnBranchNameSnapshot}
                                                            </div>
                                                        </td>
                                                    </>
                                                ) : (
                                                    <>
                                                        <td>
                                                            <div className="small"><i className="bi bi-calendar-range my-reservation-block-text-muted me-1"></i>
                                                                {formatPeriod(res.pickupDatetime, res.returnDatetime)}
                                                            </div>
                                                        </td>
                                                        <td>
                                                            <div className="fw-semibold text-dark">
                                                                {formatCurrency(res.totalPrice)}
                                                            </div>
                                                        </td>
                                                    </>
                                                )}
                                                <td className="text-center" style={{ width: '140px' }}>
                                                    {renderActions(res)}
                                                </td>
                                            </tr>
                                        );
                                    })}
                                </tbody>
                            </table>
                        </div>

                        {/* Controles de Paginación */}
                        <PaginationControlsComponent
                            currentPage={searchPage}
                            totalPages={totalPages}
                            goToPage={setSearchPage}
                            type={type}
                        />
                    </>
                )}

            {/* Renderizado del Modal */}
            {reviewModalConfig.isOpen && (
                <ReviewModalComponent
                    productId={reviewModalConfig.productId}
                    onClose={() => setReviewModalConfig({ isOpen: false, productId: null, reservationId: null })}
                    onSuccess={handleReviewSuccess}
                />
            )}

            {/* Componente Toast */}
            <ToastNotification
                show={showToast}
                message={toastMessage}
                type={toastType}
                onClose={() => setShowToast(false)}
            />
        </div>
    );
};