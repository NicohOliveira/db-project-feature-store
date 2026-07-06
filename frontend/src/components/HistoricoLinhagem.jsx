import React, { useEffect, useState } from "react";
import { useParams, useNavigate, useResolvedPath } from "react-router-dom";


function HistoricoLinhagem({ id }) {
    const usuarioLogado = localStorage.getItem("username");

    const [showDeleteModal, setShowDeleteModal] = useState(false);
    const [versaoParaDeletar, setVersaoParaDeletar] = useState(null);
    const [password, setPassword] = useState("");
    const [versoes, setVersoes] = useState([]);
    const [carregando, setCarregando] = useState(true);
    const [erro, setErro] = useState(null);
    
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
            .then(async (data) => {
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
            console.log(`Deletando a versão: ${versaoParaDeletar.numVersao}`);
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
                
                // Pra nao precisar recarregar tudo
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

    if (carregando) return <div className="container mt-5 text-light"><div className="spinner-border text-light" role="status"></div> Carregando histórico...</div>;
    if (erro) return <div className="container mt-5 alert alert-danger bg-dark text-danger border-danger">{erro}</div>;
    
    if (versoes.length === 0) return (
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

                <div className="list-group shadow">
                    {versoes.map((versao) => {
                        return (
                            <div
                                key={versao.numVersao}
                                className="list-group-item bg-dark text-light border-secondary p-0 mb-3 rounded"
                                style={{ transition: "all 0.3s ease" }}
                            >
                                <div className="d-flex justify-content-between align-items-center p-3">
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
                                    </div>
                                </div>
                            </div>
                        );
                    })}
                </div>
            </div>
           {showDeleteModal && versaoParaDeletar && (
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

export default HistoricoLinhagem;