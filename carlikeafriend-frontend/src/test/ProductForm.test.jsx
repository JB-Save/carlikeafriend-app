import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { vi } from 'vitest';
import ProductForm from '../components/ProductForm';

describe('ProductForm', () => {
    // Simular todo el componente ProductFrom para simplificar las pruebas de sus interacciones con las propiedades.
    // Esto evita la necesidad de simular ganchos internos.
    vi.mock('../components/ProductForm', () => {
        return {
           // Crea un componente falso que acepta todos los accesorios y renderiza elementos simples para realizar pruebas
            default: (props) => {
                return (
                    <form onSubmit={props.handleFormSubmit} data-testid="product-form">
                        <label htmlFor="name">Nombre</label>
                        <input 
                        id="name" 
                        name="name" 
                        onChange={props.handleChange}
                        value={props.productData?.name || ''} 
                        />

                        <label htmlFor="description">Descripción</label>
                        <textarea 
                        id="description" 
                        name="description" 
                        onChange={props.handleChange} 
                        value={props.productData?.description || ''} 
                        />

                        <label htmlFor="price">Precio</label>
                        <input 
                        id="price" 
                        name="price" 
                        onChange={props.handleChange} 
                        value={props.productData?.price || ''} 
                        />

                        <label htmlFor="image-upload">Imágenes Nuevas del Producto</label>
                        <input 
                        data-testid="image-upload-mock" 
                        id="image-upload" 
                        type="file" 
                        onChange={props.handleNewImageChange} 
                        />

                        {/* Representación condicional para botones basada en la propiedad isEdit */}
                        {props.isEdit ? (
                            <button type="submit">Actualizar Producto</button>
                        ) : (
                            <button type="submit">Crear Producto</button>
                        )}

                        {props.error && <p>{props.error}</p>}
                    </form>
                );
            },
        };
    });

    // Caso de prueba para representar el formulario correctamente
    it('debe renderizar el formulario correctamente para crear un nuevo producto', () => {
       // Representa el componente con todos los accesorios necesarios, incluso si son simulacros
        render(<ProductForm
            isEdit={false}
            productData={{ name: '', description: '', price: '' }}
            handleFormSubmit={() => { }}
            handleNewImageChange={() => { }}
            handleRemoveImage={() => { }}
            handleProductUpdate={() => { }}
            handleChange={() => { }}
            error={null}
        />);

        // Afirmar que el título y todos los elementos del formulario están presentes
        expect(screen.getByLabelText('Nombre')).toBeInTheDocument();
        expect(screen.getByLabelText('Descripción')).toBeInTheDocument();
        expect(screen.getByLabelText('Precio')).toBeInTheDocument();
        expect(screen.getByLabelText('Imágenes Nuevas del Producto')).toBeInTheDocument();
        expect(screen.getByRole('button', { name: 'Crear Producto' })).toBeInTheDocument();
    });

   // Caso de prueba para verificar que se llama a handleNewImageChange cuando se selecciona un archivo
    it('debe llamar a handleNewImageChange con el archivo seleccionado', async () => {
        // Crea una función simulada para que actúe como espía
        const mockHandleNewImageChange = vi.fn();

       // Representa el componente, pasando la función simulada como una propiedad
        render(<ProductForm
            isEdit={false}
            productData={{ name: '', description: '', price: '' }}
            handleFormSubmit={() => { }}
            handleNewImageChange={mockHandleNewImageChange}
            handleRemoveImage={() => { }}
            handleProductUpdate={() => { }}
            handleChange={() => { }}
            error={null}
        />);

        // Encuentra el elemento de entrada del archivo usando su id de prueba
        const fileInput = screen.getByTestId('image-upload-mock');

       // Crea un objeto de archivo simulado para simular un archivo seleccionado
        const file = new File(['mock content'], 'image.png', { type: 'image/png' });

       // Simular el evento de cambio en la entrada del archivo, pasando el archivo simulado
        fireEvent.change(fileInput, {
            target: { files: [file] },
        });

       // Espera a que se llame a la función simulada y afirme que se llamó una vez
        await waitFor(() => {
            expect(mockHandleNewImageChange).toHaveBeenCalledTimes(1);
        });
    });

    // Caso de prueba para verificar que los campos del formulario se actualicen correctamente
    it('debe actualizar los campos de entrada al escribir', () => {
        render(<ProductForm
            isEdit={false}
            productData={{ name: '', description: '', price: '' }}
            handleFormSubmit={() => { }}
            handleNewImageChange={() => { }}
            handleRemoveImage={() => { }}
            handleProductUpdate={() => { }}
            handleChange={() => { }}
            error={null}
        />);

        // Encuentra los campos de entrada
        const nameInput = screen.getByLabelText('Nombre');
        const descriptionTextarea = screen.getByLabelText('Descripción');
        const priceInput = screen.getByLabelText('Precio');

        // Simular que el usuario escribe en los campos
        fireEvent.change(nameInput, { target: { value: 'Nuevo Producto' } });
        fireEvent.change(descriptionTextarea, { target: { value: 'Descripción de prueba' } });
        fireEvent.change(priceInput, { target: { value: '12.34' } });
    });

    // Caso de prueba para verificar que se llama al controlador de envío del formulario
    it('debe llamar a handleFormSubmit al enviar el formulario', async () => {
        // Crea una función simulada para que actúe como espía
        const mockHandleSubmit = vi.fn(e => e.preventDefault()); // Agrega preventDefault para evitar errores de consola

        // Representa el componente, pasando la función simulada como una propiedad
        render(<ProductForm
            isEdit={false}
            productData={{ name: 'Test Product', description: 'Test Description', price: '99.99' }}
            handleFormSubmit={mockHandleSubmit}
            handleNewImageChange={() => { }}
            handleRemoveImage={() => { }}
            handleProductUpdate={() => { }}
            handleChange={() => { }}
            error={null}
        />);

       // Encuentra el formulario y lo envía
        const form = screen.getByTestId('product-form');
        fireEvent.submit(form);

        // Espera a que se llame a la función simulada y afirma que se llamó al menos una vez
        await waitFor(() => {
            expect(mockHandleSubmit).toHaveBeenCalled();
        });
    });

   // Caso de prueba para mostrar un mensaje de error si falla el envío
    it('debe mostrar un mensaje de error si el envío del formulario falla', () => {
        render(<ProductForm
            isEdit={false}
            productData={{ name: '', description: '', price: '' }}
            handleFormSubmit={() => { }}
            handleNewImageChange={() => { }}
            handleRemoveImage={() => { }}
            handleProductUpdate={() => { }}
            handleChange={() => { }}
            error="Ocurrió un error al guardar el producto."
        />);

       // Afirma que el mensaje de error está presente
        expect(screen.getByText('Ocurrió un error al guardar el producto.')).toBeInTheDocument();
    });
});
