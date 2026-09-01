import { useNavigate } from "react-router-dom";

export const CityTableComponent = ({ cities, setCityIdToDelete }) => {

    const navigate = useNavigate(); // Este hook te permite obtener una función para navegar entre rutas

    //Creamos una función que recibe la ciudad como argumento
    const handleEditClick = (city) => {
        // Navega a la ruta y pasa el objeto como estado
        navigate('/administration/add-city', { replace: true, state: { cityToEdit: city } });
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
                    {cities.map((city) => (
                        <tr key={city.id}>
                            <th scope="row">{city.id}</th>
                            <td>{city.name}</td>
                            <td className="text-center">
                                <div className="d-inline-flex gap-2">
                                    <button
                                        className='btn btn-danger btn-sm rounded-3'
                                        onClick={() => setCityIdToDelete(city.id)}
                                    ><i className="bi bi-trash me-1"></i>
                                        Eliminar
                                    </button>
                                    <button
                                        className='btn btn-success btn-sm rounded-3'
                                        onClick={() => handleEditClick(city)}
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
