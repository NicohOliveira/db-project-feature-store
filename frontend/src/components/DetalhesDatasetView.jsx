import React, { useEffect, useState } from "react";
import { useParams, useNavigate, useResolvedPath } from "react-router-dom";
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import { LineChart, lineClasses } from '@mui/x-charts/LineChart';
import { labelMarkClasses } from '@mui/x-charts/ChartsLabel';
import MaturidadeChart from "./MaturidadeChart";

import EvolucaoAcessosChart from "./EvolucaoAcessosChart";
import VersoesAcessosChart from "./VersoesAcessosChart";

function DetalhesDatasetView({ id, nome, estatisticas }) {
    const [registros, setRegistros] = useState([]);
    const [views, setViews] = useState([]);
    const [downloads, setDownloads] = useState([]);
    const [maturidade, setMaturidade] = useState([]);

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
        <div className="card bg-dark border-secondary mb-5 shadow-sm">
            <div className="card-body">
                <h5 className="text-light mb-3">Ranking de visualizações e downloads</h5>
                    <Box
                    sx={{
                        display: "flex",
                        justifyContent: "space-evenly",
                        gap: 3,
                    }}
                    >

                    <Box sx={{
                        width: "40%",
                        mt: 3,
                        mb: -3,
                    }}
                    >
                        <VersoesAcessosChart dadosBanco={views} tipoData="versao" tipo="views" />
                    </Box>

                    <Box sx={{
                        width: "40%",
                        p: 0,
                        mt: 3,
                        mb: -3,
                    }}
                    >
                        <VersoesAcessosChart dadosBanco={downloads} tipoData="versao" tipo="downloads" />
                    </Box>
                </Box>
            </div>

            <Box sx={{
                border: "1px solid #444",
                borderRadius: 0,
                p: 5,
                backgroundColor: "#1f1f1f00",
            }}
            >
                <h5 style={{ color: "white", marginBottom: "1rem" }}>
                    Histórico de visualizações e downloads de todas as versões
                </h5>
                <EvolucaoAcessosChart dadosBanco={registros} />
            </Box>

            <div className="card bg-dark border-secondary shadow-sm">
                <div className="border-secondary bg-transparent pt-3 pb-2">
                    <h5 className="text-light mb-0">Níveis de Maturidade</h5>
                    <small className="text-secondary">Distribuição da qualidade das versões deste dataset.</small>
                </div>
                <div className="card-body d-flex justify-content-center">
                    <Box sx={{ width: "50%", minWidth: "350px" }}>
                        <MaturidadeChart dadosBanco={estatisticas?.distribuicaoMaturidade || []} />
                    </Box>
                </div>
            </div>
        </div>
    );
}

export default DetalhesDatasetView;