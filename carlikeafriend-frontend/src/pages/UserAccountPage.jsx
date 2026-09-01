import { Link, NavLink, Outlet } from 'react-router-dom';
import '../styles/MyAccountStyle.css';
import '../styles/MainStyle.css';

export const UserAccountPage = () => {
  return (
    <main id="user-account-page" className="min-vh-100 container-fluid py-4">
      <div className='container'>
        <h2 className="h3 fw-bold text-center mt-4 mb-5 my-account-block-title-color">
          Mi Cuenta
        </h2>

        <div className="row">
          {/* Menú Lateral del Perfil */}
          <div className="col-lg-3 mb-4">
            <div className="card custom-my-account-card-shadow rounded-3 border-0">
              <div className="my-account-card-header fw-bold text-center">
                Menú de Usuario
              </div>
              {/* Usamos NavLink en lugar de Link para aprovechar la clase "active" 
                                automática de react-router-dom y resaltar la sección actual.
                            */}
              <ul className="list-group list-group-flush">
                <li className="list-group-item p-0">
                  <NavLink
                    to="/my-account"
                    end
                    className={({ isActive }) => `text-decoration-none d-block py-3 px-3 my-account-form-text ${isActive ? 'fw-bold bg-light border-start border-4 border-primary' : ''}`}
                  >
                    <i className="bi bi-person-lines-fill me-3 text-primary"></i> Mis Datos
                  </NavLink>
                </li>
                <li className="list-group-item p-0">
                  <NavLink
                    to="/my-account/reservations"
                    className={({ isActive }) => `text-decoration-none d-block py-3 px-3 my-account-form-text ${isActive ? 'fw-bold bg-light border-start border-4 border-primary' : ''}`}
                  >
                    <i className="bi bi-car-front-fill me-3 text-success"></i> Mis Reservas
                  </NavLink>
                </li>
                <li className="list-group-item p-0">
                  <NavLink
                    to="/my-account/favorites"
                    className={({ isActive }) => `text-decoration-none d-block py-3 px-3 my-account-form-text ${isActive ? 'fw-bold bg-light border-start border-4 border-primary' : ''}`}
                  >
                    <i className="bi bi-heart-fill me-3 text-danger"></i> Mis Favoritos
                  </NavLink>
                </li>
                <li className="list-group-item p-0">
                  <NavLink
                    to="/my-account/security"
                    className={({ isActive }) => `text-decoration-none d-block py-3 px-3 my-account-form-text ${isActive ? 'fw-bold bg-light border-start border-4 border-primary' : ''}`}
                  >
                    <i className="bi bi-shield-lock-fill me-3 text-secondary"></i> Seguridad
                  </NavLink>
                </li>
              </ul>
            </div>
          </div>

          {/* Contenedor Dinámico (Outlet) */}
          <div className="col-lg-9">
            <div className="card custom-my-account-card-shadow rounded-3 border-0 bg-white p-4 min-vh-50">
              <Outlet />
            </div>
          </div>
        </div>
      </div>
    </main>
  );


}
