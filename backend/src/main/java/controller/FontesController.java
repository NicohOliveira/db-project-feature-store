/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import dao.DAO;
import dao.DAOFactory;
import dao.FonteDAO;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.VersaoFontes;

/**
 *
 * @author dskaster
 */
@WebServlet(name = "FontesController",
        urlPatterns = {
            "/source",
            "/source/create",
            "/source/update",
            "/source/delete",
            "/source/read"
        })
public class FontesController extends HttpServlet {
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        FonteDAO dao;
        VersaoFontes fonte;

        response.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
        response.setHeader("Access-Control-Allow-Credentials", "true");

        switch (request.getServletPath()) {
            case "/source": {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");

                try (DAOFactory daoFactory = DAOFactory.getInstance()) {
                    dao = daoFactory.getFonteDAO();

                    int datasetId  = Integer.parseInt(request.getParameter("id_dataset"));
                    int num_versao = Integer.parseInt(request.getParameter("numVersao"));

                    List<VersaoFontes> fontes = dao.allDatasetVersionSources(datasetId, num_versao);

                    Gson gson = new Gson();
                    String json = gson.toJson(fontes);

                    response.getWriter().write(json);

                } catch (Exception e) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().write("{\"status\": \"erro\", \"mensagem\": \"" + e.getMessage() + "\"}");
                }
                break;
            }

            case "/source/read": {
                // placeholder
                break;
            }
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        FonteDAO dao;
        VersaoFontes fonte;

        String servletPath = request.getServletPath();

        switch (servletPath) {
            case "/source/create": {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");

                response.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
                response.setHeader("Access-Control-Allow-Credentials", "true");

                try {
                    String datasetIdStr = request.getParameter("datasetId");
                    String versaoStr = request.getParameter("versao");
                    
                    String[] listaFontes = request.getParameterValues("fontes");

                    if (datasetIdStr == null || datasetIdStr.trim().isEmpty() || 
                        versaoStr == null || versaoStr.trim().isEmpty()) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        response.getWriter().write("{\"status\": \"erro\", \"mensagem\": \"datasetId e versao são obrigatórios.\"}");
                        return;
                    }

                    int datasetId;
                    int versao;

                    try {
                        datasetId = Integer.parseInt(datasetIdStr);
                        versao = Integer.parseInt(versaoStr);
                    } catch (NumberFormatException nfe) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        response.getWriter().write("{\"status\": \"erro\", \"mensagem\": \"datasetId ou versao precisam ser números válidos.\"}");
                        return;
                    }

                    if (listaFontes == null) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        response.getWriter().write("{\"status\": \"erro\", \"mensagem\": \"A fonte de origem é obrigatória.\"}");
                        return;
                    }
                    
                    try (DAOFactory daoFactory = DAOFactory.getInstance()){
                        dao = daoFactory.getFonteDAO();

                        for(String fonteObj : listaFontes){
                            fonte = new VersaoFontes(datasetId, versao, fonteObj);
                            dao.create(fonte);
                        }

                        response.setStatus(HttpServletResponse.SC_OK);
                        response.getWriter().write("{\"status\": \"ok\", \"mensagem\": \"Fonte(s) associada(s) com sucesso!\"}");
                    
                    }
                } catch (Exception e) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().write("{\"status\": \"erro\", \"mensagem\": \"" + e.getMessage() + "\"}");
                }
                break;
            }
            case "/source/update": {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");

                try {
                    int datasetId = Integer.parseInt(request.getParameter("datasetId"));
                    int versao = Integer.parseInt(request.getParameter("versao"));
                    String fonteStr = request.getParameter("fonte");

                    if (fonteStr == null || fonteStr.trim().isEmpty()) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        response.getWriter().write("{\"status\": \"erro\", \"mensagem\": \"A fonte de origem é obrigatória.\"}");
                        return;
                    }

                    fonte = new VersaoFontes(datasetId, versao, fonteStr);

                    try (DAOFactory daoFactory = DAOFactory.getInstance()){
                        dao = daoFactory.getFonteDAO();
                        dao.update(fonte);

                        response.setStatus(HttpServletResponse.SC_OK);
                        response.getWriter().write("{\"status\": \"ok\", \"mensagem\": \"Fonte atualizada com sucesso!\"}");
                    
                    }
                } catch (Exception e) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().write("{\"status\": \"erro\", \"mensagem\": \"" + e.getMessage() + "\"}");
                }
                break;
            }

            case "/source/delete": {
                // placeholder
                break;
            }

        }

    }

    @Override
    public String getServletInfo() {
        return "Controller responsável pelas operações relacionadas as fontes de versões.";
    }

}