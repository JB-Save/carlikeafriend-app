import { useNavigate } from "react-router-dom";
import { useSignUpForm } from "../hooks/useSignUpForm"
import { useForm } from "react-hook-form";
import { yupResolver } from "@hookform/resolvers/yup";
import { signUpSchema } from "../utils/validationSchema";

export const SignUpForm = () => {

    const navigate = useNavigate(); // <-- Usa useNavigate para la navegación
    //Creamos una función que recibe el email del usuario como argumento
    const onUserSignedUp = (email) => {
        // Navega a la ruta 'successful-registration' y pasa la variable 'email' como estado
        navigate('/successful-registration', { replace: true, state: { emailUser: email } });
    };

    const {
        error: apiError,
        isSubmittingForm: isLoading,
        submitSignUpData
    } = useSignUpForm(onUserSignedUp);

    const { register, handleSubmit, formState: { errors } } = useForm({
        resolver: yupResolver(signUpSchema),
        mode: 'onTouched'
    });


    return (

        <div col="row">
            <div className="min-vh-100 d-flex align-items-center justifiy-content-center">
                <div className="container">
                    <div className="col-12 col-md-6 mx-auto">
                        <div className="card card-shadow card-background p-2 mx-2">
                            <h1 className="fs-3 fw-bold text-center form-title mb-2">
                                Crear Cuenta
                            </h1>
                            <form onSubmit={handleSubmit(submitSignUpData)} className="container-fluid py-4">
                                <div className="mb-3">
                                    <label htmlFor="name" className="form-label">Nombre</label>
                                    <input
                                        type="text"
                                        id="name"
                                        className={`form-control ${errors.name ? 'is-invalid' : ''}`}
                                        {...register('name')}
                                        disabled={isLoading}
                                    />
                                    {errors.name && <div className="invalid-feedback">{errors.name.message}</div>}
                                </div>
                                <div className="mb-3">
                                    <label htmlFor="lastName" className="form-label">Apellido</label>
                                    <input
                                        type="text"
                                        id="lastName"
                                        className={`form-control ${errors.lastName ? 'is-invalid' : ''}`}
                                        {...register('lastName')}
                                        disabled={isLoading}
                                    />
                                    {errors.lastName && <div className="invalid-feedback">{errors.lastName.message}</div>}
                                </div>
                                <div className="mb-3">
                                    <label htmlFor="email" className="form-label">Email</label>
                                    <div className="input-group">
                                        <span className="input-group-text" id="inputGroupPrepend2">@</span>
                                        <input
                                            type="email"
                                            id="email"
                                            className={`form-control ${errors.email ? 'is-invalid' : ''}`}
                                            aria-describedby="inputGroupPrepend2"
                                            placeholder="usuario@dominio.com"
                                            autoComplete="off"
                                            {...register('email')}
                                            disabled={isLoading}
                                        />
                                        {errors.email && <div className="invalid-feedback">{errors.email.message}</div>}
                                    </div>
                                </div>
                                <div className="mb-3">
                                    <label htmlFor="password" className="form-label">Password</label>
                                    <input
                                        type="password"
                                        id="password"
                                        className={`form-control ${errors.password ? 'is-invalid' : ''}`}
                                        {...register('password')}
                                        disabled={isLoading}
                                    />
                                    {errors.password && <div className="invalid-feedback">{errors.password.message}</div>}
                                </div>

                                {apiError && (
                                    <div className="alert alert-danger shadow-sm" role="alert">
                                        <strong><i className="bi bi-exclamation-triangle me-2"></i>¡Error!</strong> {apiError}
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
