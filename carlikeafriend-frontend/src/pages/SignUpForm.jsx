import { useNavigate } from "react-router-dom";
import { useSignUpForm } from "../hooks/useSignUpForm"

export const SignUpForm = () => {

    const navigate = useNavigate(); // <-- Usa useNavigate para la navegación
    //Creamos una función que recibe el email del usuario como argumento
    const onUserSignedUp = (email) => {
        // Navega a la ruta 'successful-registration' y pasa el la variable 'email' como estado
        navigate('/successful-registration', { replace: true, state: { emailUser: email } });
    };

    const {
        newUserData,
        error,
        isLoading,
        handleChange,
        handleSubmit } = useSignUpForm(onUserSignedUp);

    return (

        <div col="row">
            <div className="min-vh-100 d-flex align-items-center justifiy-content-center">
                <div className="container">
                    <div className="col-12 col-md-6 mx-auto">
                        <div className="card card-shadow card-background p-2 mx-2">
                            <h1 className="fs-3 fw-bold text-center form-title mb-2">
                                Crear Cuenta
                            </h1>
                            <form onSubmit={handleSubmit} className="container-fluid py-4">
                                <div className="mb-3">
                                    <label htmlFor="name" className="form-label">Nombre</label>
                                    <input
                                        type="text"
                                        id="name"
                                        name="name"
                                        value={newUserData.name}
                                        onChange={handleChange}
                                        className="form-control"
                                        required
                                        disabled={isLoading}
                                    />
                                </div>
                                <div className="mb-3">
                                    <label htmlFor="lastName" className="form-label">Apellido</label>
                                    <input
                                        type="text"
                                        id="lastName"
                                        name="lastName"
                                        value={newUserData.lastName}
                                        onChange={handleChange}
                                        className="form-control"
                                        required
                                        disabled={isLoading}
                                    />

                                </div>
                                <div className="mb-3">
                                    <label htmlFor="email" className="form-label">Email</label>
                                    <div className="input-group">
                                        <span className="input-group-text" id="inputGroupPrepend2">@</span>
                                        <input
                                            type="email"
                                            id="email"
                                            name="email"
                                            value={newUserData.email}
                                            onChange={handleChange}
                                            className="form-control"
                                            aria-describedby="inputGroupPrepend2"
                                            placeholder="name@domain.com"
                                            required
                                            disabled={isLoading}
                                        />
                                    </div>
                                </div>
                                <div className="mb-3">
                                    <label htmlFor="password" className="form-label">Password</label>
                                    <input
                                        type="password"
                                        id="password"
                                        name="password"
                                        value={newUserData.password}
                                        onChange={handleChange}
                                        className="form-control"
                                        required
                                        disabled={isLoading}
                                    />
                                </div>

                                {error && (
                                    <div className="alert alert-danger" role="alert">
                                        <strong>¡Error!</strong> {error}
                                    </div>
                                )}

                                <div className="mb-2">
                                    <button
                                        type="submit"
                                        className="btn header-btn rounded-3"
                                        disabled={isLoading}
                                    >
                                        {isLoading ? 'Guardando...' : 'Registrarse'}
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
}
