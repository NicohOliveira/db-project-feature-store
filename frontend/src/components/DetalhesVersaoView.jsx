import React, { useEffect, useState } from "react";
import { useParams, useNavigate, useResolvedPath } from "react-router-dom";
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography'; // <-- Adicionado para o novo Tooltip
import { LineChart, lineClasses } from '@mui/x-charts/LineChart';
import { labelMarkClasses } from '@mui/x-charts/ChartsLabel';

import EvolucaoAcessosChart from "./EvolucaoAcessosChart";

function DetalhesVersaoView({ id, numVersao }) {
    const [registros, setRegistros] = useState([]);
    const [carregando, setCarregando] = useState(true);
    const [erro, setErro] = useState(null);
    
    const navigate = useNavigate();

    useEffect(() => {
        fetch(`http://localhost:8080/backend/registry/versao?id_dataset=${id}&num_versao=${numVersao}`, {
            method: "GET",
            credentials: "include"
        })
            .then(async (res) => {
                console.log("status:", res.status);

                const text = await res.text();
                console.log(text);

                return JSON.parse(text);
            })
            .then(async (data) => {
                if (data && data.length > 0) {
                    setRegistros(data);
                }
                setCarregando(false);
            })
            .catch((err) => {
                console.error(err);
                setErro("Não foi possível carregar o histórico.");
                setCarregando(false);
            });
    }, [id, numVersao]);

    if (carregando) return <div className="container mt-5 text-light"><div className="spinner-border text-light" role="status"></div> Carregando histórico...</div>;
    if (erro) return <div className="container mt-5 alert alert-danger bg-dark text-danger border-danger">{erro}</div>;

    return (
        <div className="text-light" style={{ width: "100%" }}>
            <Box sx={{
                border: "1px solid #444",
                borderRadius: 3,
                p: 3,
                mt: 3,
                backgroundColor: "#18181b33",
            }}
            >
                <h5 style={{ color: "white", marginBottom: "1rem" }}>
                    Histórico de visualizações e downloads desta versão.
                </h5>
                <EvolucaoAcessosChart dadosBanco={registros} />
            </Box>
        </div>
    );
}

export default DetalhesVersaoView;