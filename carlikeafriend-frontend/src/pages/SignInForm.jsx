import { useForm } from "react-hook-form";
import { useSignInForm } from "../hooks/useSignInForm"
import { signInSchema } from "../utils/validationSchema";
import { yupResolver } from "@hookform/resolvers/yup";

export const SignInForm = () => {
    const {
        error: apiError,
        isSubmittingForm: isLoading,
        submitSignInData
    } = useSignInForm();

    const { register, handleSubmit, formState: { errors } } = useForm({
        resolver: yupResolver(signInSchema),
        mode: 'onTouched'
    });

    return (

        <div col="row">
            <div className="min-vh-100 d-flex align-items-center justifiy-content-center">
                <div className="container">
                    <div className="col-12 col-md-6 mx-auto">
                        <div className="card card-shadow card-background p-2 mx-2">
                            <h1 className="fs-3 fw-bold text-center form-title mb-2">
                                Iniciar Sesión
                            </h1>
                            <form onSubmit={handleSubmit(submitSignInData)} className="container-fluid py-4">
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
                                        {isLoading ? 'Verificando...' : 'Ingresar'}
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
