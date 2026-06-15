import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";


function Login() {
    const [username, setUsername] = useState("");
    const [senha, setSenha] = useState("");
    const [mensagem, setMensagem] = useState("");
    const [erro, setErro] = useState(false);
    const navigate = useNavigate();

    const handleSubmitLogin = async (e) => {
        e.preventDefault();
        setMensagem("");

        const params = new URLSearchParams();
        params.append("username", username);
        params.append("senha", senha);

        try {
            const response = await fetch("http://localhost:8080/bd2026/login", {
                method: "POST",
                credentials: "include",
                body: params,
            });

            const data = await response.json();

            if (data.status === "ok") {
                localStorage.setItem("username", username);
                setErro(false);
                setMensagem(data.mensagem);
                setUsername("");
                setSenha("");
                navigate("/dashboard");
            } else {
                setErro(true);
                setMensagem(data.mensagem);
            }
        } catch (error) {
            setErro(true);
            setMensagem("erro ao conectar com o servidor.");
        }
    };

    return (
        <div className="container mt-5" style={{ maxWidth: "400px" }}>
            <h2>Entrar no Sistema</h2>

            <form onSubmit={handleSubmitLogin} className="mt-4">
                <div className="mb-3">
                    <label className="form-label">Nome de usuário</label>
                    <input
                        type="text"
                        className="form-control"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        required
                    />
                </div>
                <div className="mb-3">
                    <label className="form-label">Senha</label>
                    <input
                        type="password"
                        className="form-control"
                        value={senha}
                        onChange={(e) => setSenha(e.target.value)}
                        required
                    />
                </div>
                <button type="submit" className="btn btn-primary w-100">Entrar</button>
            </form>

            <div className="mt-3 text-center">
                <p>Não possui conta? <Link to="/cadastro">Cadastre-se aqui</Link></p>
            </div>

            {mensagem && (
                <p className="mt-3 text-center" style={{ color: erro ? "red" : "green", fontWeight: "bold" }}>
                    {mensagem}
                </p>
            )}
        </div>
    );
}

export default Login;