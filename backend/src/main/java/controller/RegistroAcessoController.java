package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dao.DAOFactory;
import dao.RegistroAcessoDAO;
import dao.UserDAO;
import dao.VersaoDAO;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import model.Feature;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
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

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import model.RegistroAcesso;
import model.DadosAcesso;

@WebServlet(name = "RegistroAcessoController",
        urlPatterns = {
            "/registry",
            "/registry/dataset",
            "/registry/dataset/top/views",
            "/registry/dataset/top/downloads",
            "/registry/dataset/stats", // Engloba os 3 acima (para usar um só fetch)
            "/registry/versao",
            "/registry/create",
            "/registry/read",
            "/registry/update",
            "/registry/delete"
        })
@MultipartConfig(maxFileSize = 1024 * 1024 * 4)
public class RegistroAcessoController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        long aYear = 31536000000L;
        RegistroAcessoDAO dao;

        response.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
        response.setHeader("Access-Control-Allow-Credentials", "true");

        switch (request.getServletPath()) {

            case "/registry/delete": {
                // placeholder
                break;
            }

            case "/registry/read": {
                // placeholder
                break;
            }

            case "/registry/dataset/stats": {
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

                    long now = System.currentTimeMillis();

                    Date startDate = new Date(now - aYear);
                    Date endDate = new Date(now);

                    try (DAOFactory daoFactory = DAOFactory.getInstance()) {
                        dao = daoFactory.getRegistroAcessoDAO();

                        List<DadosAcesso> historico = dao.allDatasetAcessesBetween(startDate, endDate, idDataset);
                        List<DadosAcesso> topViews = dao.topVersionViews(startDate, endDate, idDataset);
                        List<DadosAcesso> topDownloads = dao.topVersionDownloads(startDate, endDate, idDataset);

                        Map<String, Object> resposta = Map.of(
                            "historico", historico,

                            "topViews", topViews.stream().map(d -> Map.of(
                                "versao", d.getVersao(),
                                "visualizacoes", d.getVisualizacoes()
                            )).toList(),

                            "topDownloads", topDownloads.stream().map(d -> Map.of(
                                "versao", d.getVersao(),
                                "downloads", d.getDownloads()
                            )).toList()
                        );

                        Gson gson = new Gson();
                        response.getWriter().write(gson.toJson(resposta));
                    }

                } catch (Exception e) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().write(
                        "{\"status\":\"erro\", \"mensagem\":\"" + e.getMessage() + "\"}"
                    );
                }

                break;
            }

            case "/registry/dataset": {
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

                    long now = System.currentTimeMillis();

                    Date startDate = new Date(now - aYear);
                    Date endDate = new Date(now);

                    try (DAOFactory daoFactory = DAOFactory.getInstance()) {
                        dao = daoFactory.getRegistroAcessoDAO();
                        List<DadosAcesso> historico = dao.allDatasetAcessesBetween(startDate, endDate, idDataset);

                        Gson gson = new Gson();
                        String json = gson.toJson(historico);

                        response.getWriter().write(json);
                    }
                } catch (Exception e) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().write("{\"status\": \"erro\", \"mensagem\": \"" + e.getMessage() + "\"}");
                }
                break;
            }

            case "/registry/versao": {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");

                try {
                    String idDatasetStr = request.getParameter("id_dataset");
                    String numVersaoStr = request.getParameter("num_versao");

                    if (idDatasetStr == null || idDatasetStr.isEmpty() || numVersaoStr == null || numVersaoStr.isEmpty()) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        response.getWriter().write("{\"erro\": \"ID do dataset e versão são obrigatórios.\"}");
                        return;
                    }

                    int idDataset = Integer.parseInt(idDatasetStr);
                    int numVersao = Integer.parseInt(numVersaoStr);

                    long now = System.currentTimeMillis();

                    Date startDate = new Date(now - aYear);
                    Date endDate = new Date(now);

                    try (DAOFactory daoFactory = DAOFactory.getInstance()) {
                        dao = daoFactory.getRegistroAcessoDAO();
                        List<DadosAcesso> historico = dao.allVersionAcessesBetween(startDate, endDate, idDataset, numVersao);

                        Gson gson = new Gson();
                        String json = gson.toJson(historico);

                        response.getWriter().write(json);
                    }
                } catch (Exception e) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().write("{\"status\": \"erro\", \"mensagem\": \"" + e.getMessage() + "\"}");
                }
                break;
            }

            case "/registry/dataset/top/views": {
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

                    long now = System.currentTimeMillis();

                    Date startDate = new Date(now - aYear);
                    Date endDate = new Date(now);

                    try (DAOFactory daoFactory = DAOFactory.getInstance()) {
                        dao = daoFactory.getRegistroAcessoDAO();
                        List<DadosAcesso> historico = dao.topVersionViews(startDate, endDate, idDataset);

                        List<Map<String, Integer>> ranking = historico.stream().map(d -> Map.of(
                            "versao", d.getVersao(),
                            "visualizacoes", d.getDownloads()
                        )).toList();

                        Gson gson = new Gson();
                        String json = gson.toJson(ranking);

                        response.getWriter().write(json);
                    }
                } catch (Exception e) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().write("{\"status\": \"erro\", \"mensagem\": \"" + e.getMessage() + "\"}");
                }
                break;
            }
            
            case "/registry/dataset/top/downloads": {
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

                    long now = System.currentTimeMillis();

                    Date startDate = new Date(now - aYear);
                    Date endDate = new Date(now);

                    try (DAOFactory daoFactory = DAOFactory.getInstance()) {
                        dao = daoFactory.getRegistroAcessoDAO();
                        List<DadosAcesso> historico = dao.topVersionDownloads(startDate, endDate, idDataset);

                        List<Map<String, Integer>> ranking = historico.stream().map(d -> Map.of(
                            "versao", d.getVersao(),
                            "downloads", d.getDownloads()
                        )).toList();

                        Gson gson = new Gson();
                        String json = gson.toJson(ranking);

                        response.getWriter().write(json);
                    }
                } catch (Exception e) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().write("{\"status\": \"erro\", \"mensagem\": \"" + e.getMessage() + "\"}");
                }
                break;
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        RegistroAcessoDAO dao;
        RegistroAcesso rg;
        HttpSession session = request.getSession();

        String servletPath = request.getServletPath();

        switch (servletPath) {
            case "/registry/create": {
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

                    int idDataset = Integer.parseInt(request.getParameter("id_dataset"));
                    int numVersao = Integer.parseInt(request.getParameter("num_versao"));
                    String tipoAcao = request.getParameter("tipo_acao");
                    String usernameAutor = request.getParameter("username_leitor");

                    Date date = new Date(System.currentTimeMillis());
                    Time time = Time.valueOf(LocalTime.now());

                    rg = new RegistroAcesso(date, time, tipoAcao, usernameAutor, idDataset, numVersao);

                    try (DAOFactory daoFactory = DAOFactory.getInstance()) {
                        dao = daoFactory.getRegistroAcessoDAO();

                        dao.create(rg);

                        response.setStatus(HttpServletResponse.SC_OK);
                        response.getWriter().write("{\"status\": \"ok\", \"mensagem\": \"Registro criado com sucesso!\"}");

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