import { render, screen } from '../utils/test-utils'; 
import { vi, describe, it, expect } from 'vitest';
import { HomeComponent } from '../pages/HomeComponent';

// Simular todos los componentes secundarios para aislar HomeComponent para pruebas.
// Esto nos permite comprobar si se renderizan sin ejecutar su lógica interna.
vi.mock('../components/WelcomeSection', () => ({
  WelcomeSection: vi.fn(() => <div data-testid="welcome-section"></div>),
}));

vi.mock('../components/SearchSection', () => ({
  // Capturamos las props y las renderizamos en el atributo data-props
  SearchSection: (props) => (
    <div data-testid="search-section" data-props={JSON.stringify(props)}></div>
  ),
}));

vi.mock('../components/CategoriesSection', () => ({
  CategoriesSection: vi.fn(() => <div data-testid="categories-section"></div>),
}));

vi.mock('../components/RecommendationSection', () => ({
  RecommendationSection: (props) => (
    <div data-testid="recommendation-section" data-props={JSON.stringify(props)}></div>
  ),
}));

describe('HomeComponent', () => {
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
    const searchSection = screen.getByTestId('search-section');
    const props = JSON.parse(searchSection.getAttribute('data-props'));

    expect(props).toEqual({
      productsPerPage: 10,
      type: 'search'
    });
  });

  // Verifica que se pasen las props correctas a RecommendationsSection.
  it('Debe pasar las props correctas a RecommendationSection', () => {
    render(<HomeComponent />);
    const recommendationSection = screen.getByTestId('recommendation-section');
    const props = JSON.parse(recommendationSection.getAttribute('data-props'));

    expect(props).toEqual({
      productsPerPage: 4,
      type: 'recommendation'
    });
  });
});