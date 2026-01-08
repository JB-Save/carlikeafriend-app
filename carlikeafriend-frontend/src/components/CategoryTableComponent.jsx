import { useNavigate } from "react-router-dom";

export const CategoryTableComponent = ({ categories, setCategoryIdToDelete }) => {

    const navigate = useNavigate(); // Este hook te permite obtener una función para navegar entre rutas

    //Creamos una función que recibe la categoría como argumento
    const handleEditClick = (category) => {
        // Navega a la ruta 'add-category' y pasa el objeto 'category' como estado
        navigate('/administration/add-category', { replace: true, state: { categoryToEdit: category } });
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
                    {categories.map((category) => (
                        <tr key={category.id}>
                            <th scope="row">{category.id}</th>
                            <td>{category.name}</td>
                            <td className="d-flex flex-row justify-content-center gap-3">
                                <button
                                    className='btn btn-danger btn-sm rounded-3'
                                    onClick={() => setCategoryIdToDelete(category.id)}
                                >
                                    Eliminar
                                </button>
                                <button
                                    className='btn btn-success btn-sm rounded-3'
                                    onClick={() => handleEditClick(category)}
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
