/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import dao.DAO;
import dao.DAOFactory;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.Dataset;
import model.User;

/**
 *
 * @author dskaster
 */
@WebServlet(name = "DatasetController",
        urlPatterns = {
            "/dataset",
            "/dataset/create",
            "/dataset/update",
            "/dataset/delete",
            "/dataset/read"
        })
public class DatasetController extends HttpServlet {
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
        DAO<Dataset> dao;
        Dataset dataset;
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setCharacterEncoding("UTF-8");

        switch (request.getServletPath()) {
            case "/dataset": {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");

                try (DAOFactory daoFactory = DAOFactory.getInstance()) {
                    dao = daoFactory.getDatasetDAO();

                    List<Dataset> datasets = dao.all();

                    Gson gson = new Gson();
                    String json = gson.toJson(datasets);

                    response.getWriter().write(json);

                } catch (Exception e) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().write("{\"status\": \"erro\", \"mensagem\": \"" + e.getMessage() + "\"}");
                }
                break;
            }

            case "/dataset/read": {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");

                try (DAOFactory daoFactory = DAOFactory.getInstance()) {
                    dao = daoFactory.getDatasetDAO();

                    int id = Integer.parseInt(request.getParameter("id"));
                    dataset = dao.read(String.valueOf(id));

                    Gson gson = new Gson();
                    response.getWriter().write(gson.toJson(dataset));
                    
                } catch (Exception e) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().write("{\"status\": \"erro\", \"mensagem\": \"" + e.getMessage() + "\"}");
                }
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
        DAO<Dataset> dao;
        Dataset dataset;
        HttpSession session = request.getSession();

        String servletPath = request.getServletPath();

        switch (servletPath) {
            case "/dataset/create": {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");

                response.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
                response.setHeader("Access-Control-Allow-Credentials", "true");

                try {
                    // Tratamento de erro pra sessão nula
                    if (session == null || session.getAttribute("usuario") == null) {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.getWriter().write("{\"status\": \"erro\", \"mensagem\": \"Usuário não autenticado.\"}");
                        return;
                    }
                    User usuarioLogado = (User) session.getAttribute("usuario");

                    String nome = request.getParameter("nome");

                    if (nome == null || nome.trim().isEmpty()) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        response.getWriter().write("{\"status\": \"erro\", \"mensagem\": \"O nome do dataset é obrigatório.\"}");
                        return;
                    }

                    dataset = new Dataset(0, nome, usuarioLogado.getUsername());
                    
                    try (DAOFactory daoFactory = DAOFactory.getInstance()){
                        dao = daoFactory.getDatasetDAO();
                        dao.create(dataset);

                        response.setStatus(HttpServletResponse.SC_OK);
                        response.getWriter().write("{\"status\": \"ok\", \"mensagem\": \"Repositório criado com sucesso!\"}");
                    
                    }
                } catch (Exception e) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().write("{\"status\": \"erro\", \"mensagem\": \"" + e.getMessage() + "\"}");
                }
                break;
            }
            case "/dataset/update": {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");

                try {
                    int id = Integer.parseInt(request.getParameter("id"));
                    String nome = request.getParameter("nome");

                    if (nome == null || nome.trim().isEmpty()) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        response.getWriter().write("{\"status\": \"erro\", \"mensagem\": \"Nome é obrigatório.\"}");
                        return;
                    }

                    dataset = new Dataset(id, nome, null);

                    try (DAOFactory daoFactory = DAOFactory.getInstance()){
                        dao = daoFactory.getDatasetDAO();
                        dao.update(dataset);

                        response.setStatus(HttpServletResponse.SC_OK);
                        response.getWriter().write("{\"status\": \"ok\", \"mensagem\": \"Dataset atualizado com sucesso!\"}");
                    
                    }
                } catch (Exception e) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().write("{\"status\": \"erro\", \"mensagem\": \"" + e.getMessage() + "\"}");
                }
                break;
            }

            case "/dataset/delete": {
                // placeholder
                break;
            }

        }

    }

    @Override
    public String getServletInfo() {
        return "Controller responsável pelas operações relacionadas aos datasets (repositórios).";
    }

}