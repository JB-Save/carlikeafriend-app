
export const WelcomeSection = () => {

    return (
        <section className="container-fluid px-0 mb-5">
            <div
                className="container shadow-sm p-5 text-center position-relative overflow-hidden welcome-section-bg"
                style={{ borderBottomLeftRadius: '1.5rem', borderBottomRightRadius: '1.5rem' }}
            >
                <div className="position-absolute top-0 start-0 w-100 h-100 opacity-25 welcome-overlay">
                </div>

                <div className="position-relative py-4" style={{ zIndex: 1 }}>
                    <h2 className="fw-bold display-5 mb-4 text-white">
                        Bienvenido a <span style={{ color: '#70ACDE' }}>Car Like A Friend</span>
                    </h2>
                    <p className="lead mx-auto mb-0 text-light" style={{ maxWidth: '800px', fontWeight: '300' }}>
                        Explora nuestra amplia selección de vehículos disponibles. Encuentra y reserva en la sucursal más cercana tu compañero de viaje perfecto.
                    </p>
                </div>
            </div>
        </section>
    )
}
