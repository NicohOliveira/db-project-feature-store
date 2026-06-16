import { useState, useEffect } from "react";
import { useNavigate, useParams, Link } from "react-router-dom";

function CadastroVersao() {
    const { idData, idVers } = useParams();
    const navigate = useNavigate();

    const [mensagem, setMensagem] = useState("");

    const [versaoBase, setVersaoBase] = useState(null);
    const [descricao, setDescricao] = useState("");
    const [detalhesFeature, setDetalhesFeature] = useState("");
    const [arquivo, setArquivo] = useState(null);
    const [carregando, setCarregando] = useState(true);
    const [erro, setErro] = useState(null);

    useEffect(() => {
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

    const handleSalvar = async (e) => {
        e.preventDefault();

        try {
            const dados = new FormData();

            dados.append("id_dataset", idData);
            dados.append("num_versao_base", idVers);
            dados.append("descricao_modificacoes", descricao);
            dados.append("detalhes_feature", detalhesFeature);
            
            if (arquivo) dados.append("arquivo", arquivo);
            
            const username = localStorage.getItem("username");
            if (!username) {
                setMensagem("Usuário não autenticado.");
                return;
            }
            dados.append("username_autor", username);

            const response = await fetch("http://localhost:8080/backend/versao/create", {
                method: "POST",
                credentials: "include",
                body: dados
            });

            const data = await response.json();

            if (response.ok && data.status === "ok") {
                setMensagem("Versão criada com sucesso.");
            } else {
                alert("Erro: " + data.mensagem);
            }
        } catch (error) {
            console.error("Erro na requisição:", error);
            setMensagem("Erro ao conectar com o servidor.");
        }
    };

    if (carregando) return <p style={{ color: "#e0e0e0" }}>Carregando...</p>;
    if (erro) return <p style={{ color: "red" }}>{erro}</p>;

    return (
        <div className="d-flex vh-100 justify-content-center align-items-center" style={{ background: "#1a1a1a" }}>
            <div className="card shadow p-4 border-0" style={{ width: "100%", maxWidth: "600px", background: "#222222" }}>
                <div className="d-flex justify-content-between align-items-center mb-4">
                    <Link to="/dashboard" className="btn btn-outline-secondary btn-sm">
                        Voltar
                    </Link>
                </div>

                <h3 className="mb-1" style={{ color: "#e0e0e0" }}>Nova Versão</h3>
                <p style={{ color: "#888" }}>Derivada da Versão {idVers}</p>

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
                            placeholder="Foi modificado o sabor das batatas, bem como outros ingredientes..."
                            style={{ background: "#2e2e2e", border: "1px solid #444", color: "#e0e0e0" }}
                        />
                    </div>

                    <div className="mb-3">
                        <label className="form-label fw-bold" style={{ color: "#e0e0e0" }}>Detalhes da Feature</label>
                        <input
                            type="text"
                            className="form-control"
                            value={detalhesFeature}
                            onChange={(e) => setDetalhesFeature(e.target.value)}
                            placeholder="Coluninhas, batatas..."
                            style={{ background: "#2e2e2e", border: "1px solid #444", color: "#e0e0e0" }}
                        />
                    </div>

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

                    <button type="submit" className="btn btn-success w-100 fw-bold fs-5 py-2">
                        Criar Versão
                    </button>
                </form>
            </div>
        </div>
    );
}

export default CadastroVersao;