import userEvent from '@testing-library/user-event';
import { vi, describe, it, expect, beforeEach } from 'vitest';
import { render, screen, waitFor } from '../utils/test-utils';
import { ProductForm } from '../components/ProductForm';
import { useProductForm } from '../hooks/useProductForm';

// 1. Mock de dependencias externas
vi.mock('../hooks/useProductForm', () => ({
  useProductForm: vi.fn(),
}));

vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return {
    ...actual,
    useNavigate: () => vi.fn(),
  };
});

describe('ProductForm - Pruebas Integrales', () => {
  const mockSubmit = vi.fn();
  const mockRemoveImage = vi.fn();

  const defaultHookValues = {
    allCategories: [{ id: 1, name: 'SUV' }],
    allFeatures: [{ id: 1, name: 'Aire Acondicionado' }],
    allMakes: [{ id: 1, name: 'Toyota' }],
    allPolicies: [{ id: 1, name: 'Política de prueba' }],
    isLoadingCategory: false,
    isLoadingFeature: false,
    isLoadingMake: false,
    isLoadingPolicy: false,
    error: null,
    isSubmittingForm: false,
    newImages: [],
    existingImages: [],
    imageUploadError: null,
    availableSlots: 5,
    canAddMoreImages: true,
    fileInputRef: { current: null },
    handleNewImageChange: vi.fn(),
    handleRemoveExistingImage: mockRemoveImage,
    handleRemoveNewImageFile: vi.fn(),
    submitProductData: mockSubmit,
  };

  beforeEach(() => {
    useProductForm.mockReturnValue(defaultHookValues);
  });

  it('debe renderizar los campos principales correctamente', () => {
    render(<ProductForm productToEdit={null} onProductSaved={vi.fn()} />);

    expect(screen.getByLabelText(/Nombre/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/Descripción/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/Marca/i)).toBeInTheDocument();
  });

  it('debe llamar a submitProductData al enviar el formulario válido', async () => {
    const user = userEvent.setup();
    render(<ProductForm productToEdit={null} onProductSaved={vi.fn()} />);

    // Rellenar campos de texto obligatorios
    await user.type(screen.getByLabelText(/Nombre/i), 'Auto Nuevo');
    await user.type(screen.getByLabelText(/Descripción/i), 'Descripción de prueba detallada');

    // Seleccionar marca (asegúrate de que el valor coincida con el option value)
    await user.selectOptions(screen.getByLabelText(/Marca/i), '1');

    // Rellenar campos numéricos obligatorios (según tu esquema de validación)
    await user.type(screen.getByLabelText(/Capacidad de Pasajeros/i), '5');
    await user.type(screen.getByLabelText(/Capacidad de equipaje/i), '2');
    await user.type(screen.getByLabelText(/Número de puertas/i), '4');

    // Seleccionar elementos obligatorios en los checkboxes o listas
    await user.click(screen.getByLabelText(/SUV/i));
    await user.click(screen.getByLabelText(/Aire Acondicionado/i));
    await user.click(screen.getByLabelText(/Política de prueba/i));

    // Enviar formulario
    const submitButton = screen.getByRole('button', { name: /Registrar Producto/i });
    await user.click(submitButton);

    // Validamos que el submit se ejecute correctamente tras pasar validaciones de Yup
    await waitFor(() => {
      expect(mockSubmit).toHaveBeenCalledTimes(1);
    });
  });

  it('debe deshabilitar botones cuando isSubmittingForm es true', () => {
    useProductForm.mockReturnValue({
      ...defaultHookValues,
      isSubmittingForm: true,
    });

    render(<ProductForm productToEdit={null} onProductSaved={vi.fn()} />);

    const submitButton = screen.getByRole('button', { name: /Guardando.../i });
    expect(submitButton).toBeDisabled();
  });
});