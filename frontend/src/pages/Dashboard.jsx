import { useNavigate, Link } from "react-router-dom";
import { useState, useEffect } from "react";

function Dashboard() {
    const navigate = useNavigate();
    const usuarioLogado = localStorage.getItem("username");
    const [datasets, setDatasets] = useState([]);
    const [modo, setModo] = useState("estatisticas");
    const [estatisticas, setEstatisticas] = useState(null);
    const [pageContrib, setPageContrib] = useState(1);
    const [pageDatasets, setPageDatasets] = useState(1);

    const datasetsFiltrados = modo === "meus" ? datasets.filter(d => d.username_criador === usuarioLogado) : datasets;

    const handleLogout = () => {
        localStorage.removeItem("username");
        navigate("/");
    };


    useEffect(() => {
        fetch("http://localhost:8080/backend/dataset", {
            method: "GET",
            credentials: "include"
        })
            .then(res => res.json())
            .then(data => setDatasets(data))
            .catch(err => console.error("Erro ao buscar datasets:", err));
    }, []);

    useEffect(() => {
        fetch(`http://localhost:8080/backend/estatisticas/geral?pageContrib=${pageContrib}&pageDatasets=${pageDatasets}`, {
            method: "GET",
            credentials: "include"
        })
            .then(res => res.json())
            .then(data => {
                if (data.status === "erro") console.error("Erro do servidor:", data.mensagem);
                else setEstatisticas(data);
            })
            .catch(err => console.error("Erro ao buscar estatísticas globais:", err));
    }, [pageContrib, pageDatasets]);


    const totalAcoes = estatisticas ? (estatisticas.totalVisualizacoes + estatisticas.totalDownloads) : 0;
    const porcVis = totalAcoes > 0 ? Math.round((estatisticas.totalVisualizacoes / totalAcoes) * 100) : 0;
    const porcDown = totalAcoes > 0 ? Math.round((estatisticas.totalDownloads / totalAcoes) * 100) : 0;

    return (
        <div className="d-flex vh-100" style={{ background: "#1a1a1a" }}>

            <div className="d-flex flex-column flex-shrink-0 p-3" style={{ width: "280px", background: "#222222", borderRight: "1px solid #333333" }}>
                <span className="fs-4 fw-bold mb-4 text-center border-bottom pb-3" style={{ color: "#e0e0e0", borderColor: "#333333" }}>
                    Feature Store
                </span>

                <div className="d-flex flex-row align-items-center justify-content-center gap-3 mb-4">
                    <div className="rounded-circle bg-primary d-flex justify-content-center align-items-center flex-shrink-0" style={{ width: "45px", height: "45px" }}>
                        <span className="fw-bold text-white fs-5">
                            {usuarioLogado ? usuarioLogado.charAt(0).toUpperCase() : "?"}
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
                            className={`nav-link w-100 d-flex align-items-center gap-2 ${modo === "estatisticas" ? "active" : ""}`}
                            style={modo !== "estatisticas" ? { color: "#aaa" } : {}}
                            onClick={() => setModo("estatisticas")}
                        >
                            <span style={{ width: "24px", textAlign: "center" }}>🌍</span>
                            <span>Visão Global</span>
                        </button>
                    </li>
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
                    <h2 style={{ color: "#e0e0e0" }}>
                        {modo === "estatisticas" ? "Painel Global da Plataforma" : (modo === "meus" ? "Meus Datasets" : "Todos os Datasets")}
                    </h2>
                    <Link to="/cadastro-dataset" className="btn btn-success fw-bold">
                        + Novo Dataset
                    </Link>
                </div>

                {modo === "estatisticas" && (
                    <div>
                        {!estatisticas ? (
                            <p style={{ color: "#888" }}>Carregando estatísticas da plataforma...</p>
                        ) : (
                            <>
                                <div className="row mb-5">
                                    <div className="col-md-3">
                                        <div className="card bg-dark border-secondary shadow-sm">
                                            <div className="card-body text-center">
                                                <h6 className="text-secondary text-uppercase fw-bold" style={{ fontSize: "12px" }}>Total Datasets</h6>
                                                <h2 className="text-light mb-0">{estatisticas.totalDatasets || 0}</h2>
                                            </div>
                                        </div>
                                    </div>
                                    <div className="col-md-3">
                                        <div className="card bg-dark border-secondary shadow-sm">
                                            <div className="card-body text-center">
                                                <h6 className="text-secondary text-uppercase fw-bold" style={{ fontSize: "12px" }}>Total Versões</h6>
                                                <h2 className="text-light mb-0">{estatisticas.totalVersoes || 0}</h2>
                                            </div>
                                        </div>
                                    </div>
                                    <div className="col-md-3">
                                        <div className="card bg-dark border-secondary shadow-sm">
                                            <div className="card-body text-center">
                                                <h6 className="text-secondary text-uppercase fw-bold" style={{ fontSize: "12px" }}>Visualizações</h6>
                                                <h2 className="text-info mb-0">{estatisticas.totalVisualizacoes || 0}</h2>
                                            </div>
                                        </div>
                                    </div>
                                    <div className="col-md-3">
                                        <div className="card bg-dark border-secondary shadow-sm">
                                            <div className="card-body text-center">
                                                <h6 className="text-secondary text-uppercase fw-bold" style={{ fontSize: "12px" }}>Downloads</h6>
                                                <h2 className="text-success mb-0">{estatisticas.totalDownloads || 0}</h2>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <div className="card bg-dark border-secondary mb-5 shadow-sm">
                                    <div className="card-body">
                                        <h5 className="text-light mb-3">Engajamento Global da Plataforma</h5>
                                        {totalAcoes === 0 ? (
                                            <p className="text-secondary mb-0">Nenhuma interação registrada no sistema ainda.</p>
                                        ) : (
                                            <>
                                                <div className="progress" style={{ height: "30px", backgroundColor: "#333" }}>
                                                    <div className="progress-bar bg-info fw-bold fs-6" role="progressbar" style={{ width: `${porcVis}%` }} title="Visualizações">
                                                        {porcVis}% Views
                                                    </div>
                                                    <div className="progress-bar bg-success fw-bold fs-6" role="progressbar" style={{ width: `${porcDown}%` }} title="Downloads">
                                                        {porcDown}% Downs
                                                    </div>
                                                </div>
                                                <small className="text-secondary d-block mt-2">
                                                    Conversão média da plataforma: de todas as interações do sistema, <strong>{porcDown}%</strong> se transformam em downloads efetivos.
                                                </small>
                                            </>
                                        )}
                                    </div>
                                </div>

                                <div className="row">
                                    <div className="col-md-12 mb-4">
                                        <div className="card bg-dark border-secondary shadow-sm d-flex flex-column">
                                            <div className="card-header border-secondary bg-transparent pt-3 pb-2">
                                                <h5 className="text-light mb-0">🏆 Top Contribuidores Globais</h5>
                                                <small className="text-secondary">Usuários que mais enriquecem a plataforma com novas versões.</small>
                                            </div>
                                            <div className="card-body p-0">
                                                {estatisticas.rankingContribuidoresGlobais && estatisticas.rankingContribuidoresGlobais.length > 0 ? (
                                                    <table className="table table-dark table-hover mb-0">
                                                        <thead>
                                                        <tr>
                                                            <th className="px-4 py-3" style={{ width: "15%" }}>Posição</th>
                                                            <th className="px-4 py-3">Usuário</th>
                                                            <th className="px-4 py-3 text-center" style={{ width: "25%" }}>Versões Criadas no Sistema</th>
                                                        </tr>
                                                        </thead>
                                                        <tbody>
                                                        {estatisticas.rankingContribuidoresGlobais.map((user, index) => (
                                                            <tr key={index}>
                                                                <td className="px-4 py-3 text-secondary fw-bold">#{((pageContrib - 1) * 5) + index + 1}</td>
                                                                <td className="px-4 py-3 fw-bold text-light">@{user.usuario}</td>
                                                                <td className="px-4 py-3 text-center text-info fw-bold">{user.criacoes}</td>
                                                            </tr>
                                                        ))}
                                                        </tbody>
                                                    </table>
                                                ) : (
                                                    <p className="text-secondary p-4 mb-0 text-center">Nenhum contribuidor encontrado.</p>
                                                )}
                                            </div>
                                            <div className="card-footer border-secondary bg-transparent d-flex justify-content-between align-items-center py-3">
                                                <button
                                                    className="btn btn-sm btn-outline-secondary fw-bold"
                                                    disabled={pageContrib === 1}
                                                    onClick={() => setPageContrib(pageContrib - 1)}
                                                >
                                                    &laquo; Anterior
                                                </button>
                                                <small className="text-secondary">Página {pageContrib}</small>
                                                <button
                                                    className="btn btn-sm btn-outline-secondary fw-bold"
                                                    disabled={!estatisticas.rankingContribuidoresGlobais || estatisticas.rankingContribuidoresGlobais.length < 5}
                                                    onClick={() => setPageContrib(pageContrib + 1)}
                                                >
                                                    Próxima &raquo;
                                                </button>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </>
                        )}
                    </div>
                )}

                {(modo === "meus" || modo === "todos") && (
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
                )}
            </div>
        </div>
    );
}

export default Dashboard;