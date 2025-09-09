
export const DeleteConfirmationModalComponent = ({ id, deleteFunction, onClose }) => {

    if (!id) {
        //Si no hay id no renderiza el modal
        return null;
    }

    const manageOnDelete = () => {
        deleteFunction(id);
        onClose();
    }

/*
const manageOnDelete = (e) => {
        // Detener la propagación del evento para que no afecte a otros elementos
        e.stopPropagation();
        deleteFunction(id);
        onClose();
    }
*/
     /*<div className="confirm-modal" onClick={onClose} style={{ display: 'flex' }}>
            <div className="confirm-modal-content" onClick={(e) => e.stopPropagation()}>
                <span className="confirm-modal-close" onClick={(e) => {
                    e.stopPropagation();
                    onClose();
                }}>&times;</span>
                <p>¿Estás seguro de que quieres eliminar este producto?</p>
                <div className="d-flex justify-content-center mt-4">
                    <button
                        className="btn btn-danger me-3"
                        onClick={manageOnDelete}
                    >
                        Eliminar
                    </button>
                    <button
                        className="btn btn-secondary"
                        onClick={(e) => {
                            e.stopPropagation();
                            onClose();
                        }}
                    >
                        Cancelar
                    </button>
                </div>
            </div>
        </div>*/


     /*
       
        */

    //Modal de Confirmación de Eliminación 
    return (
     <div className="confirm-modal" style={{ display: 'flex' }}>
            <div className="confirm-modal-content">
                <span className="confirm-modal-close" onClick={onClose}>&times;</span>
                <p>¿Estás seguro de que quieres eliminar este producto?</p>
                <div className="d-flex justify-content-center mt-4">
                    <button className="btn btn-danger me-3" onClick={() => manageOnDelete()}>Eliminar</button>
                    <button className="btn btn-secondary" onClick={onClose}>Cancelar</button>
                </div>
            </div>
        </div>          
    )
}
