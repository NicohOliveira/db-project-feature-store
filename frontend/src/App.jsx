import CadastroUsuario from "./pages/CadastroUsuario";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import LoginUsuario from "./pages/LoginUsuario.jsx";
import Dashboard from "./pages/Dashboard";
import DetalhesDataset from "./pages/DetalhesDataset.jsx";
import CadastroDataset from "./pages/CadastroDataset.jsx";
import EditarDataset from "./pages/EditarDataset.jsx";
import HistoricoLinhagem from "./pages/HistoricoLinhagem.jsx";

function App() {
  return (
      <BrowserRouter>
          <Routes>
              <Route path="/" element={<LoginUsuario />} />
              <Route path="/cadastro" element={<CadastroUsuario />} />
              <Route path="/dashboard" element={<Dashboard />} />
              <Route path="/cadastro-dataset" element={<CadastroDataset/>} />
              <Route path="/dataset/editar/:id" element={<EditarDataset />} />
              <Route path="/dataset/:id" element={<DetalhesDataset />} />
              <Route path="/ver-versoes/:id" element={<HistoricoLinhagem />} />
              <Route path="*" element={<Navigate to="/" />} />
          </Routes>
      </BrowserRouter>
  );
}

export default App;