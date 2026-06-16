/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dao.DAOFactory;
import dao.UserDAO;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.User;

/**
 *
 * @author dskaster
 */
@WebServlet(name = "LoginController", 
        urlPatterns = {
            "",
            "/logout",
            "/login"
        })

public class LoginController extends HttpServlet {
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
        HttpSession session;
        
        switch (request.getServletPath()) {
            case "": {
                // placeholder
                break;
            }
            
            case "/logout": {
                session = request.getSession(false);

                if (session != null) {
                    session.invalidate();
                }

                response.sendRedirect(request.getContextPath() + "/");
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
        UserDAO dao;
        User user = new User(null, null);
        HttpSession session = request.getSession();

        String servletPath = request.getServletPath();

        response.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
        response.setHeader("Access-Control-Allow-Credentials", "true");

        switch (servletPath) {
            case "/login":
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");

                // mudei pra username pra manter o padrao
                user.setUsername(request.getParameter("username"));
                user.setSenha(request.getParameter("senha"));

                try (DAOFactory daoFactory = DAOFactory.getInstance()) {
                    dao = daoFactory.getUserDAO();
                    dao.authenticate(user);
                    
                    session.setAttribute("usuario", user);

                    // aqui só adicionei pra retornar sucesso em json
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().write("{\"status\": \"ok\", \"mensagem\": \"Login realizado com sucesso!\"}");
                
                } catch (Exception ex ) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("{\"status\": \"erro\", \"mensagem\": \"Usuário ou senha incorretos.\"}");
                }
        }                        
    }

    @Override
    public String getServletInfo() {
        return "Controller responsável por operações de login / logout.";
    }

}
