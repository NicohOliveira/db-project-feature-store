import * as React from "react";
import Box from "@mui/material/Box";
import { BarChart } from "@mui/x-charts/BarChart";

export default function VersoesAcessosChart({ dadosBanco, tipo }) {

  const isViews = tipo === "views";

  const chartData = {
    xAxis: [
      {
        scaleType: "band",
        data: dadosBanco.map((item) => item.versao),
        label: "Versão",
      },
    ],
    series: [
      {
        data: dadosBanco.map((item) =>
          isViews ? item.visualizacoes : item.downloads
        ),
        label: isViews ? "Visualizações" : "Downloads",
        color: isViews ? "#0288d1" : "#2e7d32",
      },
    ],
  };

  return (
    <Box sx={{ width: "100%", height: 350 }}>
      <BarChart
        {...chartData}
        height={300}
        sx={{
          "& .MuiChartsAxis-root line": {
            stroke: "#ffffffc5",
          },
          "& .MuiChartsAxis-root text": {
            fill: "#ffffffc5",
          },
          "& .MuiChartsLegend-root text": {
            fill: "#ffffffc5",
          },
          "& .MuiChartsGrid-line": {
            stroke: "rgba(190, 190, 190, 0.15)",
          },
          "& .MuiChartsLabel-root": {
            color: "#ffffffc5 !important",
            fill: "#ffffffc5 !important",
          },
          "& .MuiChartsLegend-root": {
            color: "#ffffffc5 !important",
          },
        }}
      />
    </Box>
  );
}