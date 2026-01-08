import { useNavigate } from "react-router-dom";

export const PermissionTableComponent = ({ permissions, setPermissionIdToDelete }) => {

    const navigate = useNavigate(); // Este hook te permite obtener una función para navegar entre rutas

    //Creamos una función que recibe el permiso como argumento
    const handleEditClick = (permission) => {
        // Navega a la ruta 'add-permission' y pasa el objeto 'permission' como estado
        navigate('/administration/add-permission', { replace: true, state: { permissionToEdit: permission } });
    };


    return (
        <div className="table-responsive">
            <table className="table table-hover table-striped table-custom">
                <thead>
                    <tr>
                        <th scope="col">ID</th>
                        <th scope="col">Nombre</th>
                        <th className="text-center" scope="col">Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    {permissions.map((permission) => (
                        <tr key={permission.id}>
                            <th scope="row">{permission.id}</th>
                            <td>{permission.name}</td>
                            <td className="d-flex flex-row justify-content-center gap-3">
                                <button
                                    className='btn btn-danger btn-sm rounded-3'
                                    onClick={() => setPermissionIdToDelete(permission.id)}
                                >
                                    Eliminar
                                </button>
                                <button
                                    className='btn btn-success btn-sm rounded-3'
                                    onClick={() => handleEditClick(permission)}
                                >
                                    Editar
                                </button>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );


}
