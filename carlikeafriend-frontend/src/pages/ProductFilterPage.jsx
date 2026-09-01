import { useEffect, useMemo, useState } from 'react';
import { ProductCardComponent } from '../components/ProductCardComponent';
import { useProductFilter } from '../hooks/useProductFilter';
import { useCurrencyFormatter } from '../hooks/useCurrencyFormatter';
import { BookingSearchForm } from '../components/BookingSearchForm';
import { EXCLUDED_FILTER_FEATURES } from '../utils/featureConstants';

// COMPONENTES AUXILIARES EXTRAÍDOS PARA EVITAR RE-RENDERIZADOS (PÉRDIDA DE FOCO AL ARRASTRAR)
const FilterItems = ({ label, value, count, isChecked, onChange }) => (
    <li className="list-group-item d-flex justify-content-between align-items-center bg-transparent border-0 px-3 py-2">
        <div>
            <input
                type="checkbox"
                id={`filter-${value}-${label}`}
                value={value}
                className="form-check-input me-2 custom-checkbox"
                checked={isChecked}
                onChange={(e) => onChange(value, e.target.checked)}
                disabled={count === 0 && !isChecked}
            />
            <label className={`form-check-label ${count === 0 && !isChecked ? 'text-muted' : ''}`} htmlFor={`filter-${value}-${label}`}>
                {label}
            </label>
        </div>
        <span className={`badge rounded-pill ${count > 0 ? 'badge-color' : 'bg-secondary'}`}>{count}</span>
    </li>
);

const FilterAmount = ({ absoluteMin, absoluteMax, minPrice, maxPrice, rangeLeft, rangeProgress, onChangeMin, onChangeMax }) => (

    <div className="custom-slider px-2"> {/* Contenedor del Slider */}
        {/* 1. Pista Inactiva (Gris) */}
        <div className="range-slider-track"></div>

        {/* 2. Pista Activa (Azul - Progreso) */}
        {/* El width y left se calculan dinámicamente con React para que el color azul sea el rango seleccionado */}
        <div
            className="range-slider-range"
            style={{ left: `${rangeLeft}%`, width: `${rangeProgress}%` }}
        />
        {/* Input 1: Controla el Mínimo */}
        <input
            type="range"
            className="range-slider-input"
            min={absoluteMin}
            max={absoluteMax}
            step={10000}
            value={minPrice}
            onChange={(e) => onChangeMin(e.target.value)}
            // Lógica para superponer el knob arrastrado
            style={{ zIndex: minPrice < maxPrice ? 4 : 5 }}
        />

        {/* Input para el máximo */}
        <input
            type="range"
            className="range-slider-input"
            min={absoluteMin}
            max={absoluteMax}
            step={10000}
            value={maxPrice}
            // Aseguramos que el máximo no cruce el mínimo
            onChange={(e) => onChangeMax(e.target.value)}
            // Lógica para superponer el knob arrastrado
            style={{ zIndex: minPrice < maxPrice ? 6 : 7 }}
        />
    </div>
);

