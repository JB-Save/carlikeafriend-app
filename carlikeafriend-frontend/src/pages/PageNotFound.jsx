export const PageNotFound = () => {
  return (
    <main id="page-not-found" className="min-vh-100 container-fluid py-4 d-flex align-items-center justify-content-center">

      <div className="container">
        <div className="row justify-content-center">
          <div className="col-12 col-md-8 col-lg-6 text-center bg-white p-5 rounded-4 shadow-sm border-0">

            {/* Texto para errores 404 */}
            <div className="mb-4">
              <h1 className="display-1 fw-bolder mb-0" style={{ color: '#1f88e6', fontSize: '7rem', textShadow: '2px 4px 10px rgba(31, 136, 230, 0.2)' }}>
                404
              </h1>
            </div>

            {/* Título del error */}
            <h2 className="h3 fw-bold mt-2 mb-3" style={{ color: '#2e2e84', fontSize: '1.5rem' }}>
              No se puede encontrar la página.
            </h2>

            {/* Detalles del error */}
            <p className="mb-5" style={{ color: '#6a5e9b', fontSize: '1.25rem' }}>
              Lo sentimos, pero la página que estás buscando no es real o ya ha sido movida.
            </p>

            {/* Botón para volver a la página principal. */}
            <button
              onClick={() => window.location.href = '/'}
              className="btn text-white rounded-pill px-5 py-3 fw-bold shadow-sm"
              style={{ backgroundColor: '#2e2e84', transition: 'all 0.3s', border: 'none' }}
              onMouseOver={(e) => e.target.style.backgroundColor = '#1f88e6'}
              onMouseOut={(e) => e.target.style.backgroundColor = '#2e2e84'}
            >
              <i className="bi bi-house-door-fill me-2"></i> Regresar a la página principal
            </button>

          </div>
        </div>
      </div>

    </main>
  );
}
