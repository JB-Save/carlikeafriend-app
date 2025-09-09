import { render, screen } from '@testing-library/react';
import { vi } from 'vitest';
import { HomeComponent } from '../pages/HomeComponent';
import { SearchSection } from '../components/SearchSection';
import { RecommendationSection } from '../components/RecommendationSection';

// Simular todos los componentes secundarios para aislar HomeComponent para pruebas.
// Esto nos permite comprobar si se renderizan sin ejecutar su lógica interna.
vi.mock('../components/WelcomeSection', () => ({
  WelcomeSection: vi.fn(() => <div data-testid="welcome-section"></div>),
}));

vi.mock('../components/SearchSection', () => ({
  SearchSection: vi.fn(() => <div data-testid="search-section"></div>),
}));

vi.mock('../components/CategoriesSection', () => ({
  CategoriesSection: vi.fn(() => <div data-testid="categories-section"></div>),
}));

vi.mock('../components/RecommendationSection', () => ({
  RecommendationSection: vi.fn(() => <div data-testid="recommendation-section"></div>),
}));

describe('HomeComponent', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

// Verifica que todas las secciones secundarias se representen correctamente.
  it('Debe representar todas las secciones de componentes', () => {
    render(<HomeComponent />);
    expect(screen.getByTestId('welcome-section')).toBeInTheDocument();
    expect(screen.getByTestId('search-section')).toBeInTheDocument();
    expect(screen.getByTestId('categories-section')).toBeInTheDocument();
    expect(screen.getByTestId('recommendation-section')).toBeInTheDocument();
  });

  // Verifica que se pasen las props correctas a SearchSection.
  it('Debe pasar las props correctas a SearchSection', () => {
    render(<HomeComponent />);
    expect(SearchSection).toHaveBeenCalledWith(
      { productsPerPage: 10, type: 'search' },
       undefined
    );
  });

  // Verifica que se pasen las props correctas a RecommendationsSection.
  it('Debe pasar las props correctas a RecommendationSection', () => {
    render(<HomeComponent />);
    expect(RecommendationSection).toHaveBeenCalledWith(
      { productsPerPage: 4, type: 'recommendation' },
       undefined
    );
  });
});