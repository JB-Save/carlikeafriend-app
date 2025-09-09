import { Link, Outlet } from 'react-router-dom'
import '../styles/AdministrationStyle.css'
import '../styles/MainStyle.css'

export const AdmininistrationComponent = () => {
    /* Contenido del Panel de Administración (oculto en móviles por CSS) */
    /* Contenido principal del panel */
    return (
        <>
            {/* Advertencia para dispositivos móviles */}
            {/* Se muestra solo en dispositivos pequeños (md o menos) */}
            <div id="mobile-warning" className="d-md-none text-center p-4">
                <i className="bi bi-exclamation-triangle-fill text-warning display-4"></i>
                <h2 className="mt-3">Acceso Restringido</h2>
                <p>El panel de administración no está disponible en dispositivos móviles.</p>
                <p>Por favor, accede desde un dispositivo de escritorio para gestionar tu negocio.</p>
                {/* Usar navigate de React Router para una mejor gestión de la navegación */}
                <Link to="/" className="btn mobile-warning-btn me-2 rounded-lg" >Volver al Inicio</Link>
            </div>

            <main id="admin-panel-content" className="min-vh-100 container-fluid py-4">
                <h2 className="h3 fw-bold text-admin-panel text-center mt-5 mb-5">Panel de Administración</h2>

                <div className="row">
                    {/* Menú de Administración */}
                    <div className="col-md-3 mb-4">
                        <div className="card card-shadow rounded-lg">
                            <div className="card-header fw-bold">
                                Menú de Funciones
                            </div>
                            <ul className="list-group list-group-flush">
                                <li className="list-group-item">
                                    <Link to="add-product" className="text-decoration-none d-block py-2">
                                        <i className="bi bi-plus-circle-fill me-2"></i> Agregar Producto
                                    </Link>
                                </li>
                                <li className="list-group-item">
                                    <Link to="product-list" className="text-decoration-none d-block py-2">
                                        <i className="bi bi-list-ul me-2"></i> Lista de productos
                                    </Link>
                                </li>
                                <li className="list-group-item disabled">
                                    <Link to="#" className="text-decoration-none d-block text-muted py-2">
                                        <i className="bi bi-people-fill me-2"></i> Gestionar Usuarios (Próximamente)
                                    </Link>
                                </li>
                                <li className="list-group-item disabled">
                                    <Link to="#" className="text-decoration-none d-block text-muted py-2">
                                        <i className="bi bi-bar-chart-line-fill me-2"></i> Ver Estadísticas (Próximamente)
                                    </Link>
                                </li>
                            </ul>
                        </div>
                    </div>
                    {/* El componente Outlet renderiza los componentes anidados */}
                    <div className="col-md-9">
                        {/* Contenido Principal del Panel (aquí se cargarán los módulos) */}
                        <Outlet/>
                    </div>                   
                </div>
            </main>
        </>
    )
}
