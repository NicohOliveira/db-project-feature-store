import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";

function HistoricoLinhagem({ id }) {
    const usuarioLogado = localStorage.getItem("username");

    const [showDeleteModal, setShowDeleteModal] = useState(false);
    const [versaoParaDeletar, setVersaoParaDeletar] = useState(null);
    const [password, setPassword] = useState("");
    const [versoes, setVersoes] = useState([]);
    const [carregando, setCarregando] = useState(true);
    const [erro, setErro] = useState(null);
    const [versaoExpandida, setVersaoExpandida] = useState(null);
    const [featuresExpandidas, setFeaturesExpandidas] = useState({});

    const [filtroTexto, setFiltroTexto] = useState("");
    const [filtroMaturidade, setFiltroMaturidade] = useState("");
    const [filtroVersaoBase, setFiltroVersaoBase] = useState("");
    const [filtroDataInicio, setFiltroDataInicio] = useState("");
    const [filtroDataFim, setFiltroDataFim] = useState("");
    const navigate = useNavigate();

    const baixarArquivo = (numVersao) => {
        const urlDownload = `http://localhost:8080/backend/versao/download?id_dataset=${id}&num_versao=${numVersao}`;
        window.open(urlDownload, "_blank");
    };

    const registrarVisualizacao = (numVersaoClicada) => {
        fetch(`http://localhost:8080/backend/versao/read?id_dataset=${id}&num_versao=${numVersaoClicada}`, {
            method: "GET",
            credentials: "include"
        })
            .catch(err => console.error("Falha silenciosa no tracking do front:", err));
    };

    const toggleFeatures = (numVersao) => {
        setFeaturesExpandidas((prev) => ({
            ...prev,
            [numVersao]: !prev[numVersao]
        }));
    };

    const toggleExpandir = (numVersao) => {
        setVersaoExpandida(versaoExpandida === numVersao ? null : numVersao);
    };

    const renderMaturidade = (nivel) => {
        switch (nivel) {
            case 1:
                return <span className="badge" style={{ backgroundColor: "#CD7F32", color: "#fff" }}>Bronze</span>;
            case 2:
                return <span className="badge" style={{ backgroundColor: "#C0C0C0", color: "#000" }}>Prata</span>;
            case 3:
                return <span className="badge" style={{ backgroundColor: "#FFD700", color: "#000" }}>Ouro</span>;
            default:
                return <span className="badge bg-secondary text-light">Indefinido</span>;
        }
    };

    const acionarExclusao = (versao) => {
        setVersaoParaDeletar(versao);
        setShowDeleteModal(true);
    };

    const confirmarExclusao = async () => {
        if (!versaoParaDeletar || !password) {
            alert("Por favor, digite sua senha.");
            return;
        }

        try {
            const idComposto = `${id}-${versaoParaDeletar.numVersao}`;
            const response = await fetch(`http://localhost:8080/backend/versao/delete?id=${idComposto}&senha=${password}`, {
                method: "GET",
                credentials: "include"
            });

            const dados = await response.json();

            if (response.ok && dados.status !== "erro") {
                alert("Versão excluída com sucesso!");
                setShowDeleteModal(false);
                setVersaoParaDeletar(null);
                setPassword("");
                setVersoes((versoesAtuais) =>
                    versoesAtuais.filter((v) => v.numVersao !== versaoParaDeletar.numVersao)
                );
            } else {
                alert("Erro: " + (dados.mensagem || "Senha incorreta ou falha no servidor."));
            }
        } catch (error) {
            alert("Erro ao conectar no servidor.");
        }
    };

    const handleVisualizar = (numVersao) => {
        navigate(`/dataset/${id}/versao/${numVersao}`);
    };

    const buscarHistorico = () => {
        setCarregando(true);
        const params = new URLSearchParams({ id_dataset: id });
        if (filtroTexto) params.append("texto", filtroTexto);
        if (filtroMaturidade) params.append("maturidade", filtroMaturidade);
        if (filtroVersaoBase) params.append("versaoBase", filtroVersaoBase);
        if (filtroDataInicio) params.append("dataInicio", filtroDataInicio);
        if (filtroDataFim) params.append("dataFim", filtroDataFim);

        fetch(`http://localhost:8080/backend/versao/history?${params.toString()}`, {
            method: "GET",
            credentials: "include"
        })
            .then((res) => res.json())
            .then(async (data) => {
                if (data && data.length > 0) {
                    setVersoes(data.reverse());
                } else {
                    setVersoes([]);
                }
                setCarregando(false);
            })
            .catch((err) => {
                console.error(err);
                setErro("Não foi possível carregar o histórico.");
                setCarregando(false);
            });
    };

    useEffect(() => {
        buscarHistorico();
    }, [id]);

    if (carregando) return <div className="container mt-5 text-light"><div className="spinner-border text-light" role="status"></div> Carregando histórico...</div>;
    if (erro) return <div className="container mt-5 alert alert-danger bg-dark text-danger border-danger">{erro}</div>;

    const temFiltroAtivo = filtroTexto || filtroMaturidade || filtroVersaoBase || filtroDataInicio || filtroDataFim;

    if (versoes.length === 0 && !temFiltroAtivo) return (
        <div>
            <div className="container mt-5 alert alert-warning bg-dark text-warning border-warning">
                Nenhuma versão encontrada para este repositório.
            </div>
            <button className="btn btn-primary fw-bold" onClick={() => navigate(`/versao/create/${id}/${0}`)}>
                Adicionar a primeira versão para este repositório :)
            </button>
        </div>
    );

    return (
        <div className="text-light" style={{ width: "100%" }}>
            <div className="container">
                <h3 className="fw-bold text-white mb-1">Histórico de Versões</h3>

                <p className="text-secondary mb-4">Acompanhe as modificações do dataset em ordem cronológica.</p>
                <div className="card bg-dark border-secondary mb-4 shadow-sm">
                    <div className="card-body">
                        <h6 className="text-info mb-3"><i className="bi bi-funnel"></i> Filtros Avançados (SQL Dinâmico)</h6>
                        <div className="row g-2">
                            <div className="col-md-4">
                                <label className="text-secondary" style={{ fontSize: "12px" }}>Busca (Autor/Descrição)</label>
                                <input type="text" className="form-control bg-secondary text-light border-dark" placeholder="Digite..." value={filtroTexto} onChange={(e) => setFiltroTexto(e.target.value)} />
                            </div>
                            <div className="col-md-2">
                                <label className="text-secondary" style={{ fontSize: "12px" }}>Maturidade</label>
                                <select className="form-select bg-secondary text-light border-dark" value={filtroMaturidade} onChange={(e) => setFiltroMaturidade(e.target.value)}>
                                    <option value="">Todas</option>
                                    <option value="1">Bronze</option>
                                    <option value="2">Prata</option>
                                    <option value="3">Ouro</option>
                                </select>
                            </div>
                            <div className="col-md-2">
                                <label className="text-secondary" style={{ fontSize: "12px" }}>Versão Base</label>
                                <input type="number" className="form-control bg-secondary text-light border-dark" placeholder="Ex: 1" value={filtroVersaoBase} onChange={(e) => setFiltroVersaoBase(e.target.value)} />
                            </div>
                            <div className="col-md-2">
                                <label className="text-secondary" style={{ fontSize: "12px" }}>Data Inicial</label>
                                <input type="date" className="form-control bg-secondary text-light border-dark" value={filtroDataInicio} onChange={(e) => setFiltroDataInicio(e.target.value)} />
                            </div>
                            <div className="col-md-2">
                                <label className="text-secondary" style={{ fontSize: "12px" }}>Data Final</label>
                                <input type="date" className="form-control bg-secondary text-light border-dark" value={filtroDataFim} onChange={(e) => setFiltroDataFim(e.target.value)} />
                            </div>
                        </div>
                        <div className="d-flex justify-content-end mt-3 gap-2">
                            <button className="btn btn-sm btn-outline-secondary" onClick={() => {
                                setFiltroTexto(""); setFiltroMaturidade(""); setFiltroVersaoBase(""); setFiltroDataInicio(""); setFiltroDataFim("");
                            }}>Limpar</button>
                            <button className="btn btn-sm btn-primary fw-bold" onClick={buscarHistorico}>Aplicar Filtros</button>
                        </div>
                    </div>
                </div>
                {versoes.length === 0 && temFiltroAtivo ? (
                    <div className="alert alert-info bg-dark text-info border-info mt-4 text-center shadow-sm">
                        <i className="bi bi-search me-2"></i> Nenhuma versão corresponde aos filtros aplicados.
                    </div>
                ) : (
                    <div className="list-group shadow">
                        {versoes.map((versao) => {
                            const isExpanded = versaoExpandida === versao.numVersao;

                            return (
                                <div
                                    key={versao.numVersao}
                                    className="list-group-item bg-dark text-light border-secondary p-0 mb-3 rounded"
                                    style={{ transition: "all 0.3s ease" }}
                                >
                                    <div
                                        className="d-flex justify-content-between align-items-center p-3"
                                        style={{ cursor: "pointer", backgroundColor: "#212529" }}
                                        onClick={() => {
                                            if (versaoExpandida !== versao.numVersao) {
                                                registrarVisualizacao(versao.numVersao);
                                            }
                                            toggleExpandir(versao.numVersao);
                                        }}
                                    >
                                        <div>
                                            <div className="d-flex align-items-center gap-2 mb-1">
                                                <h5 className="mb-0 fw-bold text-white">Versão {versao.numVersao}</h5>

                                                {versao.numVersaoBase === 0 ? (
                                                    <span className="badge bg-success">Versão Raíz</span>
                                                ) : (
                                                    <span className="badge bg-secondary border border-light">
                                                        Derivada da V{versao.numVersaoBase}
                                                    </span>
                                                )}

                                                {renderMaturidade(versao.nivelMaturidade)}
                                            </div>
                                            <div className="text-secondary" style={{ fontSize: "14px" }}>
                                                Modificado por <span className="fw-bold text-light">{versao.usernameAutor}</span>
                                                <span className="ms-2">
                                                   em {versao.dataRegistro || ""} às {versao.horaRegistro || ""}
                                                </span>
                                            </div>
                                        </div>

                                        <div className="d-flex gap-2" onClick={(e) => e.stopPropagation()}>
                                            <button
                                                type="button"
                                                className="btn btn-sm btn-outline-light d-flex align-items-center gap-1"
                                                title="Visualizar"
                                                onClick={() => handleVisualizar(versao.numVersao)}
                                            >
                                                <i className="bi bi-eye"></i> Visualizar
                                            </button>

                                            {usuarioLogado === versao.usernameAutor && (
                                                <button
                                                    type="button"
                                                    className="btn btn-sm btn-outline-danger d-flex align-items-center gap-1"
                                                    title="Excluir"
                                                    onClick={() => acionarExclusao(versao)}
                                                >
                                                    <i className="bi bi-trash"></i> Excluir
                                                </button>
                                            )}

                                            <div className="text-secondary fw-bold fs-4 ms-2">
                                                {isExpanded ? "-" : "+"}
                                            </div>
                                        </div>
                                    </div>

                                    {isExpanded && (
                                        <div className="p-3 border-top border-secondary bg-dark">
                                            <div className="row">
                                                <div className="col-md-8">
                                                    <div className="mb-3">
                                                        <h6 className="text-uppercase text-secondary fw-bold" style={{ fontSize: "12px", letterSpacing: "1px" }}>
                                                            Descrição da Modificação
                                                        </h6>
                                                        <p className="mb-0 text-white" style={{ whiteSpace: "pre-wrap" }}>
                                                            {versao.descricaoModificacoes || "Sem descrição disponível."}
                                                        </p>
                                                    </div>

                                                    <div>
                                                        <h6 className="text-uppercase text-secondary fw-bold" style={{ fontSize: "12px", letterSpacing: "1px" }}>
                                                            Dicionário de Features
                                                        </h6>

                                                        {versao.features && versao.features.length > 0 ? (
                                                            <div className="mt-2">
                                                                <div className="table-responsive">
                                                                    <table className="table table-sm table-dark table-bordered border-secondary mb-0" style={{ fontSize: "14px" }}>
                                                                        <thead className="table-active text-secondary">
                                                                        <tr>
                                                                            <th style={{ width: "30%" }}>Coluna</th>
                                                                            <th style={{ width: "20%" }}>Tipo</th>
                                                                            <th style={{ width: "50%" }}>Descrição</th>
                                                                        </tr>
                                                                        </thead>
                                                                        <tbody>
                                                                        {(featuresExpandidas[versao.numVersao]
                                                                                ? versao.features
                                                                                : versao.features.slice(0, 3)
                                                                        ).map((feat, idx) => (
                                                                            <tr key={idx}>
                                                                                <td className="fw-bold">{feat.nomeColuna}</td>
                                                                                <td>{feat.tipoDado || "-"}</td>
                                                                                <td>{feat.descricao || "-"}</td>
                                                                            </tr>
                                                                        ))}
                                                                        </tbody>
                                                                    </table>
                                                                </div>

                                                                {versao.features.length > 3 && (
                                                                    <div className="text-center mt-2">
                                                                        <button
                                                                            className="btn btn-sm text-info text-decoration-none fw-bold"
                                                                            style={{ background: "transparent", border: "none" }}
                                                                            onClick={(e) => {
                                                                                e.stopPropagation();
                                                                                toggleFeatures(versao.numVersao);
                                                                            }}
                                                                        >
                                                                            {featuresExpandidas[versao.numVersao]
                                                                                ? "⬆ Ocultar colunas"
                                                                                : `⬇ Ver todas as ${versao.features.length} colunas`
                                                                            }
                                                                        </button>
                                                                    </div>
                                                                )}
                                                            </div>
                                                        ) : (
                                                            <p className="mb-0 text-secondary" style={{ fontStyle: "italic", fontSize: "14px" }}>
                                                                Nenhum detalhe de feature informado.
                                                            </p>
                                                        )}
                                                    </div>

                                                    <br/>

                                                    <div className="mt-2">
                                                        <h6 className="text-secondary text-uppercase" style={{ fontSize: "11px" }}>Fontes</h6>
                                                        {versao.fontes && versao.fontes.length > 0 ? (
                                                            versao.fontes.map((f, idx) => (
                                                                <span key={idx} className="badge bg-secondary me-2">
                                                                    {f.fonte}
                                                                </span>
                                                            ))
                                                        ) : (
                                                            <p className="mb-0 text-secondary" style={{ fontStyle: "italic", fontSize: "14px" }}>
                                                                Nenhuma fonte associada.
                                                            </p>
                                                        )}
                                                    </div>
                                                </div>

                                                <div className="col-md-4 d-flex flex-column align-items-end justify-content-md-end mt-3 mt-md-0 gap-2">
                                                    <button className="btn btn-primary fw-bold" onClick={() => navigate(`/versao/create/${id}/${versao.numVersao}`)}>
                                                        Criar versão a partir desta
                                                    </button>
                                                    <button
                                                        className="btn btn-danger fw-bold px-4 shadow"
                                                        onClick={(e) => {
                                                            e.stopPropagation();
                                                            setVersaoParaDeletar(versao);
                                                            setShowDeleteModal(true);
                                                        }}
                                                    >
                                                        Excluir
                                                    </button>
                                                    <button
                                                        className="btn btn-success fw-bold px-4 shadow"
                                                        onClick={(e) => {
                                                            e.stopPropagation();
                                                            baixarArquivo(versao.numVersao);
                                                        }}
                                                    >
                                                        Baixar CSV
                                                    </button>
                                                </div>
                                            </div>
                                        </div>
                                    )}
                                </div>
                            );
                        })}
                    </div>
                )}
            </div>

            {showDeleteModal && (
                <div style={{ position: "fixed", top: 0, left: 0, width: "100%", height: "100%", background: "rgba(0,0,0,0.8)", display: "flex", alignItems: "center", justifyContent: "center", zIndex: 1000 }}>
                    <div className="bg-dark p-4 rounded border border-danger text-light" style={{ width: "300px" }}>
                        <h5>Confirmar exclusão</h5>
                        <p>Versão {versaoParaDeletar.numVersao}. Digite sua senha:</p>
                        <input
                            type="password"
                            className="form-control mb-3"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                        />
                        <div className="d-flex gap-2">
                            <button className="btn btn-danger w-100" onClick={confirmarExclusao}>Confirmar</button>
                            <button className="btn btn-secondary w-100" onClick={() => setShowDeleteModal(false)}>Cancelar</button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}

export default HistoricoLinhagem;{}