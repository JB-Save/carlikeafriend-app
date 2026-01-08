import { useSignInForm } from "../hooks/useSignInForm"

export const SignInForm = () => {
    const {
        userData,
        error,
        isLoading,
        handleChange,
        handleSubmit } = useSignInForm();

    return (

        <div col="row">
            <div className="min-vh-100 d-flex align-items-center justifiy-content-center">
                <div className="container">
                <div className="col-12 col-md-6 mx-auto">
                    <div className="card card-shadow card-background p-2 mx-2">
                        <h1 className="fs-3 fw-bold text-center form-title mb-2">
                            Iniciar Sesión
                        </h1>
                        <form onSubmit={handleSubmit} className="container-fluid py-4">
                            <div className="mb-3">
                                <label htmlFor="email" className="form-label">Email</label>
                                <div className="input-group">
                                    <span className="input-group-text" id="inputGroupPrepend2">@</span>
                                    <input
                                        type="email"
                                        id="email"
                                        name="email"
                                        value={userData.email}
                                        onChange={handleChange}
                                        className="form-control"
                                        aria-describedby="inputGroupPrepend2"
                                        placeholder="name@example.com"
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
                                    value={userData.password}
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
