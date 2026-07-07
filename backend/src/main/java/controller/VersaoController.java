package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dao.DAOFactory;
import dao.UserDAO;
import dao.VersaoDAO;
import dao.RegistroAcessoDAO;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import model.Feature;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import model.Versao;
import model.User;

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

        HttpSession session = request.getSession();

        switch (request.getServletPath()) {

            case "/versao/delete": {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");

                try (DAOFactory daoFactory = DAOFactory.getInstance()) {
                    dao = daoFactory.getVersaoDAO();

                    User usuarioLogado = (User) session.getAttribute("usuario");

                    if (usuarioLogado == null) {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.getWriter().write("{\"status\": \"erro\", \"mensagem\": \"Usuário não autenticado.\"}");
                        return;
                    }

                    String idVersao = request.getParameter("id");
                    String senha    = request.getParameter("senha");

                    // valida a senha do usuario logado
                    UserDAO userDao = daoFactory.getUserDAO();
                    User credenciais = new User(usuarioLogado.getUsername(), senha);
                    userDao.authenticate(credenciais);

                    // deleta com a trava de segurança (só apaga se for o autor)
                    dao.deleteV(idVersao, usuarioLogado.getUsername());

                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().write("{\"status\": \"sucesso\"}");

                } catch (SecurityException e) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("{\"status\": \"erro\", \"mensagem\": \"Senha incorreta.\"}");
                } catch (Exception e) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().write("{\"status\": \"erro\", \"mensagem\": \"" + e.getMessage() + "\"}");
                }
                break;
            }

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
                        try {
                            RegistroAcessoDAO acessoDAO = daoFactory.getRegistroAcessoDAO();
                            User usuarioLogado = (User) session.getAttribute("usuario");
                            String usernameLogado = (usuarioLogado != null) ? usuarioLogado.getUsername() : null;

                            int idData = Integer.parseInt(idDatasetStr);
                            int numVer = Integer.parseInt(numVersaoStr);

                            acessoDAO.registrar(idData, numVer, usernameLogado, "VISUALIZACAO");
                        } catch (Exception ex) {
                            Logger.getLogger(VersaoController.class.getName()).log(Level.WARNING, "Falha silenciosa ao registrar visualização", ex);
                        }
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

                        String uploadDir = request.getServletContext().getRealPath("") + File.separator + "arquivos_csv";
                        File downloadFile = new File(uploadDir + File.separator + versao.getArquivoCsv());
                        if (!downloadFile.exists()) {
                            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Arquivo físico não encontrado no servidor.");
                            return;
                        }

                        response.setContentType("text/csv");
                        response.setContentLength((int) downloadFile.length());
                        response.setHeader("Content-Disposition",
                                String.format("attachment; filename=\"%s\"", downloadFile.getName()));

                        try (FileInputStream inStream = new FileInputStream(downloadFile);
                             OutputStream outStream = response.getOutputStream()) {

                            byte[] buffer = new byte[4096];
                            int bytesRead;
                            while ((bytesRead = inStream.read(buffer)) != -1) {
                                outStream.write(buffer, 0, bytesRead);
                            }
                        }
                        try {
                            RegistroAcessoDAO acessoDAO = daoFactory.getRegistroAcessoDAO();
                            User usuarioLogado = (User) session.getAttribute("usuario");
                            String usernameLogado = (usuarioLogado != null) ? usuarioLogado.getUsername() : null;

                            int idData = Integer.parseInt(idDatasetStr);
                            int numVer = Integer.parseInt(numVersaoStr);

                            acessoDAO.registrar(idData, numVer, usernameLogado, "DOWNLOAD");
                        } catch (Exception ex) {
                            Logger.getLogger(VersaoController.class.getName()).log(Level.WARNING, "Falha silenciosa ao registrar download", ex);
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
                    String featuresJson = request.getParameter("features");
                    System.out.println(">>> JSON RECEBIDO NO JAVA: " + featuresJson);
                    List<Feature> listaFeatures = new ArrayList<>();
                    if (featuresJson != null && !featuresJson.trim().isEmpty()) {
                        Type listType = new TypeToken<List<Feature>>(){}.getType();
                        listaFeatures = new Gson().fromJson(featuresJson, listType);
                        System.out.println(">>> [CONTROLLER] GSON CONVERTEU. QTD NA LISTA: " + listaFeatures.size());
                    }
                    Part arquivoPart = request.getPart("arquivo");
                    System.out.println(">>> DEBUG: Tamanho do arquivo recebido: " + (arquivoPart != null));
                    if (arquivoPart == null || arquivoPart.getSize() == 0) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        response.getWriter().write("{\"status\": \"erro\", \"mensagem\": \"Arquivo CSV é obrigatório.\"}");
                        return;
                    }

                    String uploadDir = request.getServletContext().getRealPath("") + File.separator + "arquivos_csv";
                    new File(uploadDir).mkdirs();

                    String fileName = idDataset + "_v" + numVersaoBase + "_" + System.currentTimeMillis() + ".csv";
                    String caminhoArquivo = uploadDir + File.separator + fileName;
                    System.out.println(">>> DEBUG: Caminho completo onde o arquivo será salvo: " + caminhoArquivo);
                    System.out.println(">>> DEBUG: Número de caracteres no caminho: " + caminhoArquivo.length());
                    arquivoPart.write(caminhoArquivo);
                    System.out.println(">>> DEBUG: Arquivo escrito com sucesso");

                    versao = new Versao();

                    versao.setIdDataset(idDataset);
                    versao.setNumVersaoBase(numVersaoBase);
                    versao.setIdDatasetBase(idDataset);
                    versao.setUsernameAutor(usernameAutor);
                    versao.setDescricaoModificacoes(descricao);
                    versao.setFeatures(listaFeatures);
                    versao.setArquivoCsv(fileName);
                    String maturidadeStr = request.getParameter("nivel_maturidade");
                    int maturidade = (maturidadeStr != null && !maturidadeStr.isEmpty()) ? Integer.parseInt(maturidadeStr) : 1;
                    versao.setNivelMaturidade(maturidade);
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
                        response.getWriter().write("{\"status\": \"ok\", \"mensagem\": \"Versão criada com sucesso!\", \"numVersao\": " + proximoNum + "}");

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