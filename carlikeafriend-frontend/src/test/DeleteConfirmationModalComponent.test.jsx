import userEvent from '@testing-library/user-event';
import { render, screen } from '../utils/test-utils';
import { vi, describe, it, expect } from 'vitest';
import { DeleteConfirmationModalComponent } from '../components/DeleteConfirmationModalComponent';

describe('DeleteConfirmationModalComponent', () => {
  // Mock del ID y las funciones de callback.
  const mockId = '123';
  const mockObjectName = 'este producto';
  const mockDeleteFunction = vi.fn();
  const mockOnClose = vi.fn();

  // Caso de prueba para verificar que el modal se renderiza cuando se le proporciona un ID.
  it('debe renderizar el modal de confirmación cuando se le pasa un ID', () => {
    render(
      <DeleteConfirmationModalComponent
        id={mockId}
        deleteFunction={mockDeleteFunction}
        onClose={mockOnClose}
        objectName={mockObjectName}
        isDeleting={false}
      />
    );

    // Asegura que el texto del modal esté en el documento.
    expect(screen.getByText('¿Estás seguro de que quieres eliminar este producto con ID: 123?')).toBeInTheDocument();
    // Asegura que los botones 'Eliminar' y 'Cancelar' estén en el documento.
    expect(screen.getByRole('button', { name: 'Eliminar' })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Cancelar' })).toBeInTheDocument();
  });

  // Caso de prueba para verificar que el modal no se renderiza si no se le da un ID.
  it('no debe renderizar el modal si el ID es nulo y show es falso', () => {
    const { container } = render(
      <DeleteConfirmationModalComponent
        id={null}
        show={false}
        deleteFunction={mockDeleteFunction}
        onClose={mockOnClose}
        objectName={mockObjectName}
        isDeleting={false}
      />
    );

    // Asegura que el componente no se renderice en absoluto.
    expect(container).toBeEmptyDOMElement();
  });

  // Renderizado forzado mediante la prop 'show' sin ID
  it('debe renderizar el modal si show es verdadero aunque el ID sea nulo', () => {
    render(
      <DeleteConfirmationModalComponent
        id={null}
        show={true}
        customMessage="¿Desea continuar con la acción?"
        deleteFunction={mockDeleteFunction}
        onClose={mockOnClose}
        objectName={mockObjectName}
        isDeleting={false}
      />
    );

    expect(screen.getByText('¿Desea continuar con la acción?')).toBeInTheDocument();
  });

  // Caso de prueba para verificar que el botón 'Eliminar' llama a las funciones correctas.
  it('debe llamar a deleteFunction cuando se hace clic en "Eliminar"', async () => {
    const user = userEvent.setup();
    render(
      <DeleteConfirmationModalComponent
        id={mockId}
        deleteFunction={mockDeleteFunction}
        onClose={mockOnClose}
        objectName={mockObjectName}
        isDeleting={false}
      />
    );

    // Simula un clic realista en el botón 'Eliminar'.
    await user.click(screen.getByRole('button', { name: 'Eliminar' }));

    // Asegura que las funciones mockeadas se hayan llamado correctamente.    
    expect(mockDeleteFunction).toHaveBeenCalledTimes(1);
    expect(mockDeleteFunction).toHaveBeenCalledWith(mockId);
  });

  // Caso de prueba para verificar que el botón 'Cancelar' solo llama a onClose.
  it('solo debe llamar a onClose cuando se hace clic en "Cancelar"', async () => {
    const user = userEvent.setup();
    render(
      <DeleteConfirmationModalComponent
        id={mockId}
        deleteFunction={mockDeleteFunction}
        onClose={mockOnClose}
        objectName={mockObjectName}
        isDeleting={false}
      />
    );

    // Simula un clic realista en el botón 'Cancelar'.
    await user.click(screen.getByRole('button', { name: 'Cancelar' }));

    // Asegura que solo la función onClose haya sido llamada.
    expect(mockOnClose).toHaveBeenCalledTimes(1);
    expect(mockDeleteFunction).not.toHaveBeenCalled();
  });

  // Estado de carga (isDeleting = true)
  it('debe deshabilitar los botones y cambiar el texto cuando isDeleting es verdadero', () => {
    render(
      <DeleteConfirmationModalComponent
        id={mockId}
        deleteFunction={mockDeleteFunction}
        onClose={mockOnClose}
        objectName={mockObjectName}
        isDeleting={true}
      />
    );

    const deleteButton = screen.getByRole('button', { name: 'Eliminando...' });
    const cancelButton = screen.getByRole('button', { name: 'Cancelar' });

    expect(deleteButton).toBeDisabled();
    expect(cancelButton).toBeDisabled();
  });
});
