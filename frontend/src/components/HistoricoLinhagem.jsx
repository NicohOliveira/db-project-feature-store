import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";


function HistoricoLinhagem({ id }) {
    const [versoes, setVersoes] = useState([]);
    const [carregando, setCarregando] = useState(true);
    const [erro, setErro] = useState(null);
    
    // estado para controlar qual versão tá expandida (aberta)
    const [versaoExpandida, setVersaoExpandida] = useState(null);
    
    const navigate = useNavigate();

    useEffect(() => {
        fetch(`http://localhost:8080/backend/versao/history?id_dataset=${id}`, {
            method: "GET",
            credentials: "include"
        })
            .then((res) => {
                if (!res.ok) throw new Error("Erro ao buscar histórico.");
                return res.json();
            })
            .then((data) => {
                if (data && data.length > 0) {
                    setVersoes(data.reverse());
                }
                setCarregando(false);
            })
            .catch((err) => {
                console.error(err);
                setErro("Não foi possível carregar o histórico.");
                setCarregando(false);
            });
    }, [id]);

    const baixarArquivo = (numVersao) => {
        const urlDownload = `http://localhost:8080/backend/versao/download?id_dataset=${id}&num_versao=${numVersao}`;
        window.open(urlDownload, "_blank");
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

    if (carregando) return <div className="container mt-5 text-light"><div className="spinner-border text-light" role="status"></div> Carregando histórico...</div>;
    if (erro) return <div className="container mt-5 alert alert-danger bg-dark text-danger border-danger">{erro}</div>;
    if (versoes.length === 0) return <div className="container mt-5 alert alert-warning bg-dark text-warning border-warning">Nenhuma versão encontrada para este repositório.</div>;

    return (
       <div className="text-light" style={{ width: "100%" }}>
            <div className="container">
                <h3 className="fw-bold text-white mb-1">Histórico de Versões</h3>
                <p className="text-secondary mb-4">Acompanhe as modificações do dataset em ordem cronológica.</p>

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
                                    onClick={() => toggleExpandir(versao.numVersao)}
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
                                        </div>
                                    </div>

                                    <div className="text-secondary fw-bold fs-4">
                                        {isExpanded ? "-" : "+"}
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
                                                        Detalhes da Feature (Colunas/Formato)
                                                    </h6>
                                                    <p className="mb-0 text-light" style={{ whiteSpace: "pre-wrap" }}>
                                                        {versao.detalhesFeature || "Nenhum detalhe técnico informado."}
                                                    </p>
                                                </div>
                                            </div>

                                            <div className="col-md-4 d-flex flex-column align-items-end justify-content-md-end mt-3 mt-md-0 gap-2">
                                                <button className="btn btn-primary fw-bold" onClick={() => navigate(`/versao/create/${id}/${versao.numVersao}`)}>
                                                    Criar versão a partir desta
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
            </div>
        </div>
    );
}

export default HistoricoLinhagem;