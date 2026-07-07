import React, { useEffect, useState } from "react";
import { useParams, useNavigate, useResolvedPath } from "react-router-dom";
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography'; // <-- Adicionado para o novo Tooltip
import { LineChart, lineClasses } from '@mui/x-charts/LineChart';
import { labelMarkClasses } from '@mui/x-charts/ChartsLabel';

import EvolucaoAcessosChart from "./EvolucaoAcessosChart";
import VersoesAcessosChart from "./VersoesAcessosChart";

function DetalhesDatasetView({ id, nome }) {
    const [registros, setRegistros] = useState([]);
    const [views, setViews] = useState([]);
    const [downloads, setDownloads] = useState([]);

    const [carregando, setCarregando] = useState(true);
    const [erro, setErro] = useState(null);
    
    const navigate = useNavigate();

    useEffect(() => {
        fetch(`http://localhost:8080/backend/registry/dataset/stats?id_dataset=${id}`, {
            method: "GET",
            credentials: "include"
        })
            .then((res) => {
                if (!res.ok) throw new Error("Erro ao buscar histórico.");
                return res.json();
            })
            .then(async (data) => {
                if (data) {
                    setRegistros(data.historico);
                    setViews([...data.topViews].reverse());
                    setDownloads([...data.topDownloads].reverse());
                }
                setCarregando(false);
            })
            .catch((err) => {
                console.error(err);
                setErro("Não foi possível carregar o histórico.");
                setCarregando(false);
            });
    }, [id]);

    if (carregando) return <div className="container mt-5 text-light"><div className="spinner-border text-light" role="status"></div> Carregando histórico...</div>;
    if (erro) return <div className="container mt-5 alert alert-danger bg-dark text-danger border-danger">{erro}</div>;

    return (
        <div className="text-light" style={{ width: "100%" }}>
            <h3>Estatísticas de {nome}</h3>
            <Box
                sx={{
                    display: "flex",
                    justifyContent: "space-between",
                    gap: 3,
                }}
                >
                <Box sx={{
                    width: "50%",
                    border: "1px solid #444",
                    borderRadius: 3,
                    p: 3,
                    mt: 3,
                    backgroundColor: "#1f1f1f",
                }}
                >
                    <h5 style={{ color: "white", marginBottom: "1rem" }}>
                        Top visualizações
                    </h5>
                    <VersoesAcessosChart dadosBanco={views} tipo="views" />
                </Box>

                <Box sx={{
                    width: "50%",
                    border: "1px solid #444",
                    borderRadius: 3,
                    p: 3,
                    mt: 3,
                    backgroundColor: "#1f1f1f",
                }}
                >
                    <h5 style={{ color: "white", marginBottom: "1rem" }}>
                        Top downloads
                    </h5>
                    <VersoesAcessosChart dadosBanco={downloads} tipo="downloads" />
                </Box>
            </Box>

            <Box sx={{
                border: "1px solid #444",
                borderRadius: 3,
                p: 3,
                mt: 3,
                backgroundColor: "#1f1f1f",
            }}
            >
                <h5 style={{ color: "white", marginBottom: "1rem" }}>
                    Histórico de visualizações e downloads de todas as versões deste Dataset
                </h5>
                <EvolucaoAcessosChart dadosBanco={registros} />
            </Box>
        </div>
    );
}

export default DetalhesDatasetView;