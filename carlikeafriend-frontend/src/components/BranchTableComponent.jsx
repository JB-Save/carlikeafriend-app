import { useNavigate } from "react-router-dom";

export const BranchTableComponent = ({ branches, setBranchIdToDelete }) => {

    const navigate = useNavigate(); // Este hook te permite obtener una función para navegar entre rutas

    //Creamos una función que recibe la sucursal como argumento
    const handleEditClick = (branch) => {
        // Navega a la ruta 'add-branch' y pasa el objeto 'branch' como estado
        navigate('/administration/add-branch', { replace: true, state: { branchToEdit: branch } });
    };

    return (
        <div className="table-responsive">
            <table className="table table-hover table-striped table-custom align-middle">
                <thead>
                    <tr>
                        <th scope="col" style={{ width: '10%' }}>ID</th>
                        <th scope="col" style={{ width: '60%' }}>Nombre</th>
                        <th className="text-center" scope="col" style={{ width: '30%' }}>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    {branches.map((branch) => (
                        <tr key={branch.id}>
                            <th scope="row">{branch.id}</th>
                            <td>{branch.name}</td>
                            <td className="d-flex flex-row justify-content-center gap-3">
                                <div className="d-inline-flex gap-2">
                                    <button
                                        className='btn btn-danger btn-sm rounded-3'
                                        onClick={() => setBranchIdToDelete(branch.id)}
                                    ><i className="bi bi-trash me-1"></i>
                                        Eliminar
                                    </button>
                                    <button
                                        className='btn btn-success btn-sm rounded-3'
                                        onClick={() => handleEditClick(branch)}
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
