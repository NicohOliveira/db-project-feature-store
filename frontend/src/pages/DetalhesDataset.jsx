import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";

function DetalhesDataset() {
    const { id } = useParams();  // pega o id da URL :o
    const [dataset, setDataset] = useState(null);

    useEffect(() => {
        fetch(`http://localhost:8080/backend/dataset/read?id=${id}`)
            .then(res => res.json())
            .then(data => setDataset(data));
    }, [id]);

    if (!dataset) return <p>Carregando...</p>;

    return (
        <div className="container mt-5">
            <h2>{dataset.nome}</h2>
            <p>Criador: {dataset.username_criador}</p>
        </div>
    );
}

export default DetalhesDataset;