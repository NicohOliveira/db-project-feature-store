import { useNavigate, Link, useParams } from "react-router-dom";
import { useState, useEffect } from "react";
import HistoricoLinhagem from "../components/HistoricoLinhagem";
import DetalhesDatasetView from "../components/DetalhesDatasetView";

function DetalhesDataset() {
    const { id } = useParams();
    const [dataset, setDataset] = useState(null);
    const [estatisticas, setEstatisticas] = useState(null);
    const [aba, setAba] = useState("detalhes");
    const usuarioLogado = localStorage.getItem("username");
    const [pageContrib, setPageContrib] = useState(1);
    const [pageVersoes, setPageVersoes] = useState(1);

    useEffect(() => {
        fetch(`http://localhost:8080/backend/dataset/read?id=${id}`, {
            method: "GET",
            credentials: "include"
        })
            .then(res => res.json())
            .then(data => setDataset(data));
        fetch(`http://localhost:8080/backend/estatisticas/dataset?id=${id}&pageContrib=${pageContrib}&pageVersoes=${pageVersoes}`, {
            method: "GET",
            credentials: "include"
        })
            .then(res => res.json())
            .then(data => {
                if (data.status === "erro") console.error("Erro do servidor:", data.mensagem);
                else setEstatisticas(data);
            })
            .catch(err => console.error("Erro ao buscar estatísticas", err));
    }, [id, pageContrib, pageVersoes]);
    if (!dataset) return <p style={{ color: "#e0e0e0", padding: "20px" }}>Carregando dataset...</p>;

    const totalAcoes = estatisticas ? (estatisticas.visualizacoes + estatisticas.downloads) : 0;
    const porcVis = totalAcoes > 0 ? Math.round((estatisticas.visualizacoes / totalAcoes) * 100) : 0;
    const porcDown = totalAcoes > 0 ? Math.round((estatisticas.downloads / totalAcoes) * 100) : 0;

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
                            <span style={{ width: "200px", textAlign: "left" }}>📊 Painel Analítico</span>
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
                <div className="d-flex flex-column gap-4">
                    <div className="row">
                        <div className="col-md-4">
                            <div className="card bg-dark border-secondary shadow-sm text-center p-3 h-100">
                                <h6 className="text-secondary text-uppercase fw-bold" style={{ fontSize: "12px" }}>Total de Versões</h6>
                                <h2 className="text-light mb-0">{estatisticas?.totalVersoes || 0}</h2>
                            </div>
                        </div>
                        <div className="col-md-4">
                            <div className="card bg-dark border-secondary shadow-sm text-center p-3 h-100">
                                <h6 className="text-secondary text-uppercase fw-bold" style={{ fontSize: "12px" }}>Visualizações Totais</h6>
                                <h2 className="text-info mb-0">{estatisticas?.visualizacoes || 0}</h2>
                            </div>
                        </div>
                        <div className="col-md-4">
                            <div className="card bg-dark border-secondary shadow-sm text-center p-3 h-100">
                                <h6 className="text-secondary text-uppercase fw-bold" style={{ fontSize: "12px" }}>Downloads Totais</h6>
                                <h2 className="text-success mb-0">{estatisticas?.downloads || 0}</h2>
                            </div>
                        </div>
                    </div>

                    {/* 2. O GRÁFICO DE LINHAS DO CAIO */}
                    <div className="card bg-dark border-secondary shadow-sm">
                        <div className="card-header border-secondary bg-transparent pt-3 pb-2">
                            <h5 className="text-light mb-0">📈 Histórico de Acessos no Tempo</h5>
                        </div>
                        {/* Fundo claro no gráfico para os eixos ficarem visíveis */}
                        <div className="card-body bg-light rounded-bottom p-2">
                            <DetalhesDatasetView id={id} />
                        </div>
                    </div>

                    {/* 3. SUA TABELA DE CONTRIBUIDORES */}
                    <div className="card bg-dark border-secondary shadow-sm d-flex flex-column">
                        <div className="card-header border-secondary bg-transparent pt-3 pb-2">
                            <h5 className="text-light mb-0">🏆 Top Contribuidores Deste Dataset</h5>
                            <small className="text-secondary">Usuários que mais criaram versões.</small>
                        </div>
                        <div className="card-body p-0">
                            {estatisticas?.contribuidores && estatisticas.contribuidores.length > 0 ? (
                                <table className="table table-dark table-hover mb-0">
                                    <thead>
                                    <tr>
                                        <th className="px-4 py-3" style={{ width: "15%" }}>Posição</th>
                                        <th className="px-4 py-3">Usuário</th>
                                        <th className="px-4 py-3 text-center" style={{ width: "25%" }}>Versões Criadas</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    {estatisticas.contribuidores.map((user, index) => (
                                        <tr key={index}>
                                            <td className="px-4 py-3 text-secondary fw-bold">#{((pageContrib - 1) * 5) + index + 1}</td>
                                            <td className="px-4 py-3 fw-bold text-light">@{user.usuario}</td>
                                            <td className="px-4 py-3 text-center text-info fw-bold">{user.criacoes}</td>
                                        </tr>
                                    ))}
                                    </tbody>
                                </table>
                            ) : (
                                <p className="text-secondary p-4 mb-0 text-center">Nenhum contribuidor registrado.</p>
                            )}
                        </div>
                        <div className="card-footer border-secondary bg-transparent d-flex justify-content-between align-items-center py-3">
                            <button className="btn btn-sm btn-outline-secondary fw-bold" disabled={pageContrib === 1} onClick={() => setPageContrib(pageContrib - 1)}>
                                &laquo; Anterior
                            </button>
                            <small className="text-secondary">Página {pageContrib}</small>
                            <button className="btn btn-sm btn-outline-secondary fw-bold" disabled={!estatisticas?.contribuidores || estatisticas.contribuidores.length < 5} onClick={() => setPageContrib(pageContrib + 1)}>
                                Próxima &raquo;
                            </button>
                        </div>
                    </div>
                </div>
            )}
                {aba === "historico" && (
                    <HistoricoLinhagem id={id} />
                )}
            </div>

        </div>
    );
}

export default DetalhesDataset;