import { useNavigate, Link, useParams } from "react-router-dom";
import { useState, useEffect } from "react";
import HistoricoLinhagem from "../components/HistoricoLinhagem";

function DetalhesDataset() {
    const { id } = useParams();
    const [dataset, setDataset] = useState(null);
    const [aba, setAba] = useState("detalhes");
    const usuarioLogado = localStorage.getItem("username");

    useEffect(() => {
        fetch(`http://localhost:8080/backend/dataset/read?id=${id}`)
            .then(res => res.json())
            .then(data => setDataset(data));
    }, [id]);

    if (!dataset) return <p style={{ color: "#e0e0e0" }}>Carregando...</p>;

    return (
        <div className="d-flex vh-100" style={{ background: "#1a1a1a" }}>
            <div className="d-flex flex-column flex-shrink-0 p-3" style={{ width: "280px", background: "#222222", borderRight: "1px solid #333333" }}>
                <span className="fs-4 fw-bold mb-4 text-center border-bottom pb-3" style={{ color: "#e0e0e0", borderColor: "#333333" }}>
                    {dataset.nome}
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
                            className={`nav-link w-100 d-flex align-items-center gap-2 ${aba === "detalhes" ? "active" : ""}`}
                            style={aba !== "detalhes" ? { color: "#aaa" } : {}}
                            onClick={() => setAba("detalhes")}
                        >
                            <span style={{ width: "200px", textAlign: "center" }}>Detalhes</span>
                        </button>
                    </li>
                    <li className="nav-item mb-2">
                        <button
                            className={`nav-link w-100 d-flex align-items-center gap-2 ${aba === "historico" ? "active" : ""}`}
                            style={aba !== "historico" ? { color: "#aaa" } : {}}
                            onClick={() => setAba("historico")}
                        >
                            <span style={{ width: "200px", textAlign: "center" }}>Histórico de Versões</span>
                        </button>
                    </li>
                </ul>

                <hr style={{ borderColor: "#333333" }} />
                <Link to="/dashboard" className="btn btn-outline-secondary w-100 fw-bold">
                    Voltar ao dashboard
                </Link>
            </div>

            <div className="flex-grow-1 p-5 overflow-auto" style={{ background: "#1a1a1a" }}>
                {aba === "detalhes" && (
                    <p style={{ color: "#888" }}>em construção...</p>
                )}
                {aba === "historico" && (
                    <HistoricoLinhagem id={id} />
                )}
            </div>

        </div>
    );
}

export default DetalhesDataset;