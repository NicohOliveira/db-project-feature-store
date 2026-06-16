import { useState, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";

function Login() {
    const [username, setUsername] = useState("");
    const [senha, setSenha] = useState("");
    const [mensagem, setMensagem] = useState("");
    const [erro, setErro] = useState(false);
    const navigate = useNavigate();

    useEffect(() => {
        if (localStorage.getItem("username") != null) {
            navigate("/dashboard");
        }
    }, []);

    const handleSubmitLogin = async (e) => {
        e.preventDefault();
        setMensagem("");

        const params = new URLSearchParams();
        params.append("username", username);
        params.append("senha", senha);

        try {
            const response = await fetch("http://localhost:8080/backend/login", {
                method: "POST",
                credentials: "include",
                body: params,
            });

            const data = await response.json();

            if (data.status === "ok") {
                localStorage.setItem("username", username);
                setErro(false);
                navigate("/dashboard");
            } else {
                setErro(true);
                setMensagem(data.mensagem);
            }
        } catch (error) {
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
                                    <h4 className="fw-bold mb-0" style={{ color: "#e0e0e0" }}>Feature Store</h4>
                                    <p className="small mt-1" style={{ color: "#888" }}>Entre com sua conta para continuar</p>
                                </div>

                                {mensagem && (
                                    <div className={`alert ${erro ? "alert-danger" : "alert-success"} py-2`} role="alert">
                                        {mensagem}
                                    </div>
                                )}

                                <form onSubmit={handleSubmitLogin}>
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
                                    <button type="submit" className="btn btn-primary btn-lg w-100 fw-bold">
                                        Entrar
                                    </button>
                                </form>

                                <hr className="my-4" style={{ borderColor: "#333" }} />
                                <p className="text-center mb-0 small" style={{ color: "#888" }}>
                                    Não possui conta?{" "}
                                    <Link to="/cadastro" className="fw-semibold text-decoration-none">
                                        Cadastre-se aqui
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

export default Login;