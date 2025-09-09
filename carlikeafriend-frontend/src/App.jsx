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

export const App = () => {
  return (
    <UserProvider>
      <NavBarComponent />
      <Routes>
        <Route path="/" element={<MessageModalProvider><HomeComponent /></MessageModalProvider>} />
        <Route path='/product-detail/:id' element={<MessageModalProvider><ProductDetailsComponent /></MessageModalProvider>}></Route>

        {/* Ruta principal del panel de administración */}
        <Route path='/administration/' element={<MessageModalProvider><AdmininistrationComponent /></MessageModalProvider>}>
          <Route index element={
            <div id="welcome-section" className="card card-shadow rounded-lg p-4">
              <h4 className="h5 fw-bold card-text mb-3">Bienvenido al Panel de Administración</h4>
              <p className="card-text">Desde aquí puedes gestionar todos los aspectos de tu negocio de alquiler de autos. Utiliza el menú de la izquierda para navegar entre las diferentes secciones.</p>
              <p className="card-text">Actualmente, puedes:</p>
              <ul>
                <li><i className="bi bi-check-circle-fill text-success me-2"></i> **Registrar Producto:** Agrega nuevos vehículos a tu catálogo.</li>
                <li><i className="bi bi-check-circle-fill text-success me-2"></i> **Lista de productos:** Visualiza y gestiona todos los productos disponibles en una tabla.</li>
              </ul>
              <p className="text-muted text-sm mt-4">Mantente atento a las próximas actualizaciones con nuevas funcionalidades.</p>
            </div>
          } />
          {/* Rutas anidadas dentro del panel de administración */}
          <Route path="add-product" element={<AddProductComponent />} />
          <Route path="product-list" element={<ProductListComponent />} />
        </Route>
      </Routes>

      <FooterComponent />
    </UserProvider>
  )
}

export default App
