package controller;

import com.google.gson.Gson;
import dao.DAOFactory;
import dao.RelatorioDAO;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.User;

@WebServlet(name = "RelatorioController",
        urlPatterns = {
                "/estatisticas/geral",
                "/estatisticas/dataset"
        })
public class RelatorioController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession();

        /*if (session == null || session.getAttribute("usuario") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"status\": \"erro\", \"mensagem\": \"Usuário não autenticado.\"}");
            return;
        }*/

        RelatorioDAO dao;
        String servletPath = request.getServletPath();
        Gson gson = new Gson();

        switch (servletPath) {
            case "/estatisticas/geral": {
                try {
                    String pageCStr = request.getParameter("pageContrib");
                    String pageDStr = request.getParameter("pageDatasets");

                    int pageContrib = (pageCStr != null && !pageCStr.trim().isEmpty()) ? Integer.parseInt(pageCStr) : 1;
                    int pageDatasets = (pageDStr != null && !pageDStr.trim().isEmpty()) ? Integer.parseInt(pageDStr) : 1;

                    try (DAOFactory daoFactory = DAOFactory.getInstance()) {
                        dao = daoFactory.getRelatorioDAO();

                        Map<String, Object> estatisticas = dao.getEstatisticasGerais(pageContrib, pageDatasets);

                        response.setStatus(HttpServletResponse.SC_OK);
                        response.getWriter().write(gson.toJson(estatisticas));
                    }

                } catch (Exception e) {
                    Logger.getLogger(RelatorioController.class.getName()).log(Level.SEVERE, "Erro ao buscar estatisticas gerais", e);
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().write("{\"status\": \"erro\", \"mensagem\": \"" + e.getMessage() + "\"}");
                }
                break;
            }

            case "/estatisticas/dataset": {
                try {
                    String idDatasetStr = request.getParameter("id");
                    String pageCStr = request.getParameter("pageContrib");
                    String pageVStr = request.getParameter("pageVersoes");

                    if (idDatasetStr == null || idDatasetStr.trim().isEmpty()) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        response.getWriter().write("{\"status\": \"erro\", \"mensagem\": \"ID do dataset não fornecido.\"}");
                        return;
                    }

                    int idDataset = Integer.parseInt(idDatasetStr);
                    int pageContrib = (pageCStr != null) ? Integer.parseInt(pageCStr) : 1;
                    int pageVersoes = (pageVStr != null) ? Integer.parseInt(pageVStr) : 1;

                    try (DAOFactory daoFactory = DAOFactory.getInstance()) {
                        dao = daoFactory.getRelatorioDAO();

                        Map<String, Object> estatisticas = dao.getEstatisticasDataset(idDataset, pageContrib, pageVersoes);

                        response.setStatus(HttpServletResponse.SC_OK);
                        response.getWriter().write(gson.toJson(estatisticas));
                    }
                } catch (Exception e) {
                    Logger.getLogger(RelatorioController.class.getName()).log(Level.SEVERE, "Erro especificas", e);
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().write("{\"status\": \"erro\"}");
                }
                break;
            }
        }
    }

    @Override
    public String getServletInfo() {
        return "Controller responsável pela geração de relatórios e estatísticas JSON.";
    }
}