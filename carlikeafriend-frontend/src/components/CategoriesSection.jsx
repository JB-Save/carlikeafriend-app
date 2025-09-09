
export const CategoriesSection = () => {
    return (

        <section className="mb-5"> {/* Sección de Categorías */}
            <h3 className="h4 fw-bold category-text mb-3">Explora por Categorías</h3>
            <div className="row row-cols-2 row-cols-md-3 row-cols-lg-4 g-3">
                <div className="col">
                    <div className="card text-center h-100 card-shadow  card-background rounded-lg">
                        <div className="card-body d-flex flex-column justify-content-center align-items-center">
                            <img src="https://placehold.co/60x60/60A5FA/FFFFFF?text=🚗" alt="Compactos" className="mb-2 w-50" /> 
                            <h5 className="card-title mb-0">Compactos</h5>
                        </div>
                    </div>
                </div>
                <div className="col">
                    <div className="card text-center h-100 card-shadow card-background rounded-lg">
                        <div className="card-body d-flex flex-column justify-content-center align-items-center">
                            <img src="https://placehold.co/60x60/60A5FA/FFFFFF?text=SUV" alt="SUVs" className="mb-2 w-50" /> 
                            <h5 className="card-title mb-0">SUVs</h5>
                        </div>
                    </div>
                </div>
                <div className="col">
                    <div className="card text-center h-100 card-shadow card-background rounded-lg">
                        <div className="card-body d-flex flex-column justify-content-center align-items-center">
                            <img src="https://placehold.co/60x60/60A5FA/FFFFFF?text=🏎️" alt="Deportivos" className="mb-2 w-50" /> 
                            <h5 className="card-title mb-0">Deportivos</h5>
                        </div>
                    </div>
                </div>
                <div className="col">
                    <div className="card text-center h-100 card-shadow card-background rounded-lg">
                        <div className="card-body d-flex flex-column justify-content-center align-items-center">
                            <img src="https://placehold.co/60x60/60A5FA/FFFFFF?text=⚡" alt="Eléctricos" className="mb-2 w-50" /> 
                            <h5 className="card-title mb-0">Eléctricos</h5>
                        </div>
                    </div>
                </div>
                <div className="col">
                    <div className="card text-center h-100 card-shadow card-background rounded-lg">
                        <div className="card-body d-flex flex-column justify-content-center align-items-center">
                            <img src="https://placehold.co/60x60/60A5FA/FFFFFF?text=🚐" alt="Furgonetas" className="mb-2 w-50" /> 
                            <h5 className="card-title mb-0">Furgonetas</h5>
                        </div>
                    </div>
                </div>
                <div className="col">
                    <div className="card text-center h-100 card-shadow card-background rounded-lg">
                        <div className="card-body d-flex flex-column justify-content-center align-items-center">
                            <img src="https://placehold.co/60x60/60A5FA/FFFFFF?text=💎" alt="Lujo" className="mb-2 w-50" /> 
                            <h5 className="card-title mb-0">Lujo</h5>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    )
}
