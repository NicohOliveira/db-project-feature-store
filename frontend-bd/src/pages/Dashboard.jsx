import { useNavigate, Link } from "react-router-dom";

function Dashboard() {
    const navigate = useNavigate();
    const usuarioLogado = localStorage.getItem("username");

    const handleLogout = () => {
        localStorage.removeItem("username");
        navigate("/");
    };

    return (
        <div className="d-flex vh-100 bg-light">
            <div className="d-flex flex-column flex-shrink-0 p-3 text-white bg-dark shadow" style={{ width: "280px" }}>
                <span className="fs-4 fw-bold mb-4 text-center border-bottom pb-3">
                    Feature Store
                </span>

                <div className="d-flex align-items-center mb-4 ps-2">
                    <div className="rounded-circle bg-primary d-flex justify-content-center align-items-center me-3" style={{ width: "45px", height: "45px" }}>
                        <span className="fw-bold text-white fs-5">
                            {usuarioLogado.charAt(0).toUpperCase()}
                        </span>
                    </div>
                    <div>
                        <small className="text-secondary d-block" style={{ fontSize: "0.80rem" }}>Logado como:</small>
                        <span className="fw-bold">{usuarioLogado}</span>
                    </div>
                </div>

                <hr className="mt-0" />
                <ul className="nav nav-pills flex-column mb-auto mt-2">
                    <li className="nav-item mb-2">
                        <Link to="/dashboard" className="nav-link active" aria-current="page">
                            📊 Meus Datasets
                        </Link>
                    </li>
                    <li className="nav-item">
                        <Link to="#" className="nav-link text-white">
                            ⚙️ Configurações
                        </Link>
                    </li>
                </ul>

                <hr />

                <button className="btn btn-outline-danger w-100 mt-2 fw-bold" onClick={handleLogout}>
                    🚪 Sair
                </button>
            </div>

            <div className="flex-grow-1 p-5 overflow-auto">
                <div className="d-flex justify-content-between align-items-center mb-4">
                    <h2>Meus Datasets</h2>
                    <Link to="/cadastro-dataset" className="btn btn-success fw-bold">
                        + Novo Dataset
                    </Link>
                </div>

                <div className="card shadow-sm border-0">
                    <div className="card-body text-center text-muted py-5">
                        <p className="fs-5 mb-1">Nenhum dataset cadastrado ainda.</p>
                        <em>(aguardando a API dos dataset ficar pronta... ;))</em>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default Dashboard;