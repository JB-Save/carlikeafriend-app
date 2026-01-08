import { useNavigate } from "react-router-dom";

export const FeatureTableComponent = ({ features, setFeatureIdToDelete }) => {

    const navigate = useNavigate(); // Este hook te permite obtener una función para navegar entre rutas

    //Creamos una función que recibe la característica como argumento
    const handleEditClick = (feature) => {
        // Navega a la ruta 'add-feature' y pasa el objeto 'feature' como estado
        navigate('/administration/add-feature', { replace: true, state: { featureToEdit: feature } });
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
                    {features.map((feature) => (
                        <tr key={feature.id}>
                            <th scope="row">{feature.id}</th>
                            <td>{feature.name}</td>
                            <td className="d-flex flex-row justify-content-center gap-3">
                                <button
                                    className='btn btn-danger btn-sm rounded-3'
                                    onClick={() => setFeatureIdToDelete(feature.id)}
                                >
                                    Eliminar
                                </button>
                                <button
                                    className='btn btn-success btn-sm rounded-3'
                                    onClick={() => handleEditClick(feature)}
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
