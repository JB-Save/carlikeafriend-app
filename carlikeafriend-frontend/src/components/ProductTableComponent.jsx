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
            <table className="table table-hover table-striped table-custom align-middle">
                <thead>
                    <tr>
                        <th scope="col" style={{ width: '10%' }}>ID</th>
                        <th scope="col" style={{ width: '60%' }}>Nombre</th>
                        <th className="text-center" scope="col" style={{ width: '30%' }}>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    {products.map((product) => (
                        <tr key={product.id}>
                            <th scope="row">{product.id}</th>
                            <td>{product.name}</td>
                            <td className="text-center">
                                <div className="d-inline-flex gap-2">
                                    <button
                                        className='btn btn-danger btn-sm rounded-3'
                                        onClick={() => setProductIdToDelete(product.id)}
                                    ><i className="bi bi-trash me-1"></i>
                                        Eliminar
                                    </button>
                                    <button
                                        className='btn btn-success btn-sm rounded-3'
                                        onClick={() => handleEditClick(product)}
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
