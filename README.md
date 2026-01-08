# 🏢 Car Like a Friend

Aplicación web para el alquiler de vehículos. Permite a los usuarios registrarse, explorar diferentes tipos de vehículos, hacer reservas, y dejar favoritos. Los Administradores de la aplicación pueden gestionar productos, categorías y servicios.

**Estado:** **En Desarrollo**

Este proyecto se encuentra actualmente en estado de desarrollo activo, y la versión final todavía no está disponible.

**Características principales:**
* Estructura básica del sitio.
* Funcionalidades de gestión de productos como registro, visualización y eliminación de productos, así como el detalle de producto.
* Funcionalidades básicas del login y registro de usuario.
* Funcionalidades para gestión de usuarios, roles y permisos.

**Próximos pasos:**
* Implementar la estructura para:
- Realizar Búsquedas de productos, 
- Visualizar disponibilidad, 
- Marcar productos como favoritos, 
- Lista de favoritos, 
- Bloque de políticas del producto, 
- Compartir productos y puntuar producto. 
* Realizar pruebas unitarias y de integración.

---

## Logo and Color Reference

![Logo](carlikeafriend-frontend\src\assets\Logo.png)

| Color             | Hex                  |
| Example Color     | ![#F4F3F2] #F4F3F2 |
| Example Color     | ![#70ACDE] #70ACDE |
| Example Color     | ![#1F88E6] #1F88E6 |
| Example Color     | ![#6A5E9B] #6A5E9B |
| Example Color     | ![#2E2E84] #2E2E84 |

![Color Palette](carlikeafriend-frontend\src\assets\ColorPalette.png)

---

## ⚙️ Tecnologías

### 🖥️ Frontend
- React 19 + Vite
- Bootstrap 5 CSS
- React Router

### ☕ Backend
- Java 17
- Spring Boot 3.5.x
- Spring Data JPA
- MySQL

---

## 🚀 Instalación local

### 🧩 Requisitos previos
- Node.js 20+
- Java 17+
- MySQL

### 📦 Cloná el repositorio
```bash
git clone https://github.com/JB-Save/carlikeafriend-app.git
cd carlikeafriend-app
```

---

### 📁 Backend (`/carlikeafriend-backend`)

```bash
cd carlikeafriend-backend
```

#### Configurar base de datos:
```sql
CREATE DATABASE carlikeafriend-db;
```

#### Configuración del Entorno
Para ejecutar este proyecto localmente, debes configurar las siguientes Variables de Entorno en tu IDE (IntelliJ/Eclipse):

DB_URL=jdbc:mysql://localhost:3306/nombre_tu_db
FRONTEND_URL:http://localhost:5173/signin
DB_USER=root
DB_PASSWORD=tu_password
JWT_SECRET=un_string_muy_largo_y_aleatorio
MAIL_USER=tu_correo@yahoo.com
MAIL_PASSWORD=tu_clave_de_aplicacion
UPLOAD_DIR=./uploads

#### Correr el backend:
```bash
./mvnw spring-boot:run
```
> El Backend estará disponible en `http://localhost:8080`
---

### 🖼️ Frontend (`/carlikeafriend-frontend`)

```bash
cd carlikeafriend-frontend
npm install
```

#### Archivo .env (Variables de entorno):
```dotenv
# URL Base de la API (ej: http://localhost:8080/carlikeafriend)
VITE_API_BASE_URL=

# Configuración de archivos (5MB = 5242880)
VITE_MAX_PRODUCT_IMAGES=5
VITE_MAX_CATEGORY_IMAGES=1
VITE_MAX_FEATURE_IMAGES=1
VITE_MAX_FILE_SIZE=5242880
```

#### Correr el frontend:
```bash
npm run dev
```
> La aplicación estará disponible en `http://localhost:5173`
---

## 📬 Endpoints (API REST)

| Método    | Endpoint                         | Descripción                           | Auth |
|-----------|----------------------------------|---------------------------------------|------|
| GET       | /carlikeafriend/products         | Listado de productos                  | ❌   |
| GET       | /carlikeafriend/products/{id}    | Detalle del producto                  | ❌   |
| GET       | /carlikeafriend/products/filter  | Filtros del producto                  | ❌   |
| POST      | /carlikeafriend/auth/register    | Registro de usuario                   | ❌   |
| POST      | /carlikeafriend/auth/login       | Login y generación de JWT             | ❌   |
| POST      | /carlikeafriend/products         | Crear producto                        | ✅ (ADMIN) |
| PUT       | /carlikeafriend/products/{id}    | Actualizar producto                   | ✅ (ADMIN) |
| DELETE    | /carlikeafriend/products/{id}    | Eliminar producto                     | ✅ (ADMIN) |

---

## 🗂️ Diagrama de Entidades (ER)

![ER](carlikeafriend-frontend\src\assets\entities.png)

> Creado con [MySQL Workbench]

---

## 🧪 Testing

### Backend
- Tests unitarios con JUnit y Mockito.

```bash
./mvnw test
```

### Frontend
- Testing con Vitest + React Testing Library

```bash
npm run test
```

---

## 👤 Autores

- [@JB-Save](https://github.com/JB-Save)

---

## 📄 Licencia
MIT
---

## 📞 Soporte
¿Encontraste un bug o tienes una sugerencia?

- 🐛 Reportar bug
- 💡 Solicitar feature
- 📧 Email: jasb5787@gmail.com