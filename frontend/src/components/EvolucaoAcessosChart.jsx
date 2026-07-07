import * as React from 'react';
import { LineChart } from '@mui/x-charts/LineChart';

export default function EvolucaoAcessosChart({ dadosBanco }) {
  // 1. Consolida os dados para somar visualizações e downloads do mesmo dia
  const dadosAgrupados = dadosBanco.reduce((acc, curr) => {
    // Procura se o dia já existe no nosso acumulador
    const diaExistente = acc.find(item => item.data === curr.data);
    
    if (diaExistente) {
      diaExistente.visualizacoes += curr.visualizacoes;
      diaExistente.downloads += curr.downloads;
    } else {
      acc.push({
        data: curr.data,
        visualizacoes: curr.visualizacoes,
        downloads: curr.downloads
      });
    }
    return acc;
  }, []);

  // 2. Extrai os arrays individuais que o MUI X precisa
  const xLabels = dadosAgrupados.map(item => item.data);
  const dataVisualizacoes = dadosAgrupados.map(item => item.visualizacoes);
  const dataDownloads = dadosAgrupados.map(item => item.downloads);

  return (
    <LineChart
      height={300}
      xAxis={[
        { 
          scaleType: 'point', 
          data: xLabels 
        }
      ]}
      series={[
        {
          data: dataVisualizacoes,
          label: 'Visualizações',
          color: '#0288d1', // Azul MUI
        },
        {
          data: dataDownloads,
          label: 'Downloads',
          color: '#2e7d32', // Verde MUI
        },
      ]}
    />
  );
}