import { useState } from "react";

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
            const response = await fetch("http://localhost:8080/bd2026/user/create", {
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
        <div>
        <h2>Cadastrar Usuário</h2>

        <form onSubmit={handleSubmitCadastro}>
            <div>
            <label>Nome de usuário</label>
            <br/>
            <input
                type="text"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                maxLength={20}
                required
            />
            </div>
            <div>
            <label>Senha</label>
            <br/>
            <input
                type="password"
                value={senha}
                onChange={(e) => setSenha(e.target.value)}
                required
            />
            </div>
            <button type="submit">Cadastrar</button>
        </form>

        {mensagem && (<p>{mensagem}</p>)}
        </div>
    );
}

export default CadastroUsuario;