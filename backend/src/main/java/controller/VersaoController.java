package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dao.DAOFactory;
import dao.VersaoDAO;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import javax.servlet.annotation.MultipartConfig;

import model.Versao;

@WebServlet(name = "VersaoController",
        urlPatterns = {
            "/versao",
            "/versao/history",
            "/versao/download",
            "/versao/create",
            "/versao/read",
            "/versao/update",
            "/versao/delete"
        })

@MultipartConfig(maxFileSize = 1024 * 1024 * 4)

public class VersaoController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        VersaoDAO dao;

        response.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
        response.setHeader("Access-Control-Allow-Credentials", "true");

        switch (request.getServletPath()) {
            case "/versao/read": {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");

                try {
                    String idDatasetStr = request.getParameter("id_dataset");
                    String numVersaoStr = request.getParameter("num_versao");

                    if (idDatasetStr == null || numVersaoStr == null) {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Faltam parâmetros de ID ou Versão.");
                        return;
                    }

                    try (DAOFactory daoFactory = DAOFactory.getInstance()) {
                        dao = daoFactory.getVersaoDAO();

                        String idComposto = idDatasetStr + "-" + numVersaoStr;
                        Versao versao = dao.read(idComposto);

                        if (versao == null || versao.getArquivoCsv() == null) {
                            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Arquivo não encontrado no banco.");
                            return;
                        }

                        Gson gson = new Gson();
                        response.getWriter().write(gson.toJson(versao));
                    }
                } catch (Exception e) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().write("{\"status\": \"erro\", \"mensagem\": \"" + e.getMessage() + "\"}");
                }
                break;
            }

            case "/versao/history": {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");

                try {
                    String idDatasetStr = request.getParameter("id_dataset");

                    if (idDatasetStr == null || idDatasetStr.isEmpty()) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        response.getWriter().write("{\"erro\": \"ID do dataset é obrigatório.\"}");
                        return;
                    }

                    int idDataset = Integer.parseInt(idDatasetStr);

                    try (DAOFactory daoFactory = DAOFactory.getInstance()) {
                        dao = daoFactory.getVersaoDAO();
                        List<Versao> historico = dao.listByDataset(idDataset);

                        Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy").create();
                        response.getWriter().write(gson.toJson(historico));
                    
                    }
                } catch (Exception e) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().write("{\"status\": \"erro\", \"mensagem\": \"" + e.getMessage() + "\"}");
                }
                break;
            }

            case "/versao/download": {
                try {
                    String idDatasetStr = request.getParameter("id_dataset");
                    String numVersaoStr = request.getParameter("num_versao");

                    if (idDatasetStr == null || numVersaoStr == null) {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Faltam parâmetros de ID ou Versão.");
                        return;
                    }

                    try (DAOFactory daoFactory = DAOFactory.getInstance()) {
                        dao = daoFactory.getVersaoDAO();
                    
                        String idComposto = idDatasetStr + "-" + numVersaoStr;
                        Versao versao = dao.read(idComposto);

                        if (versao == null || versao.getArquivoCsv() == null) {
                            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Arquivo não encontrado no banco.");
                            return;
                        }

                        File downloadFile = new File(versao.getArquivoCsv());
                        if (!downloadFile.exists()) {
                            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Arquivo físico não encontrado no servidor. :( ");
                            return;
                        }

                        // em tese faz baixar o arquivo

                        response.setContentType("text/csv");
                        response.setContentLength((int) downloadFile.length());
                        String headerKey = "Content-Disposition";
                        String headerValue = String.format("attachment; filename=\"%s\"", downloadFile.getName());
                        response.setHeader(headerKey, headerValue);

                        // e entao escreve os bytes na resposta http

                        try (FileInputStream inStream = new FileInputStream(downloadFile);
                            OutputStream outStream = response.getOutputStream()) {

                            byte[] buffer = new byte[4096];
                            int bytesRead;
                            while ((bytesRead = inStream.read(buffer)) != -1) {
                                outStream.write(buffer, 0, bytesRead);
                            }
                        }
                    }
                } catch (Exception e) {
                    Logger.getLogger(VersaoController.class.getName()).log(Level.SEVERE, "Erro no Download", e);
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro ao processar o download.");
                }
                break;
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        VersaoDAO dao;
        Versao versao = new Versao();
        HttpSession session = request.getSession();

        String servletPath = request.getServletPath();

        switch (servletPath) {
            case "/versao/create": {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");

                response.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
                response.setHeader("Access-Control-Allow-Credentials", "true");

                try {
                    if (session == null || session.getAttribute("usuario") == null) {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.getWriter().write("{\"status\": \"erro\", \"mensagem\": \"Usuário não autenticado.\"}");
                        return;
                    }

                    int idDataset          = Integer.parseInt(request.getParameter("id_dataset"));
                    int numVersaoBase      = Integer.parseInt(request.getParameter("num_versao_base"));
                    String usernameAutor   = request.getParameter("username_autor");
                    String descricao       = request.getParameter("descricao_modificacoes");
                    String detalhesFeature = request.getParameter("detalhes_feature");

                    Part arquivoPart = request.getPart("arquivo");
                    if (arquivoPart == null || arquivoPart.getSize() == 0) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        response.getWriter().write("{\"status\": \"erro\", \"mensagem\": \"Arquivo CSV é obrigatório.\"}");
                        return;
                    }

                    String uploadDir = request.getServletContext().getRealPath("") + File.separator + "arquivos_csv";
                    new File(uploadDir).mkdirs();

                    String fileName = idDataset + "_v" + numVersaoBase + "_" + System.currentTimeMillis() + ".csv";
                    String caminhoArquivo = uploadDir + File.separator + fileName;
                    arquivoPart.write(caminhoArquivo);

                    versao = new Versao();

                    versao.setIdDataset(idDataset);
                    versao.setNumVersaoBase(numVersaoBase);
                    versao.setIdDatasetBase(idDataset);
                    versao.setUsernameAutor(usernameAutor);
                    versao.setDescricaoModificacoes(descricao);
                    versao.setDetalhesFeature(detalhesFeature);
                    versao.setArquivoCsv(caminhoArquivo);
                    versao.setNivelMaturidade(1);
                    versao.setDataRegistro(new java.sql.Date(System.currentTimeMillis()));
                    versao.setHoraRegistro(new java.sql.Time(System.currentTimeMillis()));

                    try (DAOFactory daoFactory = DAOFactory.getInstance()) {
                        dao = daoFactory.getVersaoDAO();

                        // meio que gambiarra por enquanto :/
                        List<Versao> versoes = dao.listByDataset(idDataset);
                        int proximoNum = versoes.stream().mapToInt(Versao::getNumVersao).max().orElse(0) + 1;
                        versao.setNumVersao(proximoNum);
                        
                        dao.create(versao);

                        response.setStatus(HttpServletResponse.SC_OK);
                        response.getWriter().write("{\"status\": \"ok\", \"mensagem\": \"Versão criada com sucesso!\"}");

                    }
                } catch (Exception e) {
                    Logger.getLogger(VersaoController.class.getName()).log(Level.SEVERE, "Create", e);
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().write("{\"status\": \"erro\", \"mensagem\": \"" + e.getMessage() + "\"}");
                }
                break;
            }
        }
    }

    @Override
    public String getServletInfo() {
        return "Controller responsável por Histórico e Download de Versões (Parte B)";
    }
}