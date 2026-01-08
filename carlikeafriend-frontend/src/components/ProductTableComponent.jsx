import { useNavigate } from "react-router-dom";

export const ProductTableComponent = ({ products, setProductIdToDelete }) => {

    const navigate = useNavigate(); // Este hook te permite obtener una función para navegar entre rutas

    //Creamos una función que recibe el producto como argumento
    const handleEditClick = (product) => {
        // Navega a la ruta 'add-product' y pasa el objeto 'product' como estado
        navigate('/administration/add-product', { replace: true, state: { productToEdit: product } });
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
                    {products.map((product) => (
                        <tr key={product.id}>
                            <th scope="row">{product.id}</th>
                            <td>{product.name}</td>
                            <td className="d-flex flex-row justify-content-center gap-3">
                                <button
                                    className='btn btn-danger btn-sm rounded-3'
                                    onClick={() => setProductIdToDelete(product.id)}
                                >
                                    Eliminar
                                </button>
                                <button
                                    className='btn btn-success btn-sm rounded-3'
                                    onClick={() => handleEditClick(product)}
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
