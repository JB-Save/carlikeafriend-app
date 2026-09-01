import { useNavigate } from "react-router-dom";

export const TransferFeeTableComponent = ({ transferFees, setTransferFeeIdToDelete }) => {

    const navigate = useNavigate(); // Este hook te permite obtener una función para navegar entre rutas

    //Creamos una función que recibe la tarifa como argumento
    const handleEditClick = (transferFee) => {
        // Navega a la ruta 'add-transferFee' y pasa el objeto 'transferFee' como estado
        navigate('/administration/add-transferFee', { replace: true, state: { transferFeeToEdit: transferFee } });
    };


    return (
        <div className="table-responsive">
            <table className="table table-hover table-striped table-custom align-middle">
                <thead>
                    <tr>
                        <th scope="col" style={{ width: '10%' }}>ID</th>
                        <th scope="col" style={{ width: '50%' }}>Sucursal de Destino</th>
                        <th className="text-center" scope="col" style={{ width: '10%' }}>Tarifa</th>
                        <th className="text-center" scope="col" style={{ width: '30%' }}>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    {transferFees.map((transferFee) => (
                        <tr key={transferFee.id}>
                            <th scope="row">{transferFee.id}</th>
                            <td>{transferFee.destinationBranch.name}</td>
                            <td className="text-center fw-bold">
                                <span className={transferFee.feeAmount === 0 ? "text-danger" : ""}>
                                    {transferFee.feeAmount}
                                </span>
                            </td>
                            <td className="text-center">
                                <div className="d-inline-flex gap-2">
                                    <button
                                        className='btn btn-danger btn-sm rounded-3'
                                        onClick={() => setTransferFeeIdToDelete(transferFee.id)}
                                    ><i className="bi bi-trash me-1"></i>
                                        Eliminar
                                    </button>
                                    <button
                                        className='btn btn-success btn-sm rounded-3'
                                        onClick={() => handleEditClick(transferFee)}
                                    ><i className="bi bi-pencil-square me-1"></i>
                                        Editar
                                    </button>
                                </div>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );


}
