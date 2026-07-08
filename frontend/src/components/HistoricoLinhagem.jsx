import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";

import { Box } from "@mui/material";

import * as bootstrap from "bootstrap";

function HistoricoLinhagem({ id }) {
    const usuarioLogado = localStorage.getItem("username");

    const [showDeleteModal, setShowDeleteModal] = useState(false);
    const [versaoParaDeletar, setVersaoParaDeletar] = useState(null);
    const [password, setPassword] = useState("");
    const [versoes, setVersoes] = useState([]);
    const [carregando, setCarregando] = useState(true);
    const [erro, setErro] = useState(null);

    const [filtroTexto, setFiltroTexto] = useState("");
    const [filtroMaturidade, setFiltroMaturidade] = useState("");
    const [filtroVersaoBase, setFiltroVersaoBase] = useState("");
    const [filtroDataInicio, setFiltroDataInicio] = useState("");
    const [filtroDataFim, setFiltroDataFim] = useState("");
    const navigate = useNavigate();

    useEffect(() => {
        const tooltipTriggerList = document.querySelectorAll(
            '[data-bs-toggle="tooltip"]'
        );

        tooltipTriggerList.forEach((el) => {
            bootstrap.Tooltip.getOrCreateInstance(el);
        });
    }, [versoes]);

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
                <div className="card bg-dark border-secondary mb-4">
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
                    <div className="list-group p-2">
                        {versoes.map((versao) => {
                            return (
                                <div
                                    key={versao.numVersao}
                                    className="list-group-item shadow bg-dark text-light border-secondary p-1 mb-3 rounded"
                                    style={{ transition: "all 0.3s ease" }}
                                >
                                    <button
                                        className="btn btn-dark"
                                        onClick={() => handleVisualizar(versao.numVersao)}
                                        style={{ width: "100%" }}
                                    >
                                        <Box
                                            sx={{
                                                display: "flex",
                                                justifyContent: "space-between",
                                                gap: 3,
                                                p: 3,
                                                mb: -1,
                                                mt: -1
                                            }}
                                        >

                                            <div style={{ width: "40%" }}>
                                                <div className="align-items-center gap-2 mb-1">
                                                    <span className="mb-0 fw-bold text-white" style={{ fontSize: "20px" }}>
                                                        Versão {versao.numVersao}
                                                    </span>
                                                    <span className="ms-2">
                                                        {renderMaturidade(versao.nivelMaturidade)}
                                                    </span>
                                                </div>

                                                <div className="text-secondary" style={{ fontSize: "14px" }}>
                                                    Modificado por <span className="fw-bold text-light">{versao.usernameAutor}</span>
                                                    <span className="ms-2">
                                                    em {versao.dataRegistro || ""} às {versao.horaRegistro || ""}
                                                    </span>
                                                </div>
                                            </div>

                                            <div className="d-flex flex-column" >
                                                <button type="button" className="btn rounded"
                                                    data-bs-toggle="tooltip" data-bs-placement="left"
                                                    data-bs-custom-className="custom-tooltip"
                                                    data-bs-title={versao.numVersaoBase === 0 ? (
                                                            "Versão Raíz"
                                                        ) : (
                                                            `Derivada da versão ${versao.numVersaoBase}`
                                                        )} 
                                                    style={{  width: "100%" }}
                                                >

                                                    <h5 style={{
                                                            display: "inline-flex",
                                                            alignItems: "center",
                                                            justifyContent: "center",
                                                            height: "100%",
                                                            width: "33px",
                                                            borderRadius: "50%",
                                                            backgroundColor: "#8792a169",
                                                            color: "white",
                                                            fontWeight: "bold",
                                                            fontSize: "20px",
                                                            marginLeft: "6px",
                                                            }}
                                                        > 
                                                            ?
                                                    </h5>

                                                </button>
                                            </div>

                                        </Box>
                                    </button>
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