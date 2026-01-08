
export const DeleteConfirmationModalComponent = ({ id, deleteFunction, onClose, objectName, isDeleting }) => {

    if (!id) {
        //Si no hay id no renderiza el modal
        return null;
    }


    //Modal de Confirmación de Eliminación 
    return (
        <div className="confirm-modal" style={{ display: 'flex' }}>
            <div className="confirm-modal-content">
                {!isDeleting && <span className="confirm-modal-close" onClick={onClose}>&times;</span>}
                <p>¿Estás seguro de que quieres eliminar {objectName} con ID: {id}?</p>
                <div className="d-flex justify-content-center mt-4">
                    <button
                        className="btn btn-danger me-3"
                        onClick={() => deleteFunction(id)}
                        disabled={isDeleting}
                    >
                        {isDeleting ? 'Eliminando...' : 'Eliminar'}
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
