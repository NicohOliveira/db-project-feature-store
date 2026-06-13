import CadastroUsuario from "./pages/CadastroUsuario";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import LoginUsuario from "./pages/LoginUsuario.jsx";
import Dashboard from "./pages/Dashboard";

function App() {
  return (
      <BrowserRouter>
          <Routes>
              <Route path="/" element={<LoginUsuario />} />
              <Route path="/cadastro" element={<CadastroUsuario />} />
              <Route path="/dashboard" element={<Dashboard />} />
              <Route path="*" element={<Navigate to="/" />} />
          </Routes>
      </BrowserRouter>
    /*<div>
      <CadastroUsuario />
    </div>*/
  );
}

export default App;