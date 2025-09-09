import '../styles/FooterStyle.css'
import { NavLink } from 'react-router-dom'

export const FooterComponent = () => {

  const currentYear = new Date().getFullYear();

  return (
    <footer className="footer mt-auto">
      <div className="container-fluid">
        <div className="d-flex flex-column flex-sm-row align-items-center justify-content-between">
          {/* Bloque alineado a la izquierda */}
          <div className="d-flex align-items-center justify-content-center justify-content-md-start">
            <img src="/src/assets/logo.png" alt="Logo de CarLikeAFriend" className="footer-logo-img rounded me-2 img-fluid img-thumbnail" />
            <div>
              <span className="footer-logo-text h6 mb-0">Car Like A Friend</span>
              <span className="footer-slogan-text d-block text-sm">Tu amigo en el viaje</span>
            </div>
            <span className="ms-3 text-copy-right d-none d-sm-block text-sm">&copy; <span>{currentYear}</span> Car Like A Friend. Todos los derechos reservados.</span>
          </div>

          {/* Iconos de redes sociales alineados a la derecha */}
          <div className="d-flex align-items-center social-icons">
            <NavLink to="https://facebook.com" target="_blank" className="me-3">
              <i className="bi bi-facebook"></i>
            </NavLink>
            <NavLink to="https://linkedin.com" target="_blank" className="me-3">
              <i className="bi bi-linkedin"></i>
            </NavLink>
            <NavLink to="https://twitter.com" target="_blank" className="me-3">
              <i className="bi bi-twitter-x"></i>
            </NavLink>
            <NavLink to="https://instagram.com" target="_blank">
              <i className="bi bi-instagram"></i>
            </NavLink>
          </div>
        </div>
        <div className="d-block d-sm-none text-center text-copy-right mt-2 text-sm">
          &copy; <span>{currentYear}</span> Car Like A Friend. Todos los derechos reservados.
        </div>
      </div>
    </footer>
  )
}
