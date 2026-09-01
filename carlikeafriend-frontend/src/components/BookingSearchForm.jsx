import { useState, useEffect, useRef } from 'react';
import { useForm, Controller } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';
import { isSameDay, addHours, addMinutes, startOfHour, isBefore, addDays } from 'date-fns';
import { useNavigate } from 'react-router-dom';
import { useBooking } from '../context/BookingContext';
import { searchSchema } from '../utils/validationSchema';
import { isTimeOptionValidForPickup, isTimeOptionValidForReturn } from '../utils/dateHelpers';
import "../styles/SearchBlockStyle.css"

export const BookingSearchForm = ({
    citiesWithBranches = [],
    onSearchSubmit,
    isCompact = false,
    isDetailView = false,
    blockedDates = [],
    onRangeError = null
}) => {
    const { bookingData, updateBookingData } = useBooking();
    const navigate = useNavigate();

    // Estados Locales UI (Autocompletado Recogida)
    const [searchTerm, setSearchTerm] = useState('');
    const [showSuggestions, setShowSuggestions] = useState(false);
    const wrapperRef = useRef(null);

    // Estados Locales UI (Autocompletado Devolución)
    const [returnSearchTerm, setReturnSearchTerm] = useState('');
    const [showReturnSuggestions, setShowReturnSuggestions] = useState(false);
    const returnWrapperRef = useRef(null);

    const { control, handleSubmit, setValue, watch, formState: { errors } } = useForm({
        resolver: yupResolver(searchSchema),
        defaultValues: bookingData, // Se alimenta del Contexto Global
        mode: 'onChange'
    });

    // Observadores en tiempo real
    const currentDateRange = watch("dateRange");
    const currentPickupTime = watch("pickupTime");
    const currentReturnTime = watch("returnTime");
    const isDifferentBranch = watch("differentReturnBranch");

    // Vigilar las sucursales seleccionadas para excluirlas mutuamente
    const currentPickupBranch = watch("pickupBranch");
    const currentReturnBranch = watch("returnBranch");

    // Observar todo el formulario para sincronizar con el Contexto en Detail View
    const allValues = watch();

    // Inicializar el buscador con el texto del Contexto si ya se había buscado algo
    useEffect(() => {
        if (bookingData.pickupBranch) {
            setSearchTerm(`${bookingData.pickupBranch.name} - ${bookingData.pickupBranch.cityName}`);
        }
        if (bookingData.returnBranch) {
            setReturnSearchTerm(`${bookingData.returnBranch.name} - ${bookingData.returnBranch.cityName}`);
        }
    }, [bookingData.pickupBranch, bookingData.returnBranch]);

    // Al desactivar el switch, limpiamos el estado de retorno inmediatamente
    useEffect(() => {
        if (!isDifferentBranch && currentReturnBranch !== null) {
            setValue('returnBranch', null, { shouldValidate: true });
            setReturnSearchTerm('');
        }
    }, [isDifferentBranch, currentReturnBranch, setValue]);


    // --- LÓGICA DE FECHAS BLOQUEADAS ---
    // Aseguramos que sea un arreglo. Si por error llega undefined o null, usamos []
    const safeBlockedDates = Array.isArray(blockedDates) ? blockedDates : [];

    // Aseguramos que la fecha esté a las 00:00:00 para la comparación
    const excludedDatesObjects = safeBlockedDates.map(dateStr => {
        if (!dateStr) return null;
        return new Date(`${dateStr}T00:00:00`);
    }).filter(Boolean);

    const isDateBlocked = (date) => {
        return excludedDatesObjects.some(blocked => isSameDay(blocked, date));
    };

    const hasBlockedDatesInRange = (start, end) => {
        if (!start || !end) return false;
        let current = new Date(start);
        current.setHours(0, 0, 0, 0);
        const endD = new Date(end);
        endD.setHours(0, 0, 0, 0);
        while (current <= endD) {
            if (isDateBlocked(current)) return true;
            current.setDate(current.getDate() + 1);
        }
        return false;
    };

    // --- EFECTO: SINCRONIZACIÓN EN VIVO (Para Detail View) ---
    useEffect(() => {
        if (isDetailView) {
            // Sincroniza directamente el contexto cuando el usuario interactúa en la ficha
            updateBookingData({
                pickupBranch: allValues.pickupBranch,
                returnBranch: allValues.returnBranch,
                differentReturnBranch: allValues.differentReturnBranch,
                dateRange: allValues.dateRange,
                pickupTime: allValues.pickupTime,
                returnTime: allValues.returnTime
            });
        }
    }, [
        isDetailView,
        allValues.pickupBranch,
        allValues.returnBranch,
        allValues.differentReturnBranch,
        allValues.dateRange,
        allValues.pickupTime,
        allValues.returnTime
    ]);

    // --- FILTROS DE HORARIO (Consumen los Timestamps Absolutos) ---
    const filterPickupTime = (time) => {
        return isTimeOptionValidForPickup(currentDateRange?.[0], time);
    };

    const filterReturnTime = (time) => {
        return isTimeOptionValidForReturn(
            currentDateRange?.[0],
            currentPickupTime,
            currentDateRange?.[1],
            time
        );
    };

    // --- EFECTO: AUTO-CORRECCIÓN DE FECHAS/HORAS ---
    useEffect(() => {
        if (!currentDateRange || !currentDateRange[0]) return;
        const pickupDate = currentDateRange[0];
        const returnDate = currentDateRange[1];
        const now = new Date();
        const nowWithBuffer = new Date(now.getTime() + 10 * 60000); // 10 mins buffer

        // 1. Corrección de Recogida
        if (currentPickupTime && isSameDay(pickupDate, now)) {
            const selectedPickupDateTime = new Date(pickupDate);
            selectedPickupDateTime.setHours(currentPickupTime.getHours(), currentPickupTime.getMinutes(), 0, 0);

            // Si la hora elegida está en el pasado o en el buffer de red (peligro)
            if (isBefore(selectedPickupDateTime, nowWithBuffer)) {
                // IMPORTANTE: Calculamos la próxima franja desde el buffer, no desde el now puro
                const nextBlock = new Date(nowWithBuffer);
                const minutes = nextBlock.getMinutes();
                const nextValidTime = minutes < 30 ? addMinutes(startOfHour(nextBlock), 30) : addHours(startOfHour(nextBlock), 1);
                setValue('pickupTime', nextValidTime, { shouldValidate: true });
            }
        }

        // 2. Corrección de Entrega
        // Solo auto-corregimos si el usuario insiste en devolver el MISMO DÍA. 
        // Si seleccionó otro día, Yup y los selectores lo guiarán correctamente.
        if (currentPickupTime && currentReturnTime && returnDate && isSameDay(pickupDate, returnDate)) {
            const pickupAbsolute = new Date(pickupDate);
            pickupAbsolute.setHours(currentPickupTime.getHours(), currentPickupTime.getMinutes(), 0, 0);

            const currentReturnAbsolute = new Date(returnDate);
            currentReturnAbsolute.setHours(currentReturnTime.getHours(), currentReturnTime.getMinutes(), 0, 0);

            const minValidReturnTime = new Date(pickupAbsolute.getTime() + (60 * 60 * 1000));

            // Si la devolución seleccionada es matemáticamente menor a la permitida y sigue siendo hoy
            if (currentReturnAbsolute.getTime() < minValidReturnTime.getTime() && isSameDay(pickupDate, minValidReturnTime)) {
                setValue('returnTime', minValidReturnTime, { shouldValidate: true });
            }
        }
    }, [currentDateRange, currentPickupTime, currentReturnTime, setValue]);

    // --- CERRAR SUGERENCIAS AL HACER CLICK AFUERA ---
    useEffect(() => {
        function handleClickOutside(event) {
            if (wrapperRef.current && !wrapperRef.current.contains(event.target)) {
                setShowSuggestions(false);
            }
            if (returnWrapperRef.current && !returnWrapperRef.current.contains(event.target)) {
                setShowReturnSuggestions(false);
            }
        }
        document.addEventListener("mousedown", handleClickOutside);
        return () => document.removeEventListener("mousedown", handleClickOutside);
    }, []);

    // --- MANEJADORES DEL INPUT DE CIUDAD ---
    const handleSearchTermChange = (e) => {
        setSearchTerm(e.target.value);
        setValue('pickupBranch', null, { shouldValidate: true });
        setShowSuggestions(e.target.value.trim().length > 0);
    };

    const handleSelectBranch = (city, branch) => {
        const branchData = { id: branch.id, name: branch.name, cityName: city.name };
        setValue('pickupBranch', branchData, { shouldValidate: true });
        setSearchTerm(`${branch.name} - ${city.name}`);
        setShowSuggestions(false);
    };

    const clearCitySearch = () => {
        setSearchTerm('');
        setValue('pickupBranch', null, { shouldValidate: true });
        setShowSuggestions(true);
    };

    // Lógica de filtrado de RECOGIDA: 
    // Solo excluye si el switch está activo y hay una sucursal de retorno seleccionada
    const filteredCities = citiesWithBranches
        .map(city => ({
            ...city,
            branches: city.branches.filter(b => {
                if (isDifferentBranch && currentReturnBranch) {
                    return b.id !== currentReturnBranch.id;
                }
                return true; // Si el switch está apagado, no bloqueamos nada
            })
        }))
        .filter(city =>
            city.branches.length > 0 && // Ocultar ciudades que se quedaron sin sucursales
            (city.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
                city.branches.some(b => b.name.toLowerCase().includes(searchTerm.toLowerCase())))
        );

    // --- MANEJADORES DEL INPUT DE CIUDAD (DEVOLUCIÓN) ---
    const handleReturnSearchTermChange = (e) => {
        setReturnSearchTerm(e.target.value);
        setValue('returnBranch', null, { shouldValidate: true });
        setShowReturnSuggestions(e.target.value.trim().length > 0);
    };

    const handleSelectReturnBranch = (city, branch) => {
        const branchData = { id: branch.id, name: branch.name, cityName: city.name };
        setValue('returnBranch', branchData, { shouldValidate: true });
        setReturnSearchTerm(`${branch.name} - ${city.name}`);
        setShowReturnSuggestions(false);
    };

    const clearReturnCitySearch = () => {
        setReturnSearchTerm('');
        setValue('returnBranch', null, { shouldValidate: true });
        setShowReturnSuggestions(true);
    };

    // --- LÓGICA DE EXCLUSIÓN MUTUA PARA DEVOLUCIÓN ---
    const filteredReturnCities = citiesWithBranches
        .map(city => ({
            ...city,
            branches: city.branches.filter(b => b.id !== currentPickupBranch?.id)
        }))
        .filter(city =>
            city.branches.length > 0 && // Ocultar ciudades que se quedaron sin sucursales
            (city.name.toLowerCase().includes(returnSearchTerm.toLowerCase()) ||
                city.branches.some(b => b.name.toLowerCase().includes(returnSearchTerm.toLowerCase())))
        );

    // --- SUBMIT ---
    const onFormSubmit = (data) => {
        // 1. Guardar siempre en el estado global
        updateBookingData(data);

        // 2. Ejecutar la función pasada por el padre (si existe)
        if (onSearchSubmit) {
            onSearchSubmit(data);
        } else {
            // 3. Comportamiento por defecto: Ir a filtros
            navigate('/product-filter');
        }
    };

    // Clases dinámicas
    const containerClasses = isCompact || isDetailView
        ? "mt-3 w-100"
        : "card shadow border-0 p-4 p-md-5 search-block-container mb-5 mx-auto";

    return (
        <div className={containerClasses}>
            {!isCompact && !isDetailView && <h3 className="fw-bold mb-4 text-center text-md-start search-block-title-color">Busca tu Auto Ideal</h3>}

            <form className="row g-3 align-items-start" onSubmit={handleSubmit(onFormSubmit)}>
                {/* COLUMNA DE UBICACIONES */}
                <div className={`col-12 ${isDetailView ? 'col-lg-12 mb-2' : 'col-lg-4'}`}>
                    {/* INPUT UBICACIÓN RECOGIDA */}
                    <div className="position-relative" ref={wrapperRef}>
                        <label htmlFor="homeSearchInput" className="form-label fw-bold small search-block-title-color">¿Dónde quieres recogerlo?</label>
                        <div className="input-group input-group-lg search-input-group position-relative">
                            <span className="input-group-text search-icon-bg">
                                <i className="bi bi-geo-alt-fill search-block-text-primary"></i>
                            </span>
                            <input
                                id="homeSearchInput"
                                type="text"
                                className={`form-control search-input ${errors.pickupBranch ? 'is-invalid' : ''}`}
                                placeholder="Escribe tu ciudad o sucursal..."
                                value={searchTerm}
                                onChange={handleSearchTermChange}
                                onFocus={() => setShowSuggestions(true)}
                                autoComplete="off"
                            />
                            {searchTerm && (
                                <button
                                    type="button"
                                    className="btn bg-transparent position-absolute end-0 top-50 translate-middle-y z-3"
                                    onClick={clearCitySearch}
                                >
                                    <i className="bi bi-x-circle-fill text-secondary"></i>
                                </button>
                            )}
                        </div>
                        {errors.pickupBranch && <span className="text-danger small mt-1 d-block">{errors.pickupBranch.message}</span>}

                        {/* SUGERENCIAS DE AUTOCOMPLETADO RECOGIDA */}
                        {showSuggestions && (
                            <div className="position-absolute w-100 shadow-lg mt-1 bg-white rounded-3 overflow-hidden" style={{ zIndex: 1000, border: '1px solid #e0e0e0' }}>
                                {filteredCities.length > 0 ? (
                                    <ul className="list-unstyled m-0">
                                        {filteredCities.map((city) => (
                                            <li key={city.id} className="border-bottom">
                                                <div className="px-3 py-2 fw-bold bg-light" style={{ color: '#2E2E84', fontSize: '0.85rem' }}>
                                                    <i className="bi bi-pin-map-fill me-1"></i> CIUDAD: {city.name.toUpperCase()}
                                                </div>
                                                <ul className="list-unstyled m-0">
                                                    {city.branches.map(branch => (
                                                        <li
                                                            key={branch.id}
                                                            className="px-4 py-1 branch-hover d-flex align-items-center"
                                                            onClick={() => handleSelectBranch(city, branch)}
                                                            style={{ cursor: 'pointer' }}
                                                        >
                                                            <i className="bi bi-car-front me-2 search-block-text-muted"></i>
                                                            <div className="d-flex flex-column" style={{ fontSize: '0.85rem' }}>
                                                                <div className="mb-0">
                                                                    <span className="search-block-text-muted">Sucursal </span>
                                                                    <b style={{ color: '#2E2E84' }}>{branch.name}</b>
                                                                </div>
                                                                <small className="search-block-text-muted" style={{ opacity: 0.8 }}>
                                                                    {branch.address}
                                                                </small>
                                                            </div>
                                                        </li>
                                                    ))}
                                                </ul>
                                            </li>
                                        ))}
                                    </ul>
                                ) : (
                                    <div className="p-3 search-block-text-muted text-center small">No encontramos ciudades o sucursales.</div>
                                )}
                            </div>
                        )}
                    </div>

                    {/* CHECKBOX: DEVOLVER EN OTRA SUCURSAL */}
                    <div className="form-check form-switch mt-2">
                        <input className="form-check-input" type="checkbox" id="differentReturnBranch" {...control.register('differentReturnBranch')} />
                        <label className="form-check-label small search-block-title-color fw-medium" htmlFor="differentReturnBranch">
                            Devolver en otra sucursal
                        </label>
                    </div>

                    {/* INPUT UBICACIÓN DEVOLUCIÓN (Condicional) */}
                    {isDifferentBranch && (
                        <div className="position-relative mt-2" ref={returnWrapperRef}>
                            <label htmlFor="returnBranchInput" className="form-label fw-bold small search-block-title-color">¿Dónde quieres devolverlo?</label>
                            <div className="input-group input-group-lg search-input-group position-relative">
                                <span className="input-group-text search-icon-bg">
                                    <i className="bi bi-geo-alt-fill search-block-text-primary"></i>
                                </span>
                                <input
                                    id="returnBranchInput"
                                    type="text"
                                    className={`form-control search-input ${errors.returnBranch ? 'is-invalid' : ''}`}
                                    placeholder="Escribe tu ciudad o sucursal..."
                                    value={returnSearchTerm}
                                    onChange={handleReturnSearchTermChange}
                                    onFocus={() => setShowReturnSuggestions(true)}
                                    autoComplete="off"
                                />
                                {returnSearchTerm && (
                                    <button
                                        type="button"
                                        className="btn bg-transparent position-absolute end-0 top-50 translate-middle-y z-3"
                                        onClick={clearReturnCitySearch}
                                    >
                                        <i className="bi bi-x-circle-fill text-secondary"></i>
                                    </button>
                                )}
                            </div>
                            {errors.returnBranch && <span className="text-danger small mt-1 d-block">{errors.returnBranch.message}</span>}

                            {/* SUGERENCIAS DE AUTOCOMPLETADO DEVOLUCIÓN */}
                            {showReturnSuggestions && (
                                <div className="position-absolute w-100 shadow-lg mt-1 bg-white rounded-3 overflow-hidden" style={{ zIndex: 1000, border: '1px solid #e0e0e0' }}>
                                    {filteredReturnCities.length > 0 ? (
                                        <ul className="list-unstyled m-0">
                                            {filteredReturnCities.map((city) => (
                                                <li key={city.id} className="border-bottom">
                                                    <div className="px-3 py-2 fw-bold bg-light" style={{ color: '#2E2E84', fontSize: '0.85rem' }}>
                                                        <i className="bi bi-pin-map-fill me-1"></i> CIUDAD: {city.name.toUpperCase()}
                                                    </div>
                                                    <ul className="list-unstyled m-0">
                                                        {city.branches.map(branch => (
                                                            <li
                                                                key={branch.id}
                                                                className="px-4 py-1 branch-hover d-flex align-items-center"
                                                                onClick={() => handleSelectReturnBranch(city, branch)}
                                                                style={{ cursor: 'pointer' }}
                                                            >
                                                                <i className="bi bi-car-front me-2 search-block-text-muted"></i>
                                                                <div className="d-flex flex-column" style={{ fontSize: '0.85rem' }}>
                                                                    <div className="mb-0">
                                                                        <span className="search-block-text-muted">Sucursal </span>
                                                                        <b style={{ color: '#2E2E84' }}>{branch.name}</b>
                                                                    </div>
                                                                    <small className="search-block-text-muted" style={{ opacity: 0.8 }}>
                                                                        {branch.address}
                                                                    </small>
                                                                </div>
                                                            </li>
                                                        ))}
                                                    </ul>
                                                </li>
                                            ))}
                                        </ul>
                                    ) : (
                                        <div className="p-3 search-block-text-muted text-center small">No encontramos ciudades o sucursales.</div>
                                    )}
                                </div>
                            )}
                        </div>
                    )}
                </div>

                {/* CALENDARIO DOBLE Y HORAS */}
                <div className={`col-12 ${isDetailView ? 'col-lg-12' : 'col-lg-6'}`}>
                    <div className="row g-2">
                        {/* Fechas Rango */}
                        <div className="col-12 col-md-7">
                            <label htmlFor="startAndEndDates" className="form-label fw-bold small search-block-title-color">Fechas (Recogida - Devolución)</label>
                            <div className="input-group input-group-lg search-input-group w-100">
                                <span className="input-group-text search-icon-bg">
                                    <i className="bi bi-calendar-range search-block-text-primary"></i>
                                </span>
                                <Controller
                                    name="dateRange"
                                    control={control}
                                    render={({ field }) => (
                                        <DatePicker
                                            id="startAndEndDates"
                                            selectsRange={true}
                                            startDate={field.value[0]}
                                            endDate={field.value[1]}
                                            onChange={(update) => {
                                                const [start, end] = update;
                                                // Validación de intercepción para evitar saltar sobre días bloqueados
                                                if (start && end && hasBlockedDatesInRange(start, end)) {
                                                    if (onRangeError) onRangeError("El rango seleccionado contiene fechas no disponibles en esta sucursal.");
                                                    field.onChange([start, null]); // Resetea el end date 
                                                } else {
                                                    if (onRangeError) onRangeError(null); // Limpia el error 
                                                    field.onChange(update)
                                                }
                                            }}
                                            monthsShown={window.innerWidth > 768 ? 2 : 1}
                                            minDate={new Date()}
                                            // Límite visual en la UI de 30 días a partir de la selección inicial
                                            maxDate={field.value[0] ? addDays(field.value[0], 30) : null}
                                            excludeDates={excludedDatesObjects}
                                            dayClassName={(date) => isDateBlocked(date) ? "text-decoration-line-through text-danger opacity-50 bg-light" : undefined}
                                            placeholderText="Selecciona las fechas"
                                            className={`form-control search-input ${errors.dateRange ? 'is-invalid' : ''}`}
                                            wrapperClassName="w-100"
                                            dateFormat="dd/MM/yyyy"
                                            portalId="root"
                                        />
                                    )}
                                />
                            </div>
                            {errors.dateRange && <span className="text-danger small mt-1 d-block">{errors.dateRange.message}</span>}
                        </div>

                        {/* Horas */}
                        <div className="col-12 col-md-5">
                            <div className="d-flex gap-2">
                                <div className="w-50">
                                    <label htmlFor="pickupTimeInput" className="form-label fw-bold small search-block-title-color">Hora Rec.</label>
                                    <Controller
                                        name="pickupTime"
                                        control={control}
                                        render={({ field }) => (
                                            <DatePicker
                                                id="pickupTimeInput"
                                                selected={field.value}
                                                onChange={(date) => field.onChange(date)}
                                                showTimeSelect
                                                showTimeSelectOnly
                                                timeIntervals={30}
                                                timeCaption="Hora"
                                                dateFormat="HH:mm"
                                                filterTime={filterPickupTime}
                                                className="form-control form-control-lg search-input text-center"
                                            />
                                        )}
                                    />
                                    {errors.pickupTime && <span className="text-danger small mt-1 d-block" style={{ fontSize: '0.7rem' }}>{errors.pickupTime.message}</span>}
                                </div>
                                <div className="w-50">
                                    <label htmlFor="returnTimeInput" className="form-label fw-bold small search-block-title-color">Hora Dev.</label>
                                    <Controller
                                        name="returnTime"
                                        control={control}
                                        render={({ field }) => (
                                            <DatePicker
                                                id="returnTimeInput"
                                                selected={field.value}
                                                onChange={(date) => field.onChange(date)}
                                                showTimeSelect
                                                showTimeSelectOnly
                                                timeIntervals={30}
                                                timeCaption="Hora"
                                                dateFormat="HH:mm"
                                                filterTime={filterReturnTime}
                                                className="form-control form-control-lg search-input text-center"
                                            />
                                        )}
                                    />
                                    {errors.returnTime && <span className="text-danger small mt-1 d-block" style={{ fontSize: '0.7rem' }}>{errors.returnTime.message}</span>}
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                {/* BOTÓN BUSCAR (Oculto en DetailView porque la reserva finaliza abajo en el footer) */}
                {!isDetailView && (
                    <div className="col-12 col-lg-2 d-flex align-items-end mt-4 mt-lg-0">
                        <button type="submit" className="btn btn-lg fw-bold search-btn search-btn-hover w-100 h-100" style={{ minHeight: '50px' }}>
                            Buscar
                        </button>
                    </div>
                )}
            </form>
        </div>
    );
};