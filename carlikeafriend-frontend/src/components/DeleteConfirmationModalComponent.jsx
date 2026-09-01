
export const DeleteConfirmationModalComponent = ({
    // props para uso clásico, cuando se necesita pasar el id.
    id,
    deleteFunction,
    onClose,
    objectName,
    isDeleting,
    // props para uso sin id.
    show = false,
    customMessage,
    customButtonText
}) => {

    // Evalúa si hay un ID (uso clásico) o si se fuerza su renderizado con 'show'
    if (!id && !show) {
        return null;
    }

    // Si pasamos un mensaje personalizado, lo usa. Si no, usa el mensaje clásico.
    const textMessage = customMessage || `¿Estás seguro de que quieres eliminar ${objectName} con ID: ${id}?`;

    // Lo mismo para el texto del botón
    const buttonText = customButtonText
        ? (isDeleting ? 'Procesando...' : customButtonText)
        : (isDeleting ? 'Eliminando...' : 'Eliminar');

    // Si hay id lo pasa, si no, ejecuta la función sin argumentos
    const handleAction = () => id ? deleteFunction(id) : deleteFunction();

    return (
        <div className="confirm-modal" style={{ display: 'flex' }}>
            <div className="confirm-modal-content">
                {!isDeleting && <span className="confirm-modal-close" onClick={onClose}>&times;</span>}
                <p className="mt-3" style={{ textAlign: 'justify' }}>{textMessage}</p>
                <div className="d-flex justify-content-center mt-4">
                    <button
                        className="btn btn-danger me-3"
                        onClick={handleAction}
                        disabled={isDeleting}
                    >
                        {buttonText}
                    </button>
                    <button
                        className="btn btn-secondary"
                        onClick={onClose}
                        disabled={isDeleting}
                    >
                        Cancelar
                    </button>
                </div>
            </div>
        </div>
    )
}