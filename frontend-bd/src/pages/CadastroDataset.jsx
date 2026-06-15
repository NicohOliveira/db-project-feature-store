import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";

function CadastroDataset() {
    const [nome, setNome] = useState("");
    const navigate = useNavigate();

    const handleSalvar = async (e) => {
        e.preventDefault();

        try{
            const dados = new URLSearchParams();
            dados.append("nome", nome);
            const response = await fetch("http://localhost:8080/bd2026/dataset/create", {
                method: "POST",
                credentials: "include",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded",
                },
                body: dados.toString()
            });

            const data = await response.json();

            if (response.ok && data.status === "ok") {
                alert("Repositório criado com sucesso!");
                navigate("/dashboard");
            } else {
                alert("Erro do Java: " + data.mensagem);
            }
        } catch (error) {
            console.error("Erro na requisição:", error);
            alert("Erro ao conectar. O servidor Java tá rodando mesmo? :c ");
        }

        navigate("/dashboard");
    };

    return (
        <div className="d-flex vh-100 justify-content-center align-items-center" style={{ background: "#1a1a1a" }}>
            <div className="card shadow p-4 border-0" style={{ width: "100%", maxWidth: "600px", background: "#222222" }}>
                <div className="d-flex justify-content-between align-items-center mb-4">
                    <h3 className="mb-0" style={{ color: "#e0e0e0" }}>Novo Dataset</h3>
                    <Link to="/dashboard" className="btn btn-outline-secondary btn-sm">
                        Voltar
                    </Link>
                </div>

                <form onSubmit={handleSalvar}>
                    <div className="mb-4">
                        <label className="form-label fw-bold" style={{ color: "#e0e0e0" }}>Nome do Dataset</label>
                        <input
                            type="text"
                            className="form-control"
                            value={nome}
                            onChange={(e) => setNome(e.target.value)}
                            placeholder="Ex: dados_climaticos_2026"
                            required
                            style={{ background: "#2e2e2e", border: "1px solid #444", color: "#e0e0e0" }}
                        />
                        <small style={{ color: "#888" }}>
                            a primeira versão (bronze) será inserida depois
                        </small>
                    </div>

                    <button type="submit" className="btn btn-success w-100 fw-bold fs-5 py-2">
                        Criar Repositório
                    </button>
                </form>
            </div>
        </div>
    );
}

export default CadastroDataset;