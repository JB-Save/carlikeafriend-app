import { render, screen, fireEvent } from '@testing-library/react';
import { vi } from 'vitest';
import { DeleteConfirmationModalComponent } from '../components/DeleteConfirmationModalComponent';

describe('DeleteConfirmationModalComponent', () => {
  // Mock del ID y las funciones de callback.
  const mockId = '123';
  const mockDeleteFunction = vi.fn();
  const mockOnClose = vi.fn();
  const mockOnClose2 = vi.fn();
  const mockDeleteFunction2 = vi.fn();

  // Caso de prueba para verificar que el modal se renderiza cuando se le proporciona un ID.
  it('debe renderizar el modal de confirmación cuando se le pasa un ID', () => {
    render(
      <DeleteConfirmationModalComponent
        id={mockId}
        deleteFunction={mockDeleteFunction}
        onClose={mockOnClose}
      />
    );

    // Asegura que el texto del modal esté en el documento.
    expect(screen.getByText('¿Estás seguro de que quieres eliminar este producto?')).toBeInTheDocument();
    // Asegura que los botones 'Eliminar' y 'Cancelar' estén en el documento.
    expect(screen.getByRole('button', { name: 'Eliminar' })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Cancelar' })).toBeInTheDocument();
  });

  // Caso de prueba para verificar que el modal no se renderiza si no se le da un ID.
  it('no debe renderizar el modal si el ID es nulo', () => {
    const { container } = render(
      <DeleteConfirmationModalComponent
        id={null}
        deleteFunction={mockDeleteFunction}
        onClose={mockOnClose}
      />
    );

    // Asegura que el componente no se renderice en absoluto.
    expect(container).toBeEmptyDOMElement();
  });

  // Caso de prueba para verificar que el botón 'Eliminar' llama a las funciones correctas.
  it('debe llamar a deleteFunction y onClose cuando se hace clic en "Eliminar"', () => {
    render(
      <DeleteConfirmationModalComponent
        id={mockId}
        deleteFunction={mockDeleteFunction}
        onClose={mockOnClose}
      />
    );

    // Simula un clic en el botón 'Eliminar'.
    fireEvent.click(screen.getByRole('button', { name: 'Eliminar' }));

    // Asegura que las funciones mockeadas se hayan llamado correctamente.
    expect(mockDeleteFunction).toHaveBeenCalledTimes(1);
    expect(mockDeleteFunction).toHaveBeenCalledWith(mockId);
    expect(mockOnClose).toHaveBeenCalledTimes(1);
  });

  
  // Caso de prueba para verificar que el botón 'Cancelar' solo llama a onClose.
  it('solo debe llamar a onClose cuando se hace clic en "Cancelar"', () => {
    render(
      <DeleteConfirmationModalComponent
        id={mockId}
        deleteFunction={mockDeleteFunction2}
        onClose={mockOnClose2}
      />
    );
    
    // Simula un clic en el botón 'Cancelar'.
    fireEvent.click(screen.getByRole('button', { name: 'Cancelar' }));

    // Asegura que solo la función onClose haya sido llamada.
    expect(mockOnClose2).toHaveBeenCalledTimes(1);
    expect(mockDeleteFunction2).not.toHaveBeenCalled();
  });
  
});
