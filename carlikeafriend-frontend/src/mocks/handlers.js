import { http, HttpResponse } from 'msw';

export const handlers = [
    // Mock para listar productos
    http.get('*/carlikeafriend/products', () => {
        return HttpResponse.json([
            { id: 101, name: 'Producto Prueba', price: 50 }
        ]);
    }),

    // Mock para obtener un producto por ID
    http.get('*/carlikeafriend/products/:id', () => {
        return HttpResponse.json({
            id: 1,
            name: 'Auto Test',
            description: 'Desc',
            categories: [],
            features: [],
            policies: [],
            productImages: [{ imagePath: 'test.jpg' }]
        });
    }),

    // Mock para eliminación de producto
    http.delete('*/carlikeafriend/products/:id', () => {
        return new HttpResponse(null, { status: 204 });
    }),

    // Mock para favoritos
    http.get('*/carlikeafriend/products/favorites', () => {
        return HttpResponse.json([
            { id: 1, productId: 1 }
        ]);
    }),

    // Mock para obtener reseñas de un producto
    http.get('*/carlikeafriend/reviews/:productId/products', () => {
        return HttpResponse.json([
            { id: 1, stars: 5, comment: 'Excelente vehículo', createdAt: '2023-01-01T00:00:00Z', user: { name: 'Usuario Test' } }
        ]);
    }),

    // Mock para obtener ciudades y sus sucursales
    http.get('*/carlikeafriend/cities/branches', () => {
        return HttpResponse.json([
            { id: 10, name: 'Ciudad Mock', branches: [] }
        ]);
    }),
];