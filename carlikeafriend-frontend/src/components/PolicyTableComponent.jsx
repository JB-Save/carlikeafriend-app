import { useNavigate } from "react-router-dom";

export const PolicyTableComponent = ({ policies, setPolicyIdToDelete }) => {

    const navigate = useNavigate(); // Este hook te permite obtener una función para navegar entre rutas

    //Creamos una función que recibe la política como argumento
    const handleEditClick = (policy) => {
        // Navega a la ruta 'add-policy' y pasa el objeto 'policy' como estado
        navigate('/administration/add-policy', { replace: true, state: { policyToEdit: policy } });
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
                    {policies.map((policy) => (
                        <tr key={policy.id}>
                            <th scope="row">{policy.id}</th>
                            <td>{policy.name}</td>
                            <td className="text-center">
                                <div className="d-inline-flex gap-2">
                                    <button
                                        className='btn btn-danger btn-sm rounded-3'
                                        onClick={() => setPolicyIdToDelete(policy.id)}
                                    ><i className="bi bi-trash me-1"></i>
                                        Eliminar
                                    </button>
                                    <button
                                        className='btn btn-success btn-sm rounded-3'
                                        onClick={() => handleEditClick(policy)}
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
