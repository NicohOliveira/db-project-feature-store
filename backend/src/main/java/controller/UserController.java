/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

// import com.google.gson.Gson;
// import com.google.gson.GsonBuilder;
import dao.DAO;
import dao.DAOFactory;

import java.io.IOException;
// import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
// import javax.servlet.http.HttpSession;
import model.User;

/**
 *
 * @author dskaster
 */
@WebServlet(name = "UserController",
        urlPatterns = {
            "/user",
            "/user/create",
            "/user/update",
            "/user/delete",
            "/user/read"
        })
public class UserController extends HttpServlet {
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // DAO<User> dao;
        // User user;

        switch (request.getServletPath()) {
            case "/user": {
                // placeholder
                break;
            }

            case "/user/create": {
                // placeholder
                break;
            }

            case "/user/update": {
                // placeholder
                break;
            }

            case "/user/delete": {
                // placeholder
                break;
            }

            case "/user/read": {
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        DAO<User> dao;
        User user = new User(null, null);
        // HttpSession session = request.getSession();

        String servletPath = request.getServletPath();

        switch (servletPath) {
            case "/user/create": {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");

                try {
                    String username = request.getParameter("username");
                    String senha = request.getParameter("senha");

                    user = new User(username, senha);
                    
                    try (DAOFactory daoFactory = DAOFactory.getInstance()) {
                        dao = daoFactory.getUserDAO();
                        
                        dao.create(user);

                        response.setStatus(HttpServletResponse.SC_OK);
                        response.getWriter().write("{\"status\": \"ok\", \"mensagem\": \"Usuário cadastrado com sucesso!\"}");
                    
                    }
                } catch (Exception e) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    
                    if (e.getMessage() != null && e.getMessage().contains("duplicate key")) {
                        response.getWriter().write("{\"status\": \"erro\", \"mensagem\": \"Nome de usuário já existe.\"}");
                    } else {
                        response.getWriter().write("{\"status\": \"erro\", \"mensagem\": \"" + e.getMessage() + "\"}");
                    }
                }
                break;
            }
            case "/user/update": {
                // placeholder
                break;
            }

            case "/user/delete": {
                // placeholder
                break;
            }

        }

    }

    @Override
    public String getServletInfo() {
        return "Controller responsável pelas operações com relação ao usuário.";
    }

}