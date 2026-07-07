import * as React from "react";
import Box from "@mui/material/Box";
import { LineChart } from "@mui/x-charts/LineChart";

export default function EvolucaoAcessosChart({ dadosBanco }) {
  const dadosAgrupados = dadosBanco.reduce((acc, curr) => {
    const existente = acc.find((item) => item.data === curr.data);

    if (existente) {
      existente.visualizacoes += curr.visualizacoes;
      existente.downloads += curr.downloads;
    } else {
      acc.push({
        data: curr.data,
        visualizacoes: curr.visualizacoes,
        downloads: curr.downloads,
      });
    }

    return acc;
  }, []);

  dadosAgrupados.sort((a, b) => new Date(a.data) - new Date(b.data));

  const chartData = {
    xAxis: [
      {
        scaleType: "point",
        data: dadosAgrupados.map((item) => item.data),
      },
    ],
    series: [
      {
        data: dadosAgrupados.map((item) => item.visualizacoes),
        label: "Visualizações",
        color: "#0288d1",
        highlightScope: {
          highlight: "series",
          fade: "global",
        },
      },
      {
        data: dadosAgrupados.map((item) => item.downloads),
        label: "Downloads",
        color: "#2e7d32",
        area: true,
        highlightScope: {
          highlight: "series",
          fade: "global",
        },
      },
    ],
  };

  return (
    <Box sx={{ width: "100%", height: 350 }}>
      <LineChart
        {...chartData}
        experimentalFeatures={{
          enablePositionBasedPointerInteraction: true,
        }}
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