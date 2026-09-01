import { useNavigate } from "react-router-dom";

export const BranchAddonTableComponent = ({ inventory }) => {

    const navigate = useNavigate();

    // Cuando damos clic en editar, enviamos la data pre-cargada al formulario
    const handleEditClick = (itemToEdit) => {
        navigate('/administration/assign-addon', { replace: true, state: { addonToEdit: itemToEdit } });
    };

    return (
        <div className="table-responsive">
            <table className="table table-hover table-striped table-custom align-middle">
                <thead>
                    <tr>
                        <th scope="col" style={{ width: '10%' }}>ID Extra</th>
                        <th scope="col" style={{ width: '50%' }}>Nombre del Extra</th>
                        <th className="text-center" scope="col" style={{ width: '10%' }}>Stock Físico Total</th>
                        <th className="text-center" scope="col" style={{ width: '30%' }}>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    {inventory.map((item) => (
                        <tr key={item.addonId}>
                            <th scope="row">{item.addonId}</th>
                            <td>{item.addonName}</td>
                            <td className="text-center fw-bold">
                                {/* Resaltar visualmente si el stock está en 0 */}
                                <span className={item.totalStock === 0 ? "text-danger" : ""}>
                                    {item.totalStock}
                                </span>
                            </td>
                            <td className="text-center">
                                <div className="d-inline-flex gap-2">
                                    <button
                                        className='btn btn-success btn-sm rounded-3'
                                        onClick={() => handleEditClick(item)}
                                    >
                                        <i className="bi bi-pencil-square me-1"></i> Editar Stock
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