export const ProductFilterPage = () => {

    const {
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
        filteredProducts,
        absoluteMinPrice,
        absoluteMaxPrice,
        financialError,
        selectDefaultOption,
        selectedPickupBranchName,
        hasSearched,
        resetFilters,
        handleCheckListChange,
        handlePriceChange,
        handleSortChange
    } = useProductFilter();

    // Usar el nuevo hook para formatear
    const { formatCurrency } = useCurrencyFormatter();

    const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);
    const [searchResults, setSearchResults] = useState(0);

    // Actualizar contador total de resultados
    useEffect(() => {
        setSearchResults(products ? products.length : 0);
    }, [products]);

    //Contadores (badges)
    const availableCounts = useMemo(() => {
        const counts = {
            categories: {},
            features: {}
        };

        if (!products) return counts;

        products.forEach(product => {
            // Contar Categorías del producto actual
            if (product.categories) {
                product.categories.forEach(cat => {
                    counts.categories[cat.id] = (counts.categories[cat.id] || 0) + 1;
                });
            }

            // Contar Características del producto actual
            if (product.features) {
                product.features.forEach(feat => {
                    counts.features[feat.id] = (counts.features[feat.id] || 0) + 1;
                });
            }
        });

        return counts;
    }, [products]);

    // FILTRAR CARACTERÍSTICAS A MOSTRAR ---
    // Filtramos las características que no queremos renderizar en la lista de checkboxes
    const visibleFeatures = useMemo(() => {
        return allFeatures.filter(feature => !EXCLUDED_FILTER_FEATURES.includes(feature.name.toLowerCase()));
    }, [allFeatures]);

    /* Renderiza la lista de checkboxes
     type : "categories" o "features" para acceder a los conteos correctos
     listOptions : Lista completa de categorías / features disponibles
     currentSelectedIds : IDs actualmente marcados en el filtro
    */
    const renderFilterLists = (type, listOptions, currentSelectedIds, handleChange) => {
        // Validación de currentSelectedIds para evitar el error .includes()
        if (!currentSelectedIds || !Array.isArray(currentSelectedIds)) {
            console.error(`Error: currentSelectedIds para ${type} no es un array. Valor:`, currentSelectedIds);
            // Retornar un mensaje de error o una lista vacía para evitar fallos
            return (
                <ul className="list-group list-group-flush">
                    <li className="list-group-item text-center text-danger">Error al cargar filtros.</li>
                </ul>
            );
        }

        return (
            <ul className="list-group list-group-flush mb-3">
                {listOptions.map((option) => {

                    // Obtener el conteo calculado en availableCounts
                    const count = availableCounts[type][option.id] || 0;

                    // Verificar si está chequeado
                    const isChecked = currentSelectedIds.includes(option.id);

                    return (
                        <FilterItems
                            key={option.id}
                            label={option.name}
                            // Usamos el ID como número para el componente interno
                            value={option.id}
                            count={count}
                            isChecked={isChecked}
                            onChange={(id, isChecked) => handleChange(id, isChecked, type)}
                        />
                    );
                })}
            </ul>
        );
    };


    // Calcular total de filtros activos para el botón móvil
    const totalActiveFilters = (filteredProducts?.categories?.length || 0) + (filteredProducts?.features?.length || 0) +
        (filteredProducts?.minPrice !== absoluteMinPrice || filteredProducts?.maxPrice !== absoluteMaxPrice ? 1 : 0);

    // --- PROTECCIÓN CONTRA DESBORDAMIENTO CSS (MATEMÁTICA DEFENSIVA) ---
    // Nos aseguramos de que safeAbs siempre tenga un número real para calcular
    const safeAbsMin = absoluteMinPrice || 0;
    const safeAbsMax = absoluteMaxPrice || 10000
    // Calcular el porcentaje de los rangos para el div de progreso activo
    const totalRange = safeAbsMax - safeAbsMin;

    // Aseguramos que los valores a renderizar JAMÁS se salgan del carril de los absolutos
    const safeMin = Math.max(safeAbsMin, Math.min(filteredProducts.minPrice || 0, safeAbsMax));
    const safeMax = Math.min(safeAbsMax, Math.max(filteredProducts.maxPrice || 0, safeAbsMin));

    // Limitamos matemáticamente los porcentajes siempre entre 0% y 100%
    const rangeProgress = totalRange > 0
        ? Math.max(0, Math.min(100, ((safeMax - safeMin) / totalRange) * 100))
        : 0;

    const rangeLeft = totalRange > 0
        ? Math.max(0, Math.min(100, ((safeMin - safeAbsMin) / totalRange) * 100))
        : 0;

    // Estilos personalizados
    const customSliderStyles = `
        /* Asegurar que el componente de App esté listo para contener */
        #product-filter-content {
            font-family: 'Inter', sans-serif;
        }

        /* Contenedor del slider simulado */
        .custom-slider {
            position: relative;
            width: 100%;
            height: 35px; /* Altura para contener la pista y los knobs */
            margin-top: 10px;
        }
        
        /* 1. Pista Inactiva (Fondo gris: Rango total) */
        .custom-slider .range-slider-track {
            position: absolute;
            width: 100%;
            height: 6px;
            background: #f4f3f2; /* Color gris claro (inactivo) */
            border-radius: 3px;
            top: 50%;
            transform: translateY(-50%);
            z-index: 1;
        }

        /* 2. Pista Activa (Color primario: Rango seleccionado) */
        .custom-slider .range-slider-range {
            position: absolute;
            height: 6px;
            background: #1f88e6;
            border-radius: 3px;
            top: 50%;
            transform: translateY(-50%);
            z-index: 2;
        }

        /* 3. Inputs de rango base (Se hacen transparentes y se superponen) */
        .custom-slider .range-slider-input {
            -webkit-appearance: none;
            width: 100%;
            height: 35px; /* Altura para asegurar el agarre del mouse */
            position: absolute;
            top: 50%;
            transform: translateY(-50%);
            left: 0;
            background: transparent;
            pointer-events: none; /* Deshabilitar puntero en la pista */
            z-index: 3;
        }
        
        /* Regla de Bootstrap para el thumb - BASE y Señalizador (Webkit/Chrome/Edge) */
        .custom-slider .range-slider-input::-webkit-slider-thumb {
            -webkit-appearance: none;
            pointer-events: all;
            width: 20px; /* Mayor tamaño para agarre*/
            height: 20px;
            border-radius: 50%;
            background: #ffffff; /* Fondo blanco */
            border: 3px solid #1f88e6; 
            cursor: grab;
            box-shadow: 0 0 5px rgba(0, 0, 0, 0.2); /* Sombra para resaltar */
            position: relative;
            z-index: 10;
            transition: all 0.15s ease-in-out; 
        }

        /* Visibilidad al Sobrevolar (HOVER) */
        .custom-slider .range-slider-input::-webkit-slider-thumb:hover {
            transform: scale(1.1); /* Aumenta 10% el tamaño para mejor agarre */
            box-shadow: 0 0 0 6px rgba(31, 136, 230, 0.2); /* Halo suave */
        }
        
        /* Mayor Feedback al Arrastrar (ACTIVE) */
        .custom-slider .range-slider-input:active::-webkit-slider-thumb {
            transform: scale(1.2); /* Aumenta un poco más */
            box-shadow: 0 0 0 8px rgba(31, 136, 230, 0.3); /* Máxima visibilidad */
            cursor: grabbing; /* Cambia el cursor mientras se arrastra */
        }


        /* Regla de Bootstrap para el thumb (Firefox) - BASE y Señalizador */
        .custom-slider .range-slider-input::-moz-range-thumb {
            pointer-events: all;
            width: 20px;
            height: 20px;
            border-radius: 50%;
            background: #ffffff;
            border: 3px solid #1f88e6;;
            cursor: grab;
            box-shadow: 0 0 5px rgba(0, 0, 0, 0.2);
            z-index: 10;
            transition: all 0.15s ease-in-out;
        }

        /* Visibilidad al Sobrevolar (HOVER) - Firefox */
        .custom-slider .range-slider-input::-moz-range-thumb:hover {
            transform: scale(1.1);
            box-shadow: 0 0 0 6px rgba(31, 136, 230, 0.2);
        }

        /* Mayor Feedback al Arrastrar (ACTIVE) - Firefox */
        .custom-slider .range-slider-input:active::-moz-range-thumb {
            transform: scale(1.2);
            box-shadow: 0 0 0 8px rgba(31, 136, 230, 0.3);
            cursor: grabbing;
        }
        
        .badge-color {
            background-color: #1f88e6 !important;
            color: #f4f3f2 !important;
        }
        
        .card-shadow-filter {
            box-shadow: 0 4px 6px rgba(46, 46, 132, 0.8);
            border-radius: 12px;
            overflow: hidden;
        }

        .filter-btn {
            background-color: #2e2e84 !important;
            color: #f4f3f2 !important;
            border-radius: 8px;
            transition: all 0.2s;
        }
        
        .filter-btn:hover {
            background-color: #1a1a5c !important;
        }

        .card-background-filter {
            background-color: #f4f3f2;
        }

        .filter-title-text {
            color: #2e2e84;
            font-size: 0.95rem;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }

        .filter-card-header {
            background-color: #f8f9fa;
            color: #2e2e84;
            border-bottom: 1px solid #e9ecef;
        }

        .custom-checkbox:checked {
            background-color: #1f88e6;
            border-color: #1f88e6;
        }

        .text-muted{
        color:  #6a5e9b !important
        }
    `;


    return (

        <main id="product-filter-content" className="min-vh-100 container-fluid py-5">
            {/* Inyección de estilos custom */}
            <style dangerouslySetInnerHTML={{ __html: customSliderStyles }} />

            {/* CABECERA DE BÚSQUEDA COMPACTA */}
            <section className="container card shadow bg-white border-bottom py-3 mb-4 shadow-sm" style={{ zIndex: 1020 }}>

                {/* Uso del componente con la prop isCompact */}
                <BookingSearchForm
                    citiesWithBranches={citiesWithBranches}
                    isCompact={true}
                    onSearchSubmit={() => {
                        // Sobrescribimos con una función vacía para evitar que 
                        // el formulario ejecute el "navigate" por defecto.
                        // El Contexto ya se habrá actualizado.
                    }}
                />
            </section>

            <section className="container p-0 mt-5">
                <div className="row g-4">

                    {/* --- COLUMNA IZQUIERDA: FILTROS --- */}
                    <div className="col-xl-3 col-12">

                        {/* VERSION DESKTOP (Visible en lg en adelante) */}
                        <div className="card d-none d-xl-block rounded-3 border-0 card-shadow-filter">
                            <div className="filter-card-header fw-bold py-3">
                                <i className="bi bi-funnel mx-2"></i>Filtros:
                            </div>

                            {/* Filtros de Categorías */}
                            <div className='fs-6 fw-bold my-2 mx-3 filter-title-text'>Categorías:</div>
                            {categoryError && <div className="alert alert-danger text-center">{categoryError}</div>}
                            {isLoadingCategory
                                ? (<div className="text-center my-5">
                                    <div className="spinner-border" role="status"></div>
                                    <p className='text-muted'>Cargando Categorías...</p>
                                </div>)
                                : (
                                    renderFilterLists(
                                        "categories",
                                        allCategories,
                                        filteredProducts.categories,
                                        handleCheckListChange)
                                )
                            }

                            {/* Filtros de Características */}
                            <div className='fs-6 fw-bold my-2 mx-3 filter-title-text'>Características:</div>
                            {featureError && <div className="alert alert-danger text-center">{featureError}</div>}
                            {isLoadingFeature
                                ? (<div className="text-center my-5">
                                    <div className="spinner-border" role="status"></div>
                                    <p className='text-muted'>Cargando Características...</p>
                                </div>)
                                : (renderFilterLists(
                                    "features",
                                    visibleFeatures,
                                    filteredProducts.features,
                                    handleCheckListChange)
                                )
                            }

                            {/* FILTRO DE PRECIO */}
                            <div className='fs-6 fw-bold my-2 mx-3 filter-title-text'>Rango de Precio:</div>
                            {/* Mostrar alerta si falló la configuración financiera */}
                            {financialError && (
                                <div className="alert alert-warning d-flex align-items-center mx-3 my-2 p-2 shadow-sm animate-in fade-in"
                                    style={{ borderLeft: '4px solid #ffc107', fontSize: '0.85rem' }}>
                                    <i className="bi bi-exclamation-triangle-fill me-2 fs-5 text-warning"></i>
                                    <span className="text-dark fw-medium lh-sm">{financialError}</span>
                                </div>
                            )}
                            <div className="mb-4 border-bottom pb-3 mx-3">
                                {/* Display de Valores Formateados */}
                                <div className="d-flex justify-content-between mb-2 small text-muted mx-3">
                                    <span className='text-start'>Mínimo: <strong>{formatCurrency(filteredProducts.minPrice)}</strong></span>
                                    <span className='text-end'>Máximo: <strong>{formatCurrency(filteredProducts.maxPrice)}</strong></span>
                                </div>
                                <FilterAmount
                                    absoluteMin={safeAbsMin}
                                    absoluteMax={safeAbsMax}
                                    minPrice={safeMin}
                                    maxPrice={safeMax}
                                    rangeLeft={rangeLeft}
                                    rangeProgress={rangeProgress}
                                    // Handler para el MÍNIMO: Evita que el mínimo supere al máximo actual.
                                    onChangeMin={(value) => {
                                        // Evitar cruce con un margen de 10.000
                                        const maxLimit = Math.max(safeAbsMin, filteredProducts.maxPrice - 10000);
                                        handlePriceChange('minPrice', Math.min(Number(value), maxLimit));
                                    }}
                                    // Handler para el MÁXIMO: Evita que el máximo caiga por debajo del mínimo actual.
                                    onChangeMax={(value) => {
                                        // Evitar cruce con un margen de 10.000
                                        const minLimit = Math.min(safeAbsMax, filteredProducts.minPrice + 10000);
                                        handlePriceChange('maxPrice', Math.max(Number(value), minLimit));
                                    }}
                                />
                            </div>

                            <div className="p-2 bg-light border-top">
                                <button
                                    className="btn btn-sm btn-outline-danger w-100"
                                    onClick={resetFilters}
                                >
                                    Limpiar Filtros
                                </button>
                            </div>
                        </div>

                        {/* VERSION MOBILE */}
                        <div className="d-block d-xl-none mb-3">
                            <div className="dropdown">
                                <button
                                    className="btn filter-btn dropdown-toggle w-100 d-flex justify-content-between align-items-center"
                                    type="button"
                                    onClick={() => setIsMobileMenuOpen(!isMobileMenuOpen)}
                                >
                                    <span>Filtros ({totalActiveFilters})</span>
                                </button>

                                {/* Menú desplegable */}
                                <div
                                    className={`dropdown-menu w-100 p-0 ${isMobileMenuOpen ? 'show' : ''}`}
                                    style={{ position: 'relative' }}
                                >
                                    {/* Reutilizamos la misma lógica de renderizado */}
                                    {/* Filtros de Categorías */}
                                    <div className='fs-6 fw-bold my-2 mx-3 filter-title-text'>Categorías:</div>
                                    {categoryError && <div className="alert alert-danger text-center">{categoryError}</div>}
                                    {isLoadingCategory
                                        ? (<div className="text-center my-5">
                                            <div className="spinner-border" role="status"></div>
                                            <p className='text-muted'>Cargando Categorías...</p>
                                        </div>)
                                        : (
                                            renderFilterLists(
                                                "categories",
                                                allCategories,
                                                filteredProducts.categories,
                                                handleCheckListChange)
                                        )
                                    }

                                    {/* Filtros de Características */}
                                    <div className='fs-6 fw-bold my-2 mx-3 filter-title-text'>Características:</div>
                                    {featureError && <div className="alert alert-danger text-center">{featureError}</div>}
                                    {isLoadingFeature
                                        ? (<div className="text-center my-5">
                                            <div className="spinner-border" role="status"></div>
                                            <p className='text-muted'>Cargando Características...</p>
                                        </div>)
                                        : (renderFilterLists(
                                            "features",
                                            visibleFeatures,
                                            filteredProducts.features,
                                            handleCheckListChange)
                                        )
                                    }

                                    {/* Filtro de Precio para móvil */}
                                    <div className='fs-6 fw-bold my-2 mx-3 filter-title-text'>Rango de Precio:</div>
                                    {/* Mostrar alerta si falló la configuración financiera */}
                                    {financialError && (
                                        <div className="alert alert-warning d-flex align-items-center mx-3 my-2 p-2 shadow-sm animate-in fade-in"
                                            style={{ borderLeft: '4px solid #ffc107', fontSize: '0.85rem' }}>
                                            <i className="bi bi-exclamation-triangle-fill me-2 fs-5 text-warning"></i>
                                            <span className="text-dark fw-medium lh-sm">{financialError}</span>
                                        </div>
                                    )}
                                    <div className="mb-4 border-bottom pb-3 mx-3">
                                        {/* Display de Valores Formateados */}
                                        <div className="d-flex justify-content-between mb-2 small text-muted mx-3">
                                            <span className='text-start'>Mínimo: <strong>{formatCurrency(filteredProducts.minPrice)}</strong></span>
                                            <span className='text-end'>Máximo: <strong>{formatCurrency(filteredProducts.maxPrice)}</strong></span>
                                        </div>
                                        <FilterAmount
                                            absoluteMin={safeAbsMin}
                                            absoluteMax={safeAbsMax}
                                            minPrice={safeMin}
                                            maxPrice={safeMax}
                                            rangeLeft={rangeLeft}
                                            rangeProgress={rangeProgress}
                                            // Handler para el MÍNIMO: Evita que el mínimo supere al máximo actual.
                                            onChangeMin={(value) => {
                                                const maxLimit = Math.max(safeAbsMin, filteredProducts.maxPrice - 10000);
                                                handlePriceChange('minPrice', Math.min(Number(value), maxLimit));
                                            }}
                                            // Handler para el MÁXIMO: Evita que el máximo caiga por debajo del mínimo actual.
                                            onChangeMax={(value) => {
                                                const minLimit = Math.min(safeAbsMax, filteredProducts.minPrice + 10000);
                                                handlePriceChange('maxPrice', Math.max(Number(value), minLimit));
                                            }}
                                        />
                                    </div>

                                    <div className="p-2 bg-light border-top">
                                        <button
                                            className="btn btn-sm btn-outline-danger w-100"
                                            onClick={() => {
                                                resetFilters();
                                                setIsMobileMenuOpen(false);
                                            }}
                                        >
                                            Limpiar Filtros
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    {/* --- COLUMNA DERECHA: RESULTADOS --- */}
                    <div className="col-xl-9 col-12">
                        <div className="product-listings-section">

                            {/* TABS DE CATEGORÍA RÁPIDA (Estilo 'Pills') */}
                            <div className="card card-background-filter border-0 mb-4 rounded-3">
                                <div className="card-body p-2 d-flex flex-column flex-sm-row align-items-sm-center">
                                    <div className="d-flex align-items-center justify-content-center px-3 py-2 flex-shrink-0">
                                        <span className="text-uppercase fw-bold filter-title-text small me-2">Navegación Rápida:</span>
                                    </div>
                                    <ul className="nav nav-pills nav-fill flex-nowrap overflow-x-auto flex-grow-1">
                                        {allCategories.map((cat) => {
                                            const isSelected = filteredProducts.categories.includes(cat.id);
                                            return (
                                                <li className="nav-item" key={cat.id}>
                                                    <button
                                                        className={`nav-link w-100 ${isSelected ? 'active' : ''}`}
                                                        onClick={() => {
                                                            handleCheckListChange(cat.id, !isSelected, 'categories');
                                                        }}
                                                    >
                                                        {cat.name}
                                                    </button>
                                                </li>)
                                        })}
                                    </ul>
                                </div>
                            </div>

                            {/* RESULTADOS */}
                            <div className="d-flex flex-column flex-sm-row justify-content-sm-between align-items-sm-center mb-3">

                                <h3 className="fw-bold filter-title-text m-0 text-center text-sm-start mb-2 mb-sm-0 fs-5">
                                    {hasSearched ? `Resultados en ${selectedPickupBranchName ? selectedPickupBranchName.name : 'tu búsqueda'}: ` : 'Resultado: '}
                                    <span className="filter-title-text fs-6">{searchResults}</span> vehículos
                                </h3>

                                <div className='d-flex flex-column justify-content-center align-items-center w-sm-auto'>
                                    <label className='filter-title-text fs-6' htmlFor="sort-select">
                                        <i className="bi bi-arrow-down-up me-2"></i>Ordenar por:
                                    </label>

                                    <select
                                        id="sort-select"
                                        className="form-select w-auto"
                                        aria-label="Ordenar por"
                                        value={selectDefaultOption}
                                        onChange={(e) => handleSortChange(e.target.value)}
                                    >
                                        <option value="price_asc">Menor precio</option>
                                        <option value="price_desc">Mayor precio</option>
                                    </select>
                                </div>
                            </div>

                            {productError && <div className="alert alert-danger text-center">{productError}</div>}
                            {isLoadingProduct ? (
                                <div className="text-center my-5">
                                    <div className="spinner-border" role="status"></div>
                                    <p className='text-muted'>Buscando los mejores vehículos...</p>
                                </div>
                            ) : (!products || (products.length === 0 && !productError)) ? (
                                <div className="alert alert-info text-center mt-5"> {/* MENSAJE DE NO RESULTADOS */}
                                    No se encontraron productos con los filtros seleccionados.
                                </div>
                            ) : (
                                <div className="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4"> {/* GRID DE PRODUCTOS */}
                                    {products.map((product) => (
                                        <ProductCardComponent key={product.id} product={product} />
                                    ))}
                                </div>
                            )
                            }
                        </div>
                    </div>
                </div>
            </section>
        </main>
    )
}