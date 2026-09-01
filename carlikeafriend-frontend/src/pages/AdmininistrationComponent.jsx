import { Link, NavLink, Outlet } from 'react-router-dom'
import '../styles/AdministrationStyle.css'
import '../styles/MainStyle.css'

export const AdmininistrationComponent = () => {
    /* Contenido del Panel de Administración (oculto en móviles por CSS) */
    /* Contenido principal del panel */
    return (
        <>
            {/* Advertencia para dispositivos móviles */}
            <div id="mobile-warning" className="d-md-none text-center p-4">
                <i className="bi bi-exclamation-triangle-fill text-warning display-4"></i>
                <h2 className="mt-3">Acceso Restringido</h2>
                <p>El panel de administración no está disponible en dispositivos móviles.</p>
                <p>Por favor, accede desde un dispositivo de escritorio para gestionar tu negocio.</p>
                <Link to="/" className="btn mobile-warning-btn me-2 rounded-3" >Volver al Inicio</Link>
            </div>

            <main id="admin-panel-content" className="min-vh-100 container-fluid py-4">
                <div className='container'>
                    <h2 className="fw-bold text-admin-panel text-center mt-5 mb-5">Panel de Administración</h2>

                    <div className="row">
                        {/* Menú de Administración */}
                        <div className="col-md-3 mb-4">
                            <div className="card custom-card-shadow rounded-3 border-0">
                                <div className="card-header fw-bold">
                                    Menú de Funciones
                                </div>
                                <ul className="list-group list-group-flush border-0">

                                    {/* SECCIÓN: GESTIONAR PRODUCTOS */}
                                    <li className="list-group-item p-0 border-0">
                                        <NavLink to="#product-management-options" className="text-decoration-none d-block py-3 px-3 form-text fw-bold" data-bs-toggle="collapse" data-bs-target="#product-management-options" aria-expanded="false" aria-controls="product-management-options">
                                            <i className="bi bi-inboxes-fill me-2"></i> Gestionar Productos
                                        </NavLink>
                                        <div className="collapse" id="product-management-options">
                                            <ul className="list-group list-group-flush">
                                                <li className="list-group-item p-0 border-0 bg-transparent">
                                                    <NavLink to="product-list" className="text-decoration-none d-block py-2 px-4 admin-nav-link">
                                                        <i className="bi bi-list-ul me-2"></i> Lista de productos
                                                    </NavLink>
                                                </li>
                                                <li className="list-group-item p-0 border-0 bg-transparent">
                                                    <NavLink to="category-list" className="text-decoration-none d-block py-2 px-4 admin-nav-link">
                                                        <i className="bi bi-ui-checks-grid me-2"></i> Administrar categorías
                                                    </NavLink>
                                                </li>
                                                <li className="list-group-item p-0 border-0 bg-transparent">
                                                    <NavLink to="feature-list" className="text-decoration-none d-block py-2 px-4 admin-nav-link">
                                                        <i className="bi bi-ui-checks me-2"></i> Administrar características
                                                    </NavLink>
                                                </li>
                                                <li className="list-group-item p-0 border-0 bg-transparent">
                                                    <NavLink to="make-list" className="text-decoration-none d-block py-2 px-4 admin-nav-link">
                                                        <i className="bi bi-ui-radios me-2"></i> Administrar marca
                                                    </NavLink>
                                                </li>
                                                <li className="list-group-item p-0 border-0 bg-transparent">
                                                    <NavLink to="vehicle-list" className="text-decoration-none d-block py-2 px-4 admin-nav-link">
                                                        <i className="bi bi-car-front me-2"></i> Administrar vehículos
                                                    </NavLink>
                                                </li>
                                            </ul>
                                        </div>
                                    </li>

                                    {/* SECCIÓN: GESTIONAR USUARIOS */}
                                    <li className="list-group-item p-0 border-0">
                                        <NavLink to="#user-management-options" className="text-decoration-none d-block py-3 px-3 form-text fw-bold" data-bs-toggle="collapse" data-bs-target="#user-management-options" aria-expanded="false" aria-controls="user-management-options">
                                            <i className="bi bi-people-fill me-2"></i> Gestionar Usuarios
                                        </NavLink>
                                        <div className="collapse" id="user-management-options">
                                            <ul className="list-group list-group-flush">
                                                <li className="list-group-item p-0 border-0 bg-transparent">
                                                    <NavLink to="permission-list" className="text-decoration-none d-block py-2 px-4 admin-nav-link">
                                                        <i className="bi bi-person-fill-check me-2"></i> Permisos
                                                    </NavLink>
                                                </li>
                                                <li className="list-group-item p-0 border-0 bg-transparent">
                                                    <NavLink to="role-list" className="text-decoration-none d-block py-2 px-4 admin-nav-link">
                                                        <i className="bi bi-person-fill-gear me-2"></i> Roles
                                                    </NavLink>
                                                </li>
                                                <li className="list-group-item p-0 border-0 bg-transparent">
                                                    <NavLink to="user-list" className="text-decoration-none d-block py-2 px-4 admin-nav-link">
                                                        <i className="bi bi-person-lines-fill me-2"></i> Usuarios
                                                    </NavLink>
                                                </li>
                                            </ul>
                                        </div>
                                    </li>

                                    {/* SECCIÓN: GESTIONAR POLÍTICAS */}
                                    <li className="list-group-item p-0 border-0">
                                        <NavLink to="#policy-management-options" className="text-decoration-none d-block py-3 px-3 form-text fw-bold" data-bs-toggle="collapse" data-bs-target="#policy-management-options" aria-expanded="false" aria-controls="policy-management-options">
                                            <i className="bi bi-shield-fill-check me-2"></i> Gestionar Políticas
                                        </NavLink>
                                        <div className="collapse" id="policy-management-options">
                                            <ul className="list-group list-group-flush">
                                                <li className="list-group-item p-0 border-0 bg-transparent">
                                                    <NavLink to="policyType-list" className="text-decoration-none d-block py-2 px-4 admin-nav-link">
                                                        <i className="bi bi-layers-fill me-2"></i> Tipo de Políticas
                                                    </NavLink>
                                                </li>
                                                <li className="list-group-item p-0 border-0 bg-transparent">
                                                    <NavLink to="policy-list" className="text-decoration-none d-block py-2 px-4 admin-nav-link">
                                                        <i className="bi bi-briefcase-fill me-2"></i> Administrar Políticas
                                                    </NavLink>
                                                </li>
                                            </ul>
                                        </div>
                                    </li>

                                    {/* SECCIÓN: GESTIONAR SUCURSALES */}
                                    <li className="list-group-item p-0 border-0">
                                        <NavLink to="#branch-management-options" className="text-decoration-none d-block py-3 px-3 form-text fw-bold" data-bs-toggle="collapse" data-bs-target="#branch-management-options" aria-expanded="false" aria-controls="branch-management-options">
                                            <i className="bi bi-building-fill me-2"></i> Gestionar Sucursales
                                        </NavLink>
                                        <div className="collapse" id="branch-management-options">
                                            <ul className="list-group list-group-flush">
                                                <li className="list-group-item p-0 border-0 bg-transparent">
                                                    <NavLink to="city-list" className="text-decoration-none d-block py-2 px-4 admin-nav-link">
                                                        <i className="bi bi-map-fill me-2"></i> Ciudades
                                                    </NavLink>
                                                </li>
                                                <li className="list-group-item p-0 border-0 bg-transparent">
                                                    <NavLink to="branch-list" className="text-decoration-none d-block py-2 px-4 admin-nav-link">
                                                        <i className="bi bi-building-fill-gear me-2"></i> Administrar sucursales
                                                    </NavLink>
                                                </li>
                                                <li className="list-group-item p-0 border-0 bg-transparent">
                                                    <NavLink to="transferFee-list" className="text-decoration-none d-block py-2 px-4 admin-nav-link">
                                                        <i className="bi bi-arrow-left-right me-2"></i> Tarifa de transferencia
                                                    </NavLink>
                                                </li>
                                                <li className="list-group-item p-0 border-0 bg-transparent">
                                                    <NavLink to="extras-list" className="text-decoration-none d-block py-2 px-4 admin-nav-link">
                                                        <i className="bi bi-puzzle-fill me-2"></i> Extras
                                                    </NavLink>
                                                </li>
                                                <li className="list-group-item p-0 border-0 bg-transparent">
                                                    <NavLink to="branchAddon-list" className="text-decoration-none d-block py-2 px-4 admin-nav-link">
                                                        <i className="bi bi-boxes me-2"></i> Administrar stock extras
                                                    </NavLink>
                                                </li>
                                            </ul>
                                        </div>
                                    </li>

                                    {/* SECCIÓN: GESTIÓN FINANCIERA */}
                                    <li className="list-group-item p-0 border-0">
                                        <NavLink to="#financial-management-options" className="text-decoration-none d-block py-3 px-3 form-text fw-bold" data-bs-toggle="collapse" data-bs-target="#financial-management-options" aria-expanded="false" aria-controls="financial-management-options">
                                            <i className="bi bi-calculator-fill me-2"></i> Gestión Financiera
                                        </NavLink>
                                        <div className="collapse" id="financial-management-options">
                                            <ul className="list-group list-group-flush">
                                                <li className="list-group-item p-0 border-0 bg-transparent">
                                                    <NavLink to="financial-config" className="text-decoration-none d-block py-2 px-4 admin-nav-link">
                                                        <i className="bi bi-gear-fill me-2"></i> Configuración Financiera
                                                    </NavLink>
                                                </li>
                                            </ul>
                                        </div>
                                    </li>

                                    {/* SECCIÓN: PRÓXIMAMENTE */}
                                    <li className="list-group-item p-0 border-0 bg-transparent disabled">
                                        <div className="text-decoration-none d-block text-muted py-3 px-3">
                                            <i className="bi bi-tools me-2"></i> En desarrollo (Próximamente)
                                        </div>
                                    </li>
                                </ul>
                            </div>
                        </div>

                        {/* Contenedor Principal (Outlet) */}
                        <div className="col-md-9 mb-4">
                            <Outlet />
                        </div>
                    </div>
                </div>
            </main>
        </>
    )
}
