import { useNavigate, Link } from "react-router-dom";
import { useState, useEffect } from "react";

function Dashboard() {
    const navigate = useNavigate();
    const usuarioLogado = localStorage.getItem("username");

    const [datasets, setDatasets] = useState([]);

    const [modo, setModo] = useState("meus");

    const datasetsFiltrados = modo === "meus" ? datasets.filter(d => d.username_criador === usuarioLogado) : datasets;

    const handleLogout = () => {
        localStorage.removeItem("username");
        navigate("/");
    };

    useEffect(() => {
        fetch("http://localhost:8080/backend/dataset")
            .then(res => res.json())
            .then(data => setDatasets(data));
    }, []);

    return (
        <div className="d-flex vh-100" style={{ background: "#1a1a1a" }}>
            <div className="d-flex flex-column flex-shrink-0 p-3" style={{ width: "280px", background: "#222222", borderRight: "1px solid #333333" }}>
                <span className="fs-4 fw-bold mb-4 text-center border-bottom pb-3" style={{ color: "#e0e0e0", borderColor: "#333333" }}>
                    Feature Store
                </span>

                <div className="d-flex flex-row align-items-center justify-content-center gap-3 mb-4">
                    <div className="rounded-circle bg-primary d-flex justify-content-center align-items-center flex-shrink-0" style={{ width: "45px", height: "45px" }}>
                        <span className="fw-bold text-white fs-5">
                            {usuarioLogado.charAt(0).toUpperCase()}
                        </span>
                    </div>
                    <div>
                        <small className="d-block" style={{ fontSize: "0.80rem", color: "#888" }}>Logado como:</small>
                        <span className="fw-bold" style={{ color: "#e0e0e0" }}>{usuarioLogado}</span>
                    </div>
                </div>

                <hr style={{ borderColor: "#333333" }} className="mt-0" />
                <ul className="nav nav-pills flex-column mb-auto mt-2">
                    <li className="nav-item mb-2">
                        <button
                            className={`nav-link w-100 d-flex align-items-center gap-2 ${modo === "meus" ? "active" : ""}`}
                            style={modo !== "meus" ? { color: "#aaa" } : {}}
                            onClick={() => setModo("meus")}
                        >
                            <span style={{ width: "24px", textAlign: "center" }}>👤</span>
                            <span>Meus Datasets</span>
                        </button>
                    </li>
                    <li className="nav-item mb-2">
                        <button
                            className={`nav-link w-100 d-flex align-items-center gap-2 ${modo === "todos" ? "active" : ""}`}
                            style={modo !== "todos" ? { color: "#aaa" } : {}}
                            onClick={() => setModo("todos")}
                        >
                            <span style={{ width: "24px", textAlign: "center" }}>👥</span>
                            <span>Todos os Datasets</span>
                        </button>
                    </li>
                </ul>

                <hr style={{ borderColor: "#333333" }} />
                <button className="btn btn-outline-danger w-100 mt-2 fw-bold" onClick={handleLogout}>
                    Sair
                </button>
            </div>

            <div className="flex-grow-1 p-5 overflow-auto" style={{ background: "#1a1a1a" }}>
                <div className="d-flex justify-content-between align-items-center mb-4">
                    <h2 style={{ color: "#e0e0e0" }}>{modo === "meus" ? "Meus Datasets" : "Todos os Datasets"}</h2>
                    <Link to="/cadastro-dataset" className="btn btn-success fw-bold">
                        + Novo Dataset
                    </Link>
                </div>

                <div className="card border-0 shadow" style={{ background: "#222222" }}>
                    <div className="card-body p-0">
                        {datasetsFiltrados.length === 0 ? (
                            <div className="text-center py-5" style={{ color: "#888" }}>
                                <p className="fs-5 mb-0">Nenhum dataset encontrado.</p>
                            </div>
                        ) : (
                            datasetsFiltrados.map((d, index) => (
                                <div
                                    key={d.id}
                                    className="d-flex align-items-center px-4 py-3"
                                    style={{
                                        gap: "16px",
                                        borderBottom: index !== datasetsFiltrados.length - 1 ? "1px solid #333333" : "none"
                                    }}
                                >
                                    <div className="flex-grow-1">
                                        <div className="fw-bold" style={{ color: "#e0e0e0" }}>{d.nome}</div>
                                        <small style={{ color: "#888" }}>Criador: {d.username_criador}</small>
                                    </div>
                                    
                                    <div style={{ width: "70px" }}>
                                        {d.username_criador === usuarioLogado ? (
                                            <Link to={`/dataset/editar/${d.id}`} className="btn btn-outline-secondary btn-sm">
                                                Editar
                                            </Link>
                                        ) : null }
                                    </div>

                                    <Link to={`/dataset/${d.id}`} className="btn btn-outline-primary btn-sm flex-shrink-0">
                                        Ver
                                    </Link>
                                </div>
                            ))
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
}

export default Dashboard;