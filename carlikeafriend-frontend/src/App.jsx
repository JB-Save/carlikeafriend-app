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
import { UserAccountPage } from "./pages/UserAccountPage";
import { AddPermissionComponent } from "./components/AddPermissionComponent";
import { PermissionListComponent } from "./components/PermissionListComponent";
import { AddRoleComponent } from "./components/AddRoleComponent";
import { RoleListComponent } from "./components/RoleListComponent";
import { UserListComponent } from "./components/UserListComponent";
import { AddUserComponent } from "./components/AddUserComponent";
import { AddCategoryComponent } from "./components/AddCategoryComponent";
import { CategoryListComponent } from "./components/CategoryListComponent";
import { AddFeatureComponent } from "./components/AddFeatureComponent";
import { FeatureListComponent } from "./components/FeatureListComponent";
import { AddMakeComponent } from "./components/AddMakeComponent";
import { MakeListComponent } from "./components/MakeListComponent";
import { AddVehicleComponent } from "./components/AddVehicleComponent";
import { VehicleListComponent } from "./components/VehicleListComponent";
import { AddPolicyTypeComponent } from "./components/AddPolicyTypeComponent";
import { PolicyTypeListComponent } from "./components/PolicyTypeListComponent";
import { AddPolicyComponent } from "./components/AddPolicyComponent";
import { PolicyListComponent } from "./components/PolicyListComponent";
import { AddCityComponent } from "./components/AddCityComponent";
import { CityListComponent } from "./components/CityListComponent";
import { AddBranchComponent } from "./components/AddBranchComponent";
import { BranchListComponent } from "./components/BranchListComponent";
import { AddTransferFeeComponent } from "./components/AddTransferFeeComponent";
import { TransferFeeListComponent } from "./components/TransferFeeListComponent";
import { AddExtrasComponent } from "./components/AddExtrasComponent";
import { ExtrasListComponent } from "./components/ExtrasListComponent";
import { AddBranchAddonComponent } from "./components/AddBranchAddonComponent";
import { BranchAddonListComponent } from "./components/BranchAddonListComponent";
import { AddFinancialConfigComponent } from "./components/AddFinancialConfigComponent";
import { RegistrationSuccessAndResend } from "./pages/RegistrationSuccessAndResend";
import { ProductFilterPage } from "./pages/ProductFilterPage";
import { ScrollToTop } from "./components/ScrollToTop";
import { FavoriteProvider } from "./context/FavoriteContext";
import { MyFavoritesComponent } from "./components/MyFavoritesComponent";
import { BookingProvider } from "./context/BookingContext";
import { PageNotFound } from "./pages/PageNotFound";
import { ProfileInfoComponent } from "./components/ProfileInfoComponent";
import { MyReservationsComponent } from "./components/MyReservationsComponent";
import { SecuritySettingsComponent } from "./components/SecuritySettingsComponent";

