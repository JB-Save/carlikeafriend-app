import { useNavigate } from "react-router-dom";

export const UserTableComponent = ({ users, setUserIdToDelete }) => {

    const navigate = useNavigate(); // Este hook te permite obtener una función para navegar entre rutas

    //Creamos una función que recibe el usuario como argumento
    const handleEditClick = (user) => {
        // Navega a la ruta 'add-user' y pasa el objeto 'user' como estado
        navigate('/administration/add-user', { replace: true, state: { userToEdit: user } });
    };


    return (
        <div className="table-responsive">
            <table className="table table-hover table-striped table-custom">
                <thead>
                    <tr>
                        <th scope="col">ID</th>
                        <th scope="col">Nombre</th>
                        <th scope="col">Email</th>
                        <th className="text-center" scope="col">Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    {users.map((user) => (
                        <tr key={user.id}>
                            <th scope="row">{user.id}</th>
                            <td>{`${user.name} ${user.lastName}`}</td>
                            <td>{user.email}</td>
                            <td className="d-flex flex-row justify-content-center gap-3">
                                <button
                                    className='btn btn-danger btn-sm rounded-3'
                                    onClick={() => setUserIdToDelete(user.id)}
                                >
                                    Eliminar
                                </button>
                                <button
                                    className='btn btn-success btn-sm rounded-3'
                                    onClick={() => handleEditClick(user)}
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
