import { Link, NavLink, useNavigate } from "react-router-dom"
import { useContext, useState } from "react";
import { UserContext } from "../context/UserContext";
import '../styles/NavBarComponent.css';

export const NavBarComponent = () => {
    const navigate = useNavigate();
    const { isAuthenticated, user, logout } = useContext(UserContext); // Obtener estado y funciones
    const [allowedRoles, setAllowedRoles] = useState(["ADMIN"]);


    const handleCreateAcount = () => {
        navigate("/signup");
    }

    const handleLogin = () => {
        navigate("/signin");
    }

    const handleLogout = () => {
        logout(); // Llama a la función de cerrar sesión del contexto
        navigate("/"); // Redirige a la página de inicio
    }

    const firstName = (user) => {
        let firstName = '';
        let name = user.name.split(' ');
        if (name.length > 0) {
            firstName = name[0].charAt(0).toUpperCase() + name[0].slice(1).toLowerCase();
        }
        return firstName;
    }

    const firstLastName = (user) => {
        let firstLastName = '';
        let lastName = user.lastName.split(' ');
        if (lastName.length > 0) {
            firstLastName = lastName[0].charAt(0).toUpperCase() + lastName[0].slice(1).toLowerCase();
        }
        return firstLastName;
    }

    const initials = (user) => {
        let initials = '';
        if (user.name.length > 0 && user.lastName.length > 0) {
            initials = user.name.charAt(0).toUpperCase() + user.lastName.charAt(0).toUpperCase();
        }
        return initials;
    }

    const hasAuthority = (authorities) => {
        const userRoles = user.roles.map(role => role.name);
        return userRoles.some(userRole => authorities.includes(userRole));
    }

    return (

        <header className="navbar navbar-expand-lg header-color fixed-top py-3">  {/* Encabezado fijo y responsivo con Bootstrap */}
            <div className="container-fluid d-flex flex-column flex-md-row">
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
                    {isAuthenticated ? (

                        <li className="nav-item dropdown list-unstyled ">
                            <div className="nav-link background dropdown-toggle d-flex align-items-center p-0" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                                <div className="rounded-circle header-btn d-flex align-items-center justify-content-center" style={{ width: '40px', height: '40px', fontSize: '1.25rem' }}>
                                    <span>{initials(user)}</span>
                                </div>
                            </div>
                            <ul className="dropdown-menu dropdown-menu-end dropdown-background-color mt-5" aria-labelledby="navbarDropdownMenuLink">
                                <div className="d-flex align-items-center px-3 pt-2">
                                    <div className="rounded-circle header-btn d-flex align-items-center justify-content-center py-2 px-3" style={{ width: '40px', height: '40px', fontSize: '1.25rem' }}>
                                        <span>{initials(user)}</span>
                                    </div>
                                    <div className="ms-2" >
                                        <div className="dropdown-text text-start">
                                            <span className="fw-bold">
                                                {`
                                        ${firstName(user)} 
                                        ${firstLastName(user)}
                                        `}
                                            </span>
                                        </div>
                                        <div className="dropdown-text-muted text-break text-start small">{user.userName.toLowerCase()}</div>
                                    </div>
                                </div>
                                <li >
                                    <hr className="dropdown-divider" />
                                </li>

                                {hasAuthority(allowedRoles) && (
                                    <>
                                        <li>
                                            <Link className="dropdown-item dropdown-text" to="/administration">
                                                Administración
                                            </Link>
                                        </li>
                                        <li>
                                            <hr className="dropdown-divider" />
                                        </li>

                                    </>
                                )}

                                <li>
                                    <Link className="dropdown-item dropdown-text" to="/profile">
                                        Ver Perfil
                                    </Link>
                                </li>
                                <li>
                                    <hr className="dropdown-divider" />
                                </li>
                                <li>
                                    <Link className="dropdown-item dropdown-text" onClick={handleLogout}>
                                        Cerrar Sesión
                                    </Link>
                                </li>

                            </ul>
                        </li>
                    ) : (
                        <>
                            {/* Botones para usuario no logueado */}
                            < button
                                type="button"
                                onClick={handleCreateAcount}
                                className="btn header-btn me-2 rounded-3"
                            >
                                Crear cuenta
                            </button>
                            <button
                                type="button"
                                onClick={handleLogin}
                                className="btn header-btn rounded-3"
                            >
                                Iniciar sesión
                            </button>
                        </>
                    )}
                </div>
            </div>
        </header >
    )

}