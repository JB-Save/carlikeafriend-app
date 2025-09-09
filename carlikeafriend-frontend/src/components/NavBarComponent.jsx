import { NavLink } from "react-router-dom"
import '../styles/NavBarComponent.css';

export const NavBarComponent = () => {
    return (

        <header className="navbar navbar-expand-lg header-color fixed-top py-3">  {/* Encabezado fijo y responsivo con Bootstrap */}
            <div className="container-fluid">
                { /* Bloque del logotipo y lema (alineado a la izquierda) */}
                <NavLink to="/" className="navbar-brand d-flex align-items-center me-auto" >
                    <img src="/src/assets/logo.png" alt="Logo de CarLikeAFriend" className="header-logo-img rounded me-2 img-fluid img-thumbnail" />
                    <div>
                        <h1 className="h5 mb-0 header-logo-text">Car Like A Friend</h1>
                        <p className="mb-0 header-slogan-text d-none d-sm-block">Tu amigo en el viaje</p> {/*Oculto en móviles pequeños */}
                    </div>
                </NavLink>

                {/* Bloque de botones (alineado a la derecha) */}
                <div className="d-flex align-items-center">
                    <button className="btn header-btn me-2 rounded-lg" type="button">
                        Crear cuenta
                    </button>
                    <button className="btn header-btn rounded-lg" type="button">
                        Iniciar sesión
                    </button>
                </div>
            </div>
        </header>

    )
}