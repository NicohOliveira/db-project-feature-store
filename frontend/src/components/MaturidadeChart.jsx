import * as React from "react";
import Box from "@mui/material/Box";
import { PieChart } from "@mui/x-charts/PieChart";

export default function MaturidadeChart({ dadosBanco }) {
    if (!dadosBanco || dadosBanco.length === 0) {
        return <p className="text-secondary text-center mt-4">Sem dados de maturidade.</p>;
    }

    const mapearMaturidade = (nivel) => {
        switch (nivel) {
            case 1: return { label: "Bronze", color: "#CD7F32" };
            case 2: return { label: "Prata", color: "#C0C0C0" };
            case 3: return { label: "Ouro", color: "#FFD700" };
            default: return { label: "Indefinido", color: "#6c757d" };
        }
    };

    const chartData = dadosBanco.map((item, index) => {
        const config = mapearMaturidade(item.nivel);
        return {
            id: index,
            value: item.quantidade,
            label: config.label,
            color: config.color
        };
    });

    return (
        <Box sx={{ width: "100%", height: 300, display: "flex", justifyContent: "center" }}>
            <PieChart
                series={[
                    {
                        data: chartData,
                        innerRadius: 40,
                        outerRadius: 100,
                        paddingAngle: 5,
                        cornerRadius: 5,
                        highlightScope: { faded: 'global', highlighted: 'item' },
                        faded: { innerRadius: 30, additionalRadius: -30, color: 'gray' },
                    },
                ]}
                height={300}
                sx={{
                    "& .MuiChartsLegend-root text": {
                        fill: "#ffffffc5 !important",
                    },
                }}
            />
        </Box>
    );
}