import React from "react";

import VersoesAcessosChart from "./VersoesAcessosChart";
import Box from '@mui/material/Box';

function VisaoGlobal({ estatisticas, pageContrib, setPageContrib, datasets }) {
    if (!estatisticas) {
        return <p style={{ color: "#888" }}>Carregando estatísticas da plataforma...</p>;
    }

    const totalAcoes = estatisticas.totalVisualizacoes + estatisticas.totalDownloads;
    const porcVis = totalAcoes > 0 ? Math.round((estatisticas.totalVisualizacoes / totalAcoes) * 100) : 0;
    const porcDown = totalAcoes > 0 ? Math.round((estatisticas.totalDownloads / totalAcoes) * 100) : 0;

    const viewsDatasets = [...estatisticas.viewsDatasets].reverse();
    const downloadsDatasets = [...estatisticas.downloadsDatasets].reverse();

    return (
        <div>
            <div className="row mb-5">
                <div className="col-md-3">
                    <div className="card bg-dark border-secondary shadow-sm">
                        <div className="card-body text-center">
                            <h6 className="text-secondary text-uppercase fw-bold" style={{ fontSize: "12px" }}>Total Datasets</h6>
                            <h2 className="text-light mb-0">{estatisticas.totalDatasets || 0}</h2>
                        </div>
                    </div>
                </div>
                <div className="col-md-3">
                    <div className="card bg-dark border-secondary shadow-sm">
                        <div className="card-body text-center">
                            <h6 className="text-secondary text-uppercase fw-bold" style={{ fontSize: "12px" }}>Total Versões</h6>
                            <h2 className="text-light mb-0">{estatisticas.totalVersoes || 0}</h2>
                        </div>
                    </div>
                </div>
                <div className="col-md-3">
                    <div className="card bg-dark border-secondary shadow-sm">
                        <div className="card-body text-center">
                            <h6 className="text-secondary text-uppercase fw-bold" style={{ fontSize: "12px" }}>Visualizações</h6>
                            <h2 className="text-info mb-0">{estatisticas.totalVisualizacoes || 0}</h2>
                        </div>
                    </div>
                </div>
                <div className="col-md-3">
                    <div className="card bg-dark border-secondary shadow-sm">
                        <div className="card-body text-center">
                            <h6 className="text-secondary text-uppercase fw-bold" style={{ fontSize: "12px" }}>Downloads</h6>
                            <h2 className="text-success mb-0">{estatisticas.totalDownloads || 0}</h2>
                        </div>
                    </div>
                </div>
            </div>
            <div className="card bg-dark border-secondary mb-5 shadow-sm">
                <div className="card-body">
                    <h5 className="text-light mb-3">Engajamento Global da Plataforma</h5>
                    {totalAcoes === 0 ? (
                        <p className="text-secondary mb-0">Nenhuma interação registrada no sistema ainda.</p>
                    ) : (
                        <>
                            <div className="progress" style={{ height: "30px", backgroundColor: "#333" }}>
                                <div className="progress-bar bg-info fw-bold fs-6" role="progressbar" style={{ width: `${porcVis}%` }} title="Visualizações">
                                    {porcVis}% Views
                                </div>
                                <div className="progress-bar bg-success fw-bold fs-6" role="progressbar" style={{ width: `${porcDown}%` }} title="Downloads">
                                    {porcDown}% Downs
                                </div>
                            </div>
                            <small className="text-secondary d-block mt-2">
                                Conversão média da plataforma: de todas as interações do sistema, <strong>{porcDown}%</strong> se transformam em downloads efetivos.
                            </small>
                        </>
                    )}
                </div>
            </div>

            <div className="card bg-dark border-secondary mb-5 shadow-sm">
                <div className="card-body">
                    <h5 className="text-light mb-3">Datasets com maior número de interações</h5>
                    {totalAcoes === 0 ? (
                        <p className="text-secondary mb-0">Nenhum dataset registrado.</p>
                    ) : (
                        <Box
                sx={{
                    display: "flex",
                    justifyContent: "space-evenly",
                    gap: 3,
                }}
                >

                <Box sx={{
                    width: "45%",
                    mt: 3,
                }}
                >
                    <VersoesAcessosChart dadosBanco={viewsDatasets} tipoData="dataset" tipo="views" />
                </Box>

                <Box sx={{
                    width: "45%",
                    mt: 3,
                }}
                >
                    <VersoesAcessosChart dadosBanco={downloadsDatasets} tipoData="dataset" tipo="downloads" />
                </Box>
            </Box>
                    )}
                </div>
            </div>

            <div className="row">
                <div className="col-md-12 mb-4">
                    <div className="card bg-dark border-secondary shadow-sm d-flex flex-column">
                        <div className="card-header border-secondary bg-transparent pt-3 pb-2">
                            <h5 className="text-light mb-0">🏆 Top Contribuidores Globais</h5>
                            <small className="text-secondary">Usuários que mais enriquecem a plataforma com novas versões.</small>
                        </div>
                        <div className="card-body p-0">
                            {estatisticas.rankingContribuidoresGlobais && estatisticas.rankingContribuidoresGlobais.length > 0 ? (
                                <table className="table table-dark table-hover mb-0">
                                    <thead>
                                    <tr>
                                        <th className="px-4 py-3" style={{ width: "15%" }}>Posição</th>
                                        <th className="px-4 py-3">Usuário</th>
                                        <th className="px-4 py-3 text-center" style={{ width: "25%" }}>Versões Criadas no Sistema</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    {estatisticas.rankingContribuidoresGlobais.map((user, index) => (
                                        <tr key={index}>
                                            <td className="px-4 py-3 text-secondary fw-bold">#{((pageContrib - 1) * 5) + index + 1}</td>
                                            <td className="px-4 py-3 fw-bold text-light">@{user.usuario}</td>
                                            <td className="px-4 py-3 text-center text-info fw-bold">{user.criacoes}</td>
                                        </tr>
                                    ))}
                                    </tbody>
                                </table>
                            ) : (
                                <p className="text-secondary p-4 mb-0 text-center">Nenhum contribuidor encontrado.</p>
                            )}
                        </div>
                        <div className="card-footer border-secondary bg-transparent d-flex justify-content-between align-items-center py-3">
                            <button
                                className="btn btn-sm btn-outline-secondary fw-bold"
                                disabled={pageContrib === 1}
                                onClick={() => setPageContrib(pageContrib - 1)}
                            >
                                &laquo; Anterior
                            </button>
                            <small className="text-secondary">Página {pageContrib}</small>
                            <button
                                className="btn btn-sm btn-outline-secondary fw-bold"
                                disabled={!estatisticas.rankingContribuidoresGlobais || estatisticas.rankingContribuidoresGlobais.length < 5}
                                onClick={() => setPageContrib(pageContrib + 1)}
                            >
                                Próxima &raquo;
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default VisaoGlobal;