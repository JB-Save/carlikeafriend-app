// Recuperamos la URL base del archivo .env o usamos el valor local por defecto
const BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/carlikeafriend';

export const API_CONFIG = {
    BASE_URL: BASE_URL,

    // Endpoints específicos basados en los componentes
    ADDONS: `${BASE_URL}/addons`,
    AUTH: `${BASE_URL}/auth/me`,
    BRANCHES: `${BASE_URL}/branches`,
    BRANCH_INVENTORY: `${BASE_URL}/inventory`,
    CATEGORIES: `${BASE_URL}/categories`,
    CHANGE_PASSWORD: `${BASE_URL}/auth/change-password`,
    CITIES: `${BASE_URL}/cities`,
    CITIES_WITH_BRANCHES: `${BASE_URL}/cities/branches`,
    EMAIL_RESEND_CONFIRMATION: `${BASE_URL}/users/email/resend-confirmation`,
    FAVORITES: `${BASE_URL}/products/favorites`,
    FEATURES: `${BASE_URL}/features`,
    FILTERS: `${BASE_URL}/products/filters`,
    LOGIN: `${BASE_URL}/auth/login`,
    MAKES: `${BASE_URL}/makes`,
    PERMISSIONS: `${BASE_URL}/permissions`,
    POLICIES: `${BASE_URL}/policies`,
    POLICY_TYPES: `${BASE_URL}/policy-types`,
    PRIVATE_FINANCIAL_CONFIG: `${BASE_URL}/financial-config`,
    PRODUCTS: `${BASE_URL}/products`,
    PRODUCTS_HOME_CATALOGUES: `${BASE_URL}/products/home-catalogues`,
    PRODUCT_PRICE_RANGE:  `${BASE_URL}/products/price-ranges`,
    PRODUCT_REVIEWS: `${BASE_URL}/reviews`,
    PUBLIC_FINANCIAL_CONFIG: `${BASE_URL}/public/financial-config`,
    RECOMMENDED_PRODUCTS: `${BASE_URL}/products/recommended-products`,
    REGISTER: `${BASE_URL}/auth/register`,
    RESERVATIONS: `${BASE_URL}/reservations`,
    ROLES: `${BASE_URL}/roles`,
    SHARE: `${BASE_URL}/share`,
    TRANSFER_FEES: `${BASE_URL}/transfer-fees`,
    USERS: `${BASE_URL}/users`,
    USER_ACCOUNT: `${BASE_URL}/users/account`,
    VEHICLES: `${BASE_URL}/vehicles`,

    // Ruta para metadatos
    CHARGE_TYPES: `${BASE_URL}/metadata/charge-types`,
    VEHICLE_STATUSES: `${BASE_URL}/metadata/vehicle-statuses`,
    DOCUMENT_TYPES: `${BASE_URL}/metadata/document-types`,

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