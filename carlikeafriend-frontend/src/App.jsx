import { AddProductComponent } from "./components/AddProductComponent";
import { AdmininistrationComponent } from "./pages/AdmininistrationComponent";
import { FooterComponent } from "./components/FooterComponent";
import { HomeComponent } from "./pages/HomeComponent";
import { NavBarComponent } from "./components/NavBarComponent"
import { ProductDetailsComponent } from "./pages/ProductDetailsComponent";
import { ProductListComponent } from "./components/ProductListComponent";
import { MessageModalProvider } from "./context/MessageModalContext";
import { UserProvider } from "./context/UserProvider"
import { Route, Routes } from "react-router-dom";
import { SignUpForm } from "./pages/SignUpForm";
import { SignInForm } from "./pages/SignInForm";
import { ProtectedRoute } from "./components/ProtectedRoute";
import { UserProfilePage } from "./pages/UserProfilePage";
import { AddPermissionComponent } from "./components/AddPermissionComponent";
import { PermissionListComponent } from "./components/PermissionListComponent";
import { AddRoleComponent } from "./components/AddRoleComponent";
import { RoleListComponent } from "./components/RoleListComponent";
import { UserListComponent } from "./components/UserListComponent";
import { AddUserComponent } from "./components/AddUserComponent";
import { AddFeatureComponent } from "./components/AddFeatureComponent";
import { FeatureListComponent } from "./components/FeatureListComponent";
import { RegistrationSuccessAndResend } from "./pages/RegistrationSuccessAndResend";
import { ProductFilterPage } from "./pages/ProductFilterPage";
import { ScrollToTop } from "./components/ScrollToTop";
import { AddCategoryComponent } from "./components/AddCategoryComponent";
import { CategoryListComponent } from "./components/CategoryListComponent";

export const App = () => {
  return (
    <UserProvider>
      <MessageModalProvider>
        {/* ScrollToTop -> Componente para ajustar la página llevando al usuario al inicio visualmente. */}
        <ScrollToTop />
        <NavBarComponent />
        {/* Rutas Públicas */}
        <Routes>
          <Route path="/signup" element={<SignUpForm />} />
          <Route path="/signin" element={<SignInForm />} />
          <Route path="/successful-registration" element={<RegistrationSuccessAndResend />} />
          <Route path="/" element={<HomeComponent />} />
          <Route path='/product-detail/:id' element={<ProductDetailsComponent />} />
          <Route path="/product-filter" element={<ProductFilterPage />} />

          {/* Rutas Protegidas */}
          {/* Ruta principal del panel de administración. Envolver las rutas con ProtectedRoute */}
          <Route element={<ProtectedRoute allowedRoles="ADMIN" />}> {/* Solo usuarios con el rol 'ADMIN' */}
            <Route path='/administration/' element={<AdmininistrationComponent />}>
              <Route index element={
                <div id="welcome-section" className="card card-shadow rounded-3 p-4">
                  <h4 className="fw-bold card-text mb-3">Bienvenido al Panel de Administración</h4>
                  <p className="card-text">Desde aquí puedes gestionar todos los aspectos de tu negocio de alquiler de autos. Utiliza el menú de la izquierda para navegar entre las diferentes secciones.</p>
                  <p className="card-text">Actualmente, puedes:</p>
                  <ul className="list-unstyled">
                    <li>
                      <i className="bi bi-check-circle-fill text-success me-2"></i> <b>**Gestionar Productos:**</b>
                      <ul className="list-unstyled ps-4">
                        <li className="card-text">• Lista de productos: Agrega nuevos vehículos a tu catálogo. Visualiza y gestiona todos los productos disponibles en una tabla.</li>
                        <li className="card-text">• Administrar Categorías : Agrega nuevas, visualiza y gestiona todas las categorías disponibles en una tabla.</li>
                        <li className="card-text">• Administrar Características : Agrega nuevas, visualiza y gestiona todas las características disponibles en una tabla.</li>
                      </ul>
                    </li>

                    <li>
                      <i className="bi bi-check-circle-fill text-success me-2"></i> <b>**Gestionar Usuarios:**</b>
                      <ul className="list-unstyled ps-4">
                        <li className="card-text">• Administrar Permisos: Agrega nuevos permisos para los roles de usuario. Visualiza y gestiona todos los permisos disponibles en una tabla.</li>
                        <li className="card-text">• Administrar Roles : Agrega nuevos, visualiza y gestiona todos los roles disponibles en una tabla.</li>
                        <li className="card-text">• Administrar Usuarios : Visualiza y gestiona todos usuarios para asignarles roles.</li>
                      </ul>
                    </li>
                  </ul>
                  <p className="text-muted text-sm mt-4">Mantente atento a las próximas actualizaciones con nuevas funcionalidades.</p>
                </div>
              } />
              {/* Rutas anidadas dentro del panel de administración */}
              <Route path="add-product" element={<AddProductComponent />} />
              <Route path="product-list" element={<ProductListComponent />} />
              <Route path="add-category" element={<AddCategoryComponent />} />
              <Route path="category-list" element={<CategoryListComponent />} />
              <Route path="add-feature" element={<AddFeatureComponent />} />
              <Route path="feature-list" element={<FeatureListComponent />} />
              <Route path="add-permission" element={<AddPermissionComponent />} />
              <Route path="permission-list" element={<PermissionListComponent />} />
              <Route path="add-role" element={<AddRoleComponent />} />
              <Route path="role-list" element={<RoleListComponent />} />
              <Route path="add-user" element={<AddUserComponent />} />
              <Route path="user-list" element={<UserListComponent />} />
            </Route>
          </Route>

          {/*Ruta para información personal, solo requiere estar logueado, sin rol específico */}
          <Route element={<ProtectedRoute />}> {/* No se especifica 'allowedRoles', solo requiere isAuthenticated=true */}
            <Route path='/profile' element={<UserProfilePage />} />
          </Route>

          {/* Ruta de 404/No Encontrada */}
          <Route path="*" element={<h1>404: Page Not Found</h1>} />
        </Routes>

        <FooterComponent />
      </MessageModalProvider>
    </UserProvider>
  )
}

export default App
