import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import DetalhesVersaoView from "../components/DetalhesVersaoView";

function DetalhesVersao() {
    const usuarioLogado = localStorage.getItem("username");

    const { id, idVers } = useParams();
    const navigate = useNavigate();

    const [versao, setVersao] = useState(null);
    const [carregando, setCarregando] = useState(true);
    const [erro, setErro] = useState(null);

    const [showDeleteModal, setShowDeleteModal] = useState(false);
    const [password, setPassword] = useState("");

    const [verTodasFeatures, setVerTodasFeatures] = useState(false);

    useEffect(() => {
        fetch(`http://localhost:8080/backend/registry/create?id_dataset=${id}&num_versao=${idVers}&tipo_acao=VISUALIZACAO&username_leitor=${usuarioLogado}`, {
            method: "POST",
            credentials: "include",
        })
            .catch((err) => {
                console.error("Erro ao registrar acesso:", err);
            });
    }, [id, idVers]);

    useEffect(() => {
        setCarregando(true);
        fetch(`http://localhost:8080/backend/versao/read?id_dataset=${id}&num_versao=${idVers}`, {
            method: "GET",
            credentials: "include"
        })
            .then((res) => {
                if (!res.ok) throw new Error("Erro ao buscar a versão.");
                return res.json();
            })
            .then(async (data) => {
                const dadosVersao = Array.isArray(data) ? data[0] : data;

                if (dadosVersao) {
                    try {
                        const resFonte = await fetch(
                            `http://localhost:8080/backend/source?id_dataset=${id}&num_versao=${idVers}`, 
                            { method: "GET", credentials: "include" }
                        );
                        const fontesDaVersao = resFonte.ok ? await resFonte.json() : [];
                        
                        setVersao({
                            ...dadosVersao,
                            fontes: fontesDaVersao
                        });
                    } catch (e) {
                        console.error(`Erro ao buscar fontes da versão ${idVers}:`, e);
                        setVersao({ ...dadosVersao, fontes: [] });
                    }
                } else {
                    throw new Error("Versão não encontrada.");
                }
                setCarregando(false);
            })
            .catch((err) => {
                console.error(err);
                setErro(err.message || "Não foi possível carregar a versão.");
                setCarregando(false);
            });
    }, [id, idVers]);

    const baixarArquivo = async () => {
        try {
            await fetch(`http://localhost:8080/backend/registry/create?id_dataset=${id}&num_versao=${idVers}&tipo_acao=DOWNLOAD&username_leitor=${usuarioLogado}`, {
                method: "POST",
                credentials: "include",
            });
        } catch (err) {
            console.error("Erro ao registrar download:", err);
        }

        const urlDownload = `http://localhost:8080/backend/versao/download?id_dataset=${id}&num_versao=${idVers}`;
        window.open(urlDownload, "_blank");
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

    const deletarVersao = async () => {
        try {
            const idComposto = `${id}-${idVers}`;
            const response = await fetch(`http://localhost:8080/backend/versao/delete?id=${idComposto}&senha=${password}`, {
                method: "DELETE",
                credentials: "include"
            });

            if (response.ok) {
                alert("Versão excluída com sucesso!");
                setShowDeleteModal(false);
                setPassword("");
                navigate(`/dataset/${id}`);
            } else {
                const data = await response.json();
                alert("Erro: " + (data.mensagem || "Não foi possível excluir."));
            }
        } catch (error) {
            alert("Erro ao conectar no servidor.");
        }
    };

    if (carregando) return <div className="container mt-5 text-light"><div className="spinner-border text-light" role="status"></div> Carregando detalhes da versão...</div>;
    if (erro) return <div className="container mt-5 alert alert-danger bg-dark text-danger border-danger">{erro}</div>;
    if (!versao) return <div className="container mt-5 text-light">Nenhuma informação encontrada.</div>;

    const featuresExibidas = verTodasFeatures ? (versao.features || []) : (versao.features || []).slice(0, 3);

    return (
        <div className="text-light py-4" style={{ width: "100%", background: "#1a1a1a", minHeight: "100vh" }}>
            <div className="container">
                <div className="d-flex justify-content-between align-items-center mb-4">
                    <div>
                        <h3 className="fw-bold text-white mb-1">Versão {versao.numVersao} {renderMaturidade(versao.nivelMaturidade)}</h3>
                    </div>

                    <div className="d-flex align-items-center" style={{ gap: "30px" }}>
                        <button className="btn btn-primary btn-sm" onClick={() => navigate(`/versao/create/${id}/${versao.numVersao}`)}>
                            Criar versão a partir desta
                        </button>
                                        
                        <button className="btn btn-success btn-sm" onClick={baixarArquivo}>
                            Baixar CSV
                        </button>

                        <button className="btn btn-danger btn-sm" onClick={() => setShowDeleteModal(true)}>
                            Excluir
                        </button>

                        <button className="btn btn-outline-secondary btn-sm" onClick={() => navigate(`/dataset/${id}`)}>
                            Voltar para o Dataset
                        </button>
                    </div>
                </div>

                <div className="card bg-dark text-light border-secondary p-4 mb-4 shadow-sm">
                    <div className="row g-4">
                        <div className="col-12 col-lg-5 border-end-lg border-secondary">
                            <div className="d-flex justify-content-center align-items-center gap-3 mb-3">
                                {versao.numVersaoBase === 0 ? (
                                    <span className="badge bg-success fs-6">Versão Raiz</span>
                                ) : (
                                    <span className="badge bg-secondary border border-light fs-6">Derivada da versão {versao.numVersaoBase}</span>
                                )}
                            </div>

                            <div className="text-secondary mb-4" style={{ fontSize: "14px" }}>
                                Modificado por <span className="fw-bold text-light">{versao.usernameAutor || "Autor desconhecido"}</span>
                            </div>

                            <hr className="border-secondary my-3" />

                            <div className="mb-4">
                                <h6 className="text-uppercase text-secondary fw-bold mb-2" style={{ fontSize: "12px", letterSpacing: "1px" }}>
                                    Descrição da Modificação
                                </h6>
                                <p className="mb-0 text-white" style={{ whiteSpace: "pre-wrap", fontSize: "15px" }}>
                                    {versao.descricaoModificacoes || "Sem descrição disponível."}
                                </p>
                            </div>

                            <div className="mb-3">
                                <h6 className="text-secondary text-uppercase fw-bold mb-2" style={{ fontSize: "11px", letterSpacing: "1px" }}>Fontes</h6>
                                {versao.fontes && versao.fontes.length > 0 ? (
                                    <div className="d-flex flex-wrap gap-2">
                                        {versao.fontes.map((f, idx) => (
                                            <span key={idx} className="badge bg-secondary p-2">
                                                {f.fonte}
                                            </span>
                                        ))}
                                    </div>
                                ) : (
                                    <p className="mb-0 text-secondary" style={{ fontStyle: "italic", fontSize: "14px" }}>
                                        Nenhuma fonte associada.
                                    </p>
                                )}
                            </div>
                        </div>

                        <div className="col-12 col-lg-7">
                            <h6 className="text-uppercase text-secondary fw-bold mb-3" style={{ fontSize: "12px", letterSpacing: "1px" }}>
                                Dicionário de Features
                            </h6>

                            {versao.features && versao.features.length > 0 ? (
                                <div>
                                    <div className="table-responsive">
                                        <table className="table table-sm table-dark table-bordered border-secondary mb-0" style={{ fontSize: "14px" }}>
                                            <thead className="table-active text-secondary">
                                                <tr>
                                                    <th style={{ width: "30%" }}>Coluna</th>
                                                    <th style={{ width: "15%" }}>Tipo</th>
                                                    <th style={{ width: "45%" }}>Descrição</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                {featuresExibidas.map((feat, idx) => (
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
                                                onClick={() => setVerTodasFeatures(!verTodasFeatures)}
                                            >
                                                {verTodasFeatures ? "⬆ Ocultar colunas" : `⬇ Ver todas as ${versao.features.length} colunas`}
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

                    </div>

                    <DetalhesVersaoView id={id} numVersao={idVers} />
                </div>
            </div>

            {showDeleteModal && (
                <div style={{ position: "fixed", top: 0, left: 0, width: "100%", height: "100%", background: "rgba(0,0,0,0.8)", display: "flex", alignItems: "center", justifyContent: "center", zIndex: 1000 }}>
                    <div className="bg-dark p-4 rounded border border-danger text-light" style={{ width: "350px" }}>
                        <h5>Confirmar exclusão</h5>
                        <p>Deseja realmente excluir a <strong>Versão {idVers}</strong>? Digite sua senha para confirmar:</p>
                        <input
                            type="password"
                            className="form-control mb-3 bg-secondary text-white border-dark"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                        />
                        <div className="d-flex gap-2">
                            <button className="btn btn-danger w-100" onClick={deletarVersao}>Confirmar</button>
                            <button className="btn btn-secondary w-100" onClick={() => { setShowDeleteModal(false); setPassword(""); }}>Cancelar</button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}

export default DetalhesVersao;