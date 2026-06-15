import { useState } from "react";
import { Link } from "react-router-dom";

function CadastroUsuario() {
    const [username, setUsername] = useState("");
    const [senha, setSenha] = useState("");
    const [mensagem, setMensagem] = useState("");
    const [erro, setErro] = useState(false);

    const handleSubmitCadastro = async (e) => {
        e.preventDefault();
        setMensagem("");

        const params = new URLSearchParams();
        params.append("username", username);
        params.append("senha", senha);

        try{
            const response = await fetch("http://localhost:8080/backend/user/create", {
                method: "POST",
                body: params,
            });

            const data = await response.json();

            if(data.status === "ok"){
                setErro(false);
                setMensagem(data.mensagem);
                setUsername("");
                setSenha("");
            }
            else{
                setErro(true);
                setMensagem(data.mensagem);
            }
        }
        catch(error){
            setErro(true);
            setMensagem("Erro ao conectar com o servidor.");
        }
    };

    return (
        <div className="min-vh-100 d-flex align-items-center justify-content-start" style={{ background: "#1a1a1a" }}>
            <div className="container-fluid">
                <div className="row">
                    <div className="col-12 col-md-4 offset-md-2">
                        <div className="card border-0 shadow" style={{ background: "#222222" }}>
                            <div className="card-body p-5">

                                <div className="text-center mb-4">
                                    <h4 className="fw-bold mb-0" style={{ color: "#e0e0e0" }}>Criar Conta</h4>
                                    <p className="small mt-1" style={{ color: "#888" }}>Preencha os dados para se cadastrar</p>
                                </div>

                                {mensagem && (
                                    <div className={`alert ${erro ? "alert-danger" : "alert-success"} py-2`} role="alert">
                                        {mensagem}
                                    </div>
                                )}

                                <form onSubmit={handleSubmitCadastro}>
                                    <div className="mb-3">
                                        <label className="form-label fw-semibold" style={{ color: "#e0e0e0" }}>Usuário</label>
                                        <input
                                            type="text"
                                            className="form-control form-control-sm"
                                            placeholder="Willem_Dafoe"
                                            value={username}
                                            onChange={(e) => setUsername(e.target.value)}
                                            required
                                            style={{ background: "#2e2e2e", border: "1px solid #444", color: "#e0e0e0" }}
                                        />
                                    </div>
                                    <div className="mb-4">
                                        <label className="form-label fw-semibold" style={{ color: "#e0e0e0" }}>Senha</label>
                                        <input
                                            type="password"
                                            className="form-control form-control-sm"
                                            placeholder="••••••••"
                                            value={senha}
                                            onChange={(e) => setSenha(e.target.value)}
                                            required
                                            style={{ background: "#2e2e2e", border: "1px solid #444", color: "#e0e0e0" }}
                                        />
                                    </div>
                                    <button type="submit" className="btn btn-success btn-lg w-100 fw-bold">
                                        Cadastrar
                                    </button>
                                </form>

                                <hr className="my-4" style={{ borderColor: "#333" }} />
                                <p className="text-center mb-0 small" style={{ color: "#888" }}>
                                    Já tem uma conta?{" "}
                                    <Link to="/" className="fw-semibold text-decoration-none">
                                        Faça login
                                    </Link>
                                </p>

                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default CadastroUsuario;