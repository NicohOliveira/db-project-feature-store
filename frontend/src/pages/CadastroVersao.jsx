import { useState, useEffect } from "react";
import { useNavigate, useParams, Link } from "react-router-dom";

function CadastroVersao() {
    const { idData, idVers } = useParams();
    
    const navigate = useNavigate();
    
    const [mensagem, setMensagem] = useState("");
    
    const [versaoBase, setVersaoBase] = useState(null);
    const [descricao, setDescricao] = useState("");
    
    const [features, setFeatures] = useState([]);
    const [fontes, setFontes] = useState([]);

    const [arquivo, setArquivo] = useState(null);
    const [carregando, setCarregando] = useState(true);
    const [erro, setErro] = useState(null);

    useEffect(() => {
        if (idVers === "0") {
            setCarregando(false);
            return;
        }

        fetch(`http://localhost:8080/backend/versao/read?id_dataset=${idData}&num_versao=${idVers}`, {
            method: "GET",
            credentials: "include"
        })
            .then((res) => {
                if (!res.ok) throw new Error("Erro ao buscar versão base.");
                return res.json();
            })
            .then((data) => {
                setVersaoBase(data);
                setCarregando(false);
            })
            .catch((err) => {
                console.error(err);
                setErro("Não foi possível carregar a versão base.");
                setCarregando(false);
            });
    }, [idData, idVers]);

    const handleAddFeature = () => {
        setFeatures([...features, { nomeColuna: "", tipoDado: "", descricao: "" }]);
    };

    const handleAddFonte = () => {
        setFontes([...fontes, { nome: "" }]);
    };

    const handleRemoveFeature = (index) => {
        const novasFeatures = features.filter((_, i) => i !== index);
        setFeatures(novasFeatures);
    };

    const handleRemoveFonte = (index) => {
        const novasFontes = fontes.filter((_, i) => i !== index);
        setFontes(novasFontes);
    }

    const handleFeatureChange = (index, campo, valor) => {
        const novasFeatures = [...features];
        novasFeatures[index][campo] = valor;
        setFeatures(novasFeatures);
    };

    const handleFonteChange = (index, campo, valor) => {
        const novasFontes = [...fontes];
        novasFontes[index][campo] = valor;
        setFontes(novasFontes);
    };

    const handleSalvar = async (e) => {
        e.preventDefault();

        try {
            const dadosVers = new FormData();

            dadosVers.append("id_dataset", idData);
            dadosVers.append("num_versao_base", idVers);
            dadosVers.append("descricao_modificacoes", descricao);

            dadosVers.append("features", JSON.stringify(features));

            if (arquivo) dadosVers.append("arquivo", arquivo);

            console.log(">>> ENVIANDO PRO JAVA:", JSON.stringify(features));

            const username = localStorage.getItem("username");
            if (!username) {
                setMensagem("Usuário não autenticado.");
                return;
            }
            dadosVers.append("username_autor", username);

            const responseVers = await fetch("http://localhost:8080/backend/versao/create", {
                method: "POST",
                credentials: "include",
                body: dadosVers
            });

            const dataVers = await responseVers.json();

            if (dataVers.status !== "ok") {
                throw new Error(dataVers.mensagem || "Erro ao criar versão.");
            }

            const dadosFonte = new URLSearchParams();

            dadosFonte.append("datasetId", idData);
            dadosFonte.append("versao", dataVers.numVersao);

            fontes.forEach(f => { dadosFonte.append("fontes", f.nome); });

            const responseFonte = await fetch("http://localhost:8080/backend/source/create", {
                method: "POST",
                credentials: "include",
                headers: { "Content-Type": "application/x-www-form-urlencoded" },
                body: dadosFonte.toString()
            });

            const dataFonte = await responseFonte.json();

            if (dataFonte.status === "ok") {
                setMensagem("Versão criada com sucesso.");
                setTimeout(() => navigate(`/dataset/${idData}`), 1500); // Volta pro histórico após criar
            } else {
                alert("Erro: " + dataFonte.mensagem);
            }
        } catch (error) {
            console.error("Erro na requisição:", error);
            setMensagem("Erro ao conectar com o servidor.");
        }
    };

    if (carregando) return <p style={{ color: "#e0e0e0" }}>Carregando...</p>;
    if (erro) return <p style={{ color: "red" }}>{erro}</p>;

    return (
        <div className="d-flex min-vh-100 justify-content-center align-items-center py-5" style={{ background: "#1a1a1a" }}>
            <div className="card shadow p-4 border-0" style={{ width: "100%", maxWidth: "800px", background: "#222222" }}>
                <div className="d-flex justify-content-between align-items-center mb-4">
                    <button onClick={() => navigate(-1)} className="btn btn-outline-secondary btn-sm">
                        Voltar
                    </button>
                </div>

                <h3 className="mb-1" style={{ color: "#e0e0e0" }}>Nova Versão</h3>
                { idVers > 0 ? (
                    <p style={{ color: "#888" }}>Derivada da Versão {idVers}</p>
                ) : (
                    <p style={{ color: "#888" }}>Primeira versão do repositório!</p>
                ) }

                <br/>

                {mensagem && (
                    <div className={`alert ${erro ? "alert-danger" : "alert-success"} py-2`} role="alert">
                        {mensagem}
                    </div>
                )}

                <form onSubmit={handleSalvar}>
                    <div className="mb-3">
                        <label className="form-label fw-bold" style={{ color: "#e0e0e0" }}>Descrição das modificações</label>
                        <textarea
                            className="form-control"
                            rows={2}
                            value={descricao}
                            onChange={(e) => setDescricao(e.target.value)}
                            placeholder="Descreva o que mudou nesta versão..."
                            style={{ background: "#2e2e2e", border: "1px solid #444", color: "#e0e0e0" }}
                        />
                    </div>

                    {/* --- SESSÃO DINÂMICA DAS FEATURES --- */}
                    <div className="mb-4 p-3 rounded" style={{ background: "#1e1e1e", border: "1px solid #333" }}>
                        <div className="d-flex justify-content-between align-items-center mb-3">
                            <label className="form-label fw-bold mb-0" style={{ color: "#e0e0e0" }}>Mapeamento de Features (Opcional)</label>
                            <button type="button" className="btn btn-sm btn-outline-info fw-bold" onClick={handleAddFeature}>
                                + Adicionar Coluna
                            </button>
                        </div>

                        {features.length === 0 && (
                            <p className="text-secondary small mb-0">Nenhuma feature detalhada. Clique no botão acima para adicionar.</p>
                        )}

                        {features.map((feat, index) => (
                            <div key={index} className="row g-2 mb-2 align-items-center">
                                <div className="col-md-3">
                                    <input type="text" className="form-control form-control-sm" placeholder="Nome da Coluna" value={feat.nomeColuna} onChange={(e) => handleFeatureChange(index, "nomeColuna", e.target.value)} style={{ background: "#2e2e2e", border: "1px solid #444", color: "#e0e0e0" }} required />
                                </div>
                                <div className="col-md-3">
                                    <input type="text" className="form-control form-control-sm" placeholder="Tipo (ex: INT, VARCHAR)" value={feat.tipoDado} onChange={(e) => handleFeatureChange(index, "tipoDado", e.target.value)} style={{ background: "#2e2e2e", border: "1px solid #444", color: "#e0e0e0" }} />
                                </div>
                                <div className="col-md-5">
                                    <input type="text" className="form-control form-control-sm" placeholder="Descrição" value={feat.descricao} onChange={(e) => handleFeatureChange(index, "descricao", e.target.value)} style={{ background: "#2e2e2e", border: "1px solid #444", color: "#e0e0e0" }} />
                                </div>
                                <div className="col-md-1 text-end">
                                    <button type="button" className="btn btn-sm btn-outline-danger" onClick={() => handleRemoveFeature(index)}>X</button>
                                </div>
                            </div>
                        ))}
                    </div>
                    {/* ------------------------------------ */}

                    <div className="mb-4">
                        <label className="form-label fw-bold" style={{ color: "#e0e0e0" }}>Arquivo CSV</label>
                        <input
                            type="file"
                            className="form-control"
                            accept=".csv"
                            onChange={(e) => setArquivo(e.target.files[0])}
                            required
                            style={{ background: "#2e2e2e", border: "1px solid #444", color: "#e0e0e0" }}
                        />
                    </div>

                    <div className="mb-4 p-3 rounded" style={{ background: "#1e1e1e", border: "1px solid #333" }}>
                        <div className="d-flex justify-content-between align-items-center mb-3">
                            <label className="form-label fw-bold mb-0" style={{ color: "#e0e0e0" }}>Associação de fontes</label>
                            <button type="button" className="btn btn-sm btn-outline-info fw-bold" onClick={handleAddFonte}>
                                + Adicionar fonte
                            </button>
                        </div>

                        {fontes.length === 0 && (
                            <p className="text-secondary small mb-0">Nenhuma fonte adicionada.</p>
                        )}

                        {fontes.map((fonte, index) => (
                            <div key={index} className="row g-2 mb-2 align-items-center">
                                <div className="col">
                                    <input type="text" className="form-control form-control-sm" placeholder="Ex: IBGE, Wikipedia.com, hyperlink, etc" value={fonte.nome} onChange={(e) => handleFonteChange(index, "nome", e.target.value)} style={{ background: "#2e2e2e", border: "1px solid #444", color: "#e0e0e0" }} required />
                                </div>
                                <div className="col-md-1 text-end">
                                    <button type="button" className="btn btn-sm btn-outline-danger" onClick={() => handleRemoveFonte(index)}>X</button>
                                </div>
                            </div>
                        ))}
                    </div>

                    <button type="submit" className="btn btn-success w-100 fw-bold fs-5 py-2">
                        Criar Versão
                    </button>
                </form>
            </div>
        </div>
    );
}

export default CadastroVersao;