export const App = () => {
  return (
    <UserProvider>
      <MessageModalProvider>
        {/* ScrollToTop -> Componente para ajustar la página llevando al usuario al inicio visualmente. */}
        <ScrollToTop />
        <FavoriteProvider> {/* Maneja la sincronización de favoritos */}
          <NavBarComponent />
          <BookingProvider>
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
                      <p className="card-text mb-4">
                        Desde aquí puedes gestionar todos los aspectos de tu negocio de alquiler de autos. Utiliza el menú de la izquierda para navegar entre las diferentes secciones. Actualmente, puedes:
                      </p>

                      <div className="row g-4">

                        {/* Módulo Productos */}
                        <div className="col-md-6">
                          <div className="d-flex align-items-start">
                            <i className="bi bi-inboxes-fill fs-3 text-primary me-3"></i>
                            <div>
                              <h6 className="fw-bold mb-1">Gestionar Productos</h6>
                              <p className="admin-panel-text-muted small mb-0">
                                Administra el catálogo completo: vehículos, marcas, categorías y características.
                              </p>
                            </div>
                          </div>
                        </div>

                        {/* Módulo Usuarios */}
                        <div className="col-md-6">
                          <div className="d-flex align-items-start">
                            <i className="bi bi-people-fill fs-3 text-primary me-3"></i>
                            <div>
                              <h6 className="fw-bold mb-1">Gestionar Usuarios</h6>
                              <p className="admin-panel-text-muted small mb-0">
                                Controla el acceso al sistema configurando permisos, roles y cuentas de usuario.
                              </p>
                            </div>
                          </div>
                        </div>

                        {/* Módulo Políticas */}
                        <div className="col-md-6">
                          <div className="d-flex align-items-start">
                            <i className="bi bi-shield-fill-check fs-3 text-primary me-3"></i>
                            <div>
                              <h6 className="fw-bold mb-1">Gestionar Políticas</h6>
                              <p className="admin-panel-text-muted small mb-0">
                                Define y asigna los tipos de políticas y condiciones de alquiler para los productos.
                              </p>
                            </div>
                          </div>
                        </div>

                        {/* Módulo Sucursales */}
                        <div className="col-md-6">
                          <div className="d-flex align-items-start">
                            <i className="bi bi-building-fill fs-3 text-primary me-3"></i>
                            <div>
                              <h6 className="fw-bold mb-1">Gestionar Sucursales</h6>
                              <p className="admin-panel-text-muted small mb-0">
                                Administra ciudades, ubicaciones, tarifas e inventario de extras por sucursal.
                              </p>
                            </div>
                          </div>
                        </div>

                        {/* Módulo Financiero */}
                        <div className="col-md-6">
                          <div className="d-flex align-items-start">
                            <i className="bi bi-calculator-fill fs-3 text-primary me-3"></i>
                            <div>
                              <h6 className="fw-bold mb-1">Gestión Financiera</h6>
                              <p className="admin-panel-text-muted small mb-0">
                                Configura valores para el cálculo de impuestos, seguros y penalizaciones.
                              </p>
                            </div>
                          </div>
                        </div>

                      </div>

                      {/* Separador y mensaje final */}
                      <hr className="mt-4 mb-3 admin-panel-text-muted opacity-25" />
                      <p className="admin-panel-text-muted text-sm mb-0">
                        <i className="bi bi-info-circle-fill me-2"></i>
                        Mantente atento a las próximas actualizaciones con nuevas funcionalidades.
                      </p>
                    </div>
                  } />
                  {/* Rutas anidadas dentro del panel de administración */}
                  <Route path="add-product" element={<AddProductComponent />} />
                  <Route path="product-list" element={<ProductListComponent />} />
                  <Route path="add-category" element={<AddCategoryComponent />} />
                  <Route path="category-list" element={<CategoryListComponent />} />
                  <Route path="add-feature" element={<AddFeatureComponent />} />
                  <Route path="feature-list" element={<FeatureListComponent />} />
                  <Route path="add-make" element={<AddMakeComponent />} />
                  <Route path="make-list" element={<MakeListComponent />} />
                  <Route path="add-vehicle" element={<AddVehicleComponent />} />
                  <Route path="vehicle-list" element={<VehicleListComponent />} />
                  <Route path="add-permission" element={<AddPermissionComponent />} />
                  <Route path="permission-list" element={<PermissionListComponent />} />
                  <Route path="add-role" element={<AddRoleComponent />} />
                  <Route path="role-list" element={<RoleListComponent />} />
                  <Route path="add-user" element={<AddUserComponent />} />
                  <Route path="user-list" element={<UserListComponent />} />
                  <Route path="add-policyType" element={<AddPolicyTypeComponent />} />
                  <Route path="policyType-list" element={<PolicyTypeListComponent />} />
                  <Route path="add-policy" element={<AddPolicyComponent />} />
                  <Route path="policy-list" element={<PolicyListComponent />} />
                  <Route path="add-city" element={<AddCityComponent />} />
                  <Route path="city-list" element={<CityListComponent />} />
                  <Route path="add-branch" element={<AddBranchComponent />} />
                  <Route path="branch-list" element={<BranchListComponent />} />
                  <Route path="add-transferFee" element={<AddTransferFeeComponent />} />
                  <Route path="transferFee-list" element={<TransferFeeListComponent />} />
                  <Route path="add-extra" element={<AddExtrasComponent />} />
                  <Route path="extras-list" element={<ExtrasListComponent />} />
                  <Route path="assign-addon" element={<AddBranchAddonComponent />} />
                  <Route path="branchAddon-list" element={<BranchAddonListComponent />} />
                  <Route path="financial-config" element={<AddFinancialConfigComponent />} />
                </Route>
              </Route>

              {/*Ruta para información personal, solo requiere estar logueado, sin rol específico */}
              <Route element={<ProtectedRoute />}> {/* No se especifica 'allowedRoles', solo requiere isAuthenticated=true */}
                <Route path='/my-account/' element={<UserAccountPage />}>
                  <Route index element={<ProfileInfoComponent />} />
                  {/* Subrutas anidadas */}
                  <Route path="favorites" element={<MyFavoritesComponent />} />
                  <Route path="reservations" element={<MyReservationsComponent itemsPerPage={5} type="reservas" />} />
                  <Route path="security" element={<SecuritySettingsComponent />} />
                </Route>
              </Route>


              {/* Ruta de 404/No Encontrada */}
              <Route path="*" element={<PageNotFound />} />
            </Routes>
          </BookingProvider>
        </FavoriteProvider>
        <FooterComponent />
      </MessageModalProvider>
    </UserProvider>
  )
}

export default App
