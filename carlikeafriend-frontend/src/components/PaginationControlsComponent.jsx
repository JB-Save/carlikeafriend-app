import { Link } from "react-router-dom";

export const PaginationControlsComponent = ({ currentPage, totalPages, goToPage, type }) => {

    const isFirstPage = currentPage === 1;
    const isLastPage = currentPage === totalPages || totalPages === 0;

    return (
        <nav aria-label={`Navegación de páginas de ${type}`} className="mt-4">
            <ul className="pagination justify-content-center">
                <li className={`page-item ${isFirstPage ? 'disabled' : ''}`}>
                    <Link className="page-link" to="#" aria-label="Primera" onClick={() => goToPage(1)} disabled={isFirstPage}>
                        <span aria-hidden="true">&laquo;</span>
                    </Link>
                </li>
                <li className={`page-item ${isFirstPage ? 'disabled' : ''}`}>
                    <Link className="page-link" to="#" aria-label="Anterior" onClick={() => goToPage(currentPage - 1)} disabled={isFirstPage}>
                        <span aria-hidden="true">&lsaquo;</span>
                    </Link>
                </li>
                <li className="page-item active">
                    <span className="page-link">
                        {currentPage}
                    </span>
                </li>
                <li className={`page-item ${isLastPage ? 'disabled' : ''}`}>
                    <Link className="page-link" to="#" aria-label="Siguiente" onClick={() => goToPage(currentPage + 1)} disabled={isLastPage}>
                        <span aria-hidden="true">&rsaquo;</span>
                    </Link>
                </li>
                <li className={`page-item ${isLastPage ? 'disabled' : ''}`}>
                    <Link className="page-link" to="#" aria-label="Última" onClick={() => goToPage(totalPages)} disabled={isLastPage}>
                        <span aria-hidden="true">&raquo;</span>
                    </Link>
                </li>
            </ul>
        </nav>


    );
}
