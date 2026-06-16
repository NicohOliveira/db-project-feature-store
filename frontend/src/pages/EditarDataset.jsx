import { useState, useEffect } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";

function EditarDataset() {
    const { id } = useParams();
    const [nome, setDatasetNome] = useState("");
    const [mensagem, setMensagem] = useState("");
    const [erro, setErro] = useState(false);

    useEffect(() => {
        fetch(`http://localhost:8080/backend/dataset/read?id=${id}`)
            .then(res => res.json())
            .then(data => setDatasetNome(data.nome));
    }, [id]);

    const handleEditDataset = async (e) => {
        e.preventDefault();
        setMensagem("");

        const params = new URLSearchParams();
        params.append("id", id);
        params.append("nome", nome);

        try{
            const response = await fetch("http://localhost:8080/backend/dataset/update", {
                method: "POST",
                body: params,
            });

            const data = await response.json();

            if(data.status === "ok"){
                setErro(false);
                setMensagem(data.mensagem);
                setDatasetNome("");
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
                                <div className="d-flex justify-content-between align-items-center mb-4">
                                    <Link to="/dashboard" className="btn btn-outline-secondary btn-sm">
                                        Voltar
                                    </Link>
                                </div>

                                <div className="text-center mb-4">
                                    <h4 className="fw-bold mb-0" style={{ color: "#e0e0e0" }}>Editar Dataset</h4>
                                    <p className="small mt-1" style={{ color: "#888" }}>Atualize o nome do repositório</p>
                                </div>

                                {mensagem && (
                                    <div className={`alert ${erro ? "alert-danger" : "alert-success"} py-2`} role="alert">
                                        {mensagem}
                                    </div>
                                )}

                                <form onSubmit={handleEditDataset}>
                                    <div className="mb-3">
                                        <label className="form-label fw-semibold" style={{ color: "#e0e0e0" }}>Nome do repositório</label>
                                        <input
                                            type="text"
                                            className="form-control form-control-sm"
                                            placeholder="Repositório legal 123"
                                            value={nome}
                                            onChange={(e) => setDatasetNome(e.target.value)}
                                            required
                                            style={{ background: "#2e2e2e", border: "1px solid #444", color: "#e0e0e0" }}
                                        />
                                    </div>
                                    <button type="submit" className="btn btn-success btn-lg w-100 fw-bold">
                                        Atualizar
                                    </button>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default EditarDataset;