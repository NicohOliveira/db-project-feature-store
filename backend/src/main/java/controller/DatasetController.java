/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dao.DAO;
import dao.DAOFactory;
import dao.PgDatasetDAO;
import dao.PgUserDAO;
import dao.UserDAO;
import jdbc.PgConnectionFactory;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.Dataset;
import model.User;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

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
    private static final int MAX_FILE_SIZE = 1024 * 1024 * 4;

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
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
        RequestDispatcher dispatcher;

        switch (request.getServletPath()) {
            case "/dataset": {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");

                try {
                    PgConnectionFactory factory = new PgConnectionFactory();
                    Connection conn = factory.getConnection();
                    PgDatasetDAO datasetDao = new PgDatasetDAO(conn);

                    List<Dataset> datasets = datasetDao.all();

                    Gson gson = new Gson();
                    String json = gson.toJson(datasets);

                    response.getWriter().write(json);

                } catch (Exception e) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().write("{\"status\": \"erro\", \"mensagem\": \"" + e.getMessage() + "\"}");
                }
                break;
            }

            /* Não foi usado, vou guardar para caso utilize no futuro.

            case "/user/create": {
                dispatcher = request.getRequestDispatcher("/view/user/create.jsp");
                dispatcher.forward(request, response);
                break;
            }

            case "/user/delete": {
                try (DAOFactory daoFactory = DAOFactory.getInstance()) {
                    dao = daoFactory.getUserDAO();
                    dao.delete(request.getParameter("id"));
                } catch (ClassNotFoundException | IOException | SQLException ex) {
                    request.getSession().setAttribute("error", ex.getMessage());
                }

                response.sendRedirect(request.getContextPath() + "/user");
                break;
            }

            */

            case "/dataset/read": {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");

                try {
                    int id = Integer.parseInt(request.getParameter("id"));

                    PgConnectionFactory factory = new PgConnectionFactory();
                    Connection conn = factory.getConnection();
                    PgDatasetDAO datasetDao = new PgDatasetDAO(conn);

                    dataset = datasetDao.read(String.valueOf(id));

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
        Dataset dataset = new Dataset(0, null, null);
        HttpSession session = request.getSession();

        String servletPath = request.getServletPath();

        switch (request.getServletPath()) {
            case "/dataset/create": {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                // Tentando consertar o problema do controller nao estar aceitando credenciais do react

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
                    
                    PgConnectionFactory factory = new PgConnectionFactory();
                    Connection conn = factory.getConnection();
                    PgDatasetDAO datasetDao = new PgDatasetDAO(conn);
                    
                    datasetDao.create(dataset);

                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().write("{\"status\": \"ok\", \"mensagem\": \"Repositório criado com sucesso!\"}");
                
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

                    PgConnectionFactory factory = new PgConnectionFactory();
                    Connection conn = factory.getConnection();
                    PgDatasetDAO datasetDao = new PgDatasetDAO(conn);

                    datasetDao.update(dataset);

                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().write("{\"status\": \"ok\", \"mensagem\": \"Dataset atualizado com sucesso!\"}");

                } catch (Exception e) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().write("{\"status\": \"erro\", \"mensagem\": \"" + e.getMessage() + "\"}");
                }
                break;
            }

            case "/user/delete": {
                String[] users = request.getParameterValues("delete");

                try (DAOFactory daoFactory = DAOFactory.getInstance()) {
                    dao = daoFactory.getDatasetDAO();

                    try {
                        daoFactory.beginTransaction();

                        for (String userId : users) {
                            dao.delete(userId);
                        }

                        daoFactory.commitTransaction();
                        daoFactory.endTransaction();
                    } catch (SQLException ex) {
                        session.setAttribute("error", ex.getMessage());
                        daoFactory.rollbackTransaction();
                    }
                } catch (ClassNotFoundException | IOException ex) {
                    Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, "Controller", ex);
                    session.setAttribute("error", ex.getMessage());
                } catch (SQLException ex) {
                    Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, "Controller", ex);
                    session.setAttribute("rollbackError", ex.getMessage());
                }

                response.sendRedirect(request.getContextPath() + "/user");
                break;
            }

        }

    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}