import React from "react";

import VersoesAcessosChart from "./VersoesAcessosChart";
import Box from '@mui/material/Box';
import MaturidadeChart from "./MaturidadeChart";

import TotalCard from "./TotalCard";

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
                <TotalCard dadosBanco={estatisticas.totalDatasets} texto="Total de datasets" cor="text-light" />
                <TotalCard dadosBanco={estatisticas.totalVersoes} texto="Total de versões" cor="text-light" />
                <TotalCard dadosBanco={estatisticas.totalVisualizacoes} texto="Visualizações" cor="text-info" />
                <TotalCard dadosBanco={estatisticas.totalDownloads} texto="Downloads" cor="text-success" />
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
                                    <p className="text-dark">{porcVis}% Views</p>
                                </div>
                                <div className="progress-bar bg-success fw-bold fs-6" role="progressbar" style={{ width: `${porcDown}%` }} title="Downloads">
                                    <p className="text-light">{porcDown}% Downloads</p>
                                </div>
                            </div>
                            <small className="text-secondary d-block mt-2">
                                De todas as interações do sistema, <strong>{porcDown}%</strong> se transformam em downloads efetivos.
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
                                <VersoesAcessosChart dadosBanco={viewsDatasets} tipoData="Dataset" tipo="views" />
                            </Box>


                            <Box sx={{
                                width: "45%",
                                mt: 3,
                            }}
                            >
                                <VersoesAcessosChart dadosBanco={downloadsDatasets} tipoData="Dataset" tipo="downloads" />
                            </Box>
                        </Box>
                    )}
                </div>
            </div>
            <div className="row mb-4">
                <div className="col-md-6 d-flex">
                    <div className="card bg-dark border-secondary shadow-sm w-100">
                        <div className="card-header border-secondary bg-transparent pt-3 pb-2">
                            <h5 className="text-light mb-0">Qualidade do Acervo Global</h5>
                            <small className="text-secondary">Proporção de níveis de maturidade em toda a plataforma.</small>
                        </div>
                        <div className="card-body d-flex justify-content-center align-items-center">
                            <Box sx={{ width: "100%", minWidth: "300px" }}>
                                <MaturidadeChart dadosBanco={estatisticas.distribuicaoMaturidadeGlobal || []} />
                            </Box>
                        </div>
                    </div>
                </div>

                <div className="col-md-6 d-flex">
                    <div className="card bg-dark border-secondary shadow-sm w-100 d-flex flex-column">
                        <div className="card-header border-secondary bg-transparent pt-3 pb-2">
                            <h5 className="text-light mb-0">🏆 Datasets de Excelência (Ouro)</h5>
                            <small className="text-secondary">Datasets com o maior número de versões refinadas.</small>
                        </div>
                        <div className="card-body p-0 flex-grow-1">
                            {estatisticas.topDatasetsOuro && estatisticas.topDatasetsOuro.length > 0 ? (
                                <table className="table table-dark table-hover mb-0 h-100">
                                    <thead>
                                    <tr>
                                        <th className="px-4 py-3" style={{ width: "15%" }}>#</th>
                                        <th className="px-4 py-3">Dataset</th>
                                        <th className="px-4 py-3 text-center" style={{ width: "30%" }}>Versões Ouro</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    {estatisticas.topDatasetsOuro.map((item, index) => (
                                        <tr key={index}>
                                            <td className="px-4 py-3 text-secondary fw-bold">{index + 1}</td>
                                            <td className="px-4 py-3 fw-bold text-light">{item.dataset}</td>
                                            <td className="px-4 py-3 text-center text-warning fw-bold">{item.qtdOuro} ⭐</td>
                                        </tr>
                                    ))}
                                    </tbody>
                                </table>
                            ) : (
                                <div className="d-flex h-100 align-items-center justify-content-center">
                                    <p className="text-secondary p-4 mb-0 text-center">Nenhum dataset Ouro encontrado.</p>
                                </div>
                            )}
                        </div>
                    </div>
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