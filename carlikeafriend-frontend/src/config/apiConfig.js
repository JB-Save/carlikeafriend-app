// Recuperamos la URL base del archivo .env o usamos el valor local por defecto
const BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/carlikeafriend';

export const API_CONFIG = {
    BASE_URL: BASE_URL,

    // Endpoints específicos basados en los componentes
    PRODUCTS: `${BASE_URL}/products`,
    RECOMMENDED_PRODUCTS: `${BASE_URL}/products/recommended-products`,
    CATEGORIES: `${BASE_URL}/categories`,
    FEATURES: `${BASE_URL}/features`,
    PERMISSIONS: `${BASE_URL}/permissions`,
    ROLES: `${BASE_URL}/roles`,
    USERS: `${BASE_URL}/users`,
    AUTH: `${BASE_URL}/auth/me`,
    FILTER: `${BASE_URL}/products/filter`,
    LOGIN: `${BASE_URL}/auth/login`,
    REGISTER: `${BASE_URL}/auth/register`,
    EMAIL_RESEND_CONFIRMATION: `${BASE_URL}/users/email/resend-confirmation`,

    // Ruta para las imágenes de productos
    PRODUCT_IMAGES_BASE: `${BASE_URL}/products/images`,

    // Ruta para las imágenes de categorias
    CATEGORY_IMAGES_BASE: `${BASE_URL}/categories/images`,

    // Ruta para las imágenes de características
    FEATURE_IMAGES_BASE: `${BASE_URL}/features/images`,

    // Valor máximo de imágenes en useProductForm.js
    MAX_IMAGES_FOR_PRODUCTS: Number(import.meta.env.VITE_MAX_PRODUCT_IMAGES) || 5,
    // Valor máximo de imágenes en useCategoryForm.js
    MAX_IMAGES_FOR_CATEGORIES: Number(import.meta.env.VITE_MAX_CATEGORY_IMAGES) || 1,
    // Valor máximo de imágenes en useFeatureForm.js
    MAX_IMAGES_FOR_FEATURES: Number(import.meta.env.VITE_MAX_FEATURE_IMAGES) || 1,

    // Tamaño máximo por imagen 5 MB en bytes
    MAX_FILE_SIZE: Number(import.meta.env.VITE_MAX_FILE_SIZE) || 5242880,
};