import { useNavigate } from "react-router-dom";

function Dashboard() {
    const navigate = useNavigate();

    const handleLogout = () => {
       //logout vai ser aqui dps
        navigate("/");
    };

    return (
        <div>
            <nav className="navbar navbar-expand-lg navbar-dark bg-dark">
                <div className="container">
                    <span className="navbar-brand mb-0 h1">Feature Store BD</span>
                    <div className="d-flex">
                        <button className="btn btn-outline-light btn-sm" onClick={handleLogout}>
                            Sair
                        </button>
                    </div>
                </div>
            </nav>

            <div className="container mt-5">
                <div className="d-flex justify-content-between align-items-center mb-4">
                    <h2>Meus Datasets</h2>
                    <button className="btn btn-success">+ Novo Dataset</button>
                </div>

                {/* aqui vai ser a lista dos dataset */}
                <div className="card shadow-sm">
                    <div className="card-body text-center text-muted py-5">
                        <em>Nenhum dataset cadastrado ainda. <br/>( aguardando api hehe :D )</em>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default Dashboard;