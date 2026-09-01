import React from 'react';

export const PaginationControlsComponent = ({ currentPage, totalPages, goToPage, type = "ítems" }) => {

    const isFirstPage = currentPage === 1;
    const isLastPage = currentPage === totalPages || totalPages === 0;

    if (totalPages <= 1) return null; // Limpieza visual: Oculta la paginación si solo hay 1 página

    // Lógica para mostrar máximo 5 páginas a la vez y evitar que el diseño colapse
    const getVisiblePages = () => {
        let start = Math.max(1, currentPage - 2);
        let end = Math.min(totalPages, currentPage + 2);

        if (currentPage <= 3) end = Math.min(5, totalPages);
        if (currentPage >= totalPages - 2) start = Math.max(1, totalPages - 4);

        const pages = [];
        for (let i = start; i <= end; i++) {
            pages.push(i);
        }
        return pages;
    };

    return (
        <nav aria-label={`Navegación de páginas de ${type}`} className="mt-5 mb-2">
            <ul className="pagination justify-content-center gap-2 border-0 align-items-center">

                {/* Botón: Primera Página */}
                <li className={`page-item ${isFirstPage ? 'disabled' : ''}`}>
                    <button
                        className="page-link rounded-circle d-flex align-items-center justify-content-center shadow-sm border-0 page-link-hover custom-page-btn"
                        onClick={() => { if (!isFirstPage) goToPage(1); }}
                        disabled={isFirstPage}
                        aria-label="Primera página"
                    >
                        <span aria-hidden="true">&laquo;</span>
                    </button>
                </li>

                {/* Botón: Anterior */}
                <li className={`page-item ${isFirstPage ? 'disabled' : ''}`}>
                    <button
                        className="page-link rounded-circle d-flex align-items-center justify-content-center shadow-sm border-0 page-link-hover custom-page-btn"
                        onClick={() => { if (!isFirstPage) goToPage(currentPage - 1); }}
                        disabled={isFirstPage}
                        aria-label="Página anterior"
                    >
                        <span aria-hidden="true">&lsaquo;</span>
                    </button>
                </li>

                {/* Botones: Números de Página (Dinámicos) */}
                {getVisiblePages().map(page => (
                    <li key={page} className={`page-item ${currentPage === page ? 'active' : ''}`}>
                        <button
                            className={`page-link rounded-circle d-flex align-items-center justify-content-center shadow border-0 fw-bold ${currentPage === page ? 'custom-active-btn' : 'page-link-hover custom-page-btn'}`}
                            onClick={() => goToPage(page)}
                        >
                            {page}
                        </button>
                    </li>
                ))}

                {/* Botón: Siguiente */}
                <li className={`page-item ${isLastPage ? 'disabled' : ''}`}>
                    <button
                        className="page-link rounded-circle d-flex align-items-center justify-content-center shadow-sm border-0 page-link-hover custom-page-btn"
                        onClick={() => { if (!isLastPage) goToPage(currentPage + 1); }}
                        disabled={isLastPage}
                        aria-label="Página siguiente"
                    >
                        <span aria-hidden="true">&rsaquo;</span>
                    </button>
                </li>

                {/* Botón: Última Página */}
                <li className={`page-item ${isLastPage ? 'disabled' : ''}`}>
                    <button
                        className="page-link rounded-circle d-flex align-items-center justify-content-center shadow-sm border-0 page-link-hover custom-page-btn"
                        onClick={() => { if (!isLastPage) goToPage(totalPages); }}
                        disabled={isLastPage}
                        aria-label="Última página"
                    >
                        <span aria-hidden="true">&raquo;</span>
                    </button>
                </li>
            </ul>

            {/* Tus estilos exactos, optimizados para extraerlos de las etiquetas en línea */}
            <style>
                {`
                    .custom-page-btn {
                        width: 40px;
                        height: 40px;
                        color: #2E2E84;
                        background-color: #F4F3F2;
                    }
                    .custom-active-btn {
                        width: 45px;
                        height: 45px;
                        background-color: #1F88E6 !important;
                        color: #F4F3F2 !important;
                    }
                    .page-link-hover:not(:disabled):hover {
                        background-color: #2E2E84 !important;
                        color: #F4F3F2 !important;
                        transform: scale(1.1);
                        transition: all 0.2s ease;
                    }
                    .page-item.disabled .page-link {
                        color: #A0A0A0 !important;
                        background-color: #F4F3F2 !important;
                        opacity: 0.6;
                        cursor: not-allowed;
                        transform: none; /* Previene el scale en botones deshabilitados */
                    }
                    .pagination .page-link { 
                        border-radius: 50% !important; 
                        margin: 0 4px; 
                    }
                `}
            </style>
        </nav>
    );
};