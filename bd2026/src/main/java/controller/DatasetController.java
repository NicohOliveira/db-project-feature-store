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
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        DAO<User> dao;
        User user;
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

            case "/user/create": {
                dispatcher = request.getRequestDispatcher("/view/user/create.jsp");
                dispatcher.forward(request, response);
                break;
            }

            case "/user/update": {
                try (DAOFactory daoFactory = DAOFactory.getInstance()) {
                    dao = daoFactory.getUserDAO();

                    user = dao.read(request.getParameter("id"));
                    request.setAttribute("user", user);

                    dispatcher = request.getRequestDispatcher("/view/user/update.jsp");
                    dispatcher.forward(request, response);
                } catch (ClassNotFoundException | IOException | SQLException ex) {
                    request.getSession().setAttribute("error", ex.getMessage());
                    response.sendRedirect(request.getContextPath() + "/user");
                }
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

            case "/dataset/read": {
                try (DAOFactory daoFactory = DAOFactory.getInstance()) {
                    dao = daoFactory.getUserDAO();

                    user = dao.read(request.getParameter("id"));

                    Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy").create();
                    String json = gson.toJson(user);

                    response.getOutputStream().print(json);
                } catch (ClassNotFoundException | IOException | SQLException ex) {
                    request.getSession().setAttribute("error", ex.getMessage());
                    response.sendRedirect(request.getContextPath() + "/user");
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        DAO<User> dao;
        User user = new User(null, null);
        HttpSession session = request.getSession();

        String servletPath = request.getServletPath();

        switch (request.getServletPath()) {
            case "/user/create": {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");

                try {
                    String username = request.getParameter("username");
                    String senha = request.getParameter("senha");

                    User usuario = new User(username, senha);

                    PgConnectionFactory factory = new PgConnectionFactory();
                    Connection conn = factory.getConnection();
                    PgUserDAO usuarioDao = new PgUserDAO(conn);
                    usuarioDao.create(usuario);

                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().write("{\"status\": \"ok\", \"mensagem\": \"Usuário cadastrado com sucesso!\"}");

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
                // Se fosse um form simples, usaria request.getParameter()
                // String login = request.getParameter("login");

                // Manipulação de form com enctype="multipart/form-data"
                // Create a factory for disk-based file items
                DiskFileItemFactory factory = new DiskFileItemFactory();
                // Set factory constraints
                factory.setSizeThreshold(MAX_FILE_SIZE);
                // Set the directory used to temporarily store files that are larger than the configured size threshold
                factory.setRepository(new File("/tmp"));
                // Create a new file upload handler
                ServletFileUpload upload = new ServletFileUpload(factory);
                // Set overall request size constraint
                upload.setSizeMax(MAX_FILE_SIZE);

                try (DAOFactory daoFactory = DAOFactory.getInstance()) {
                    // Parse the request
                    List<FileItem> items = upload.parseRequest(request);

                    // Process the uploaded items
                    Iterator<FileItem> iter = items.iterator();
                    while (iter.hasNext()) {
                        FileItem item = iter.next();

                        // Process a regular form field
                        if (item.isFormField()) {
                            String fieldName = item.getFieldName();
                            String fieldValue = item.getString();

                            switch (fieldName) {
                                case "login":
                                    user.setUsername(fieldValue);
                                    break;
                                case "senha":
                                    user.setSenha(fieldValue);
                                    break;
                                case "nome":
                                    user.setUsername(fieldValue);
                                    break;
                                // case "nascimento":
                                //     java.util.Date dataNascimento = new SimpleDateFormat("yyyy-MM-dd").parse(fieldValue);
                                //     user.setNascimento(new Date(dataNascimento.getTime()));
                                //     break;
                                // case "id":
                                //     user.setId(Integer.valueOf(fieldValue));
                            }
                        } else {
                            String fieldName = item.getFieldName();
                            String fileName = item.getName();
                            if (fieldName.equals("avatar") && !fileName.isBlank()) {
                                // Dados adicionais (não usado nesta aplicação)
                                String contentType = item.getContentType();
                                boolean isInMemory = item.isInMemory();
                                long sizeInBytes = item.getSize();

                                // Pega o caminho absoluto da aplicação
                                String appPath = request.getServletContext().getRealPath("");
                                // Grava novo arquivo na pasta img no caminho absoluto
                                String savePath = appPath + File.separator + 0 + File.separator + fileName;
                                File uploadedFile = new File(savePath);
                                item.write(uploadedFile);

                                // user.setAvatar(fileName);
                            }
                        }
                    }

                    dao = daoFactory.getUserDAO();

                    if (servletPath.equals("/user/create")) {
                        dao.create(user);
                    } else {
                        // servletPath += "?id=" + String.valueOf(user.getId());
                        dao.update(user);
                    }

                    response.sendRedirect(request.getContextPath() + "/user");

                } catch (ParseException ex) {
                    Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, "Controller", ex);
                    session.setAttribute("error", "O formato de data não é válido. Por favor entre data no formato dd/mm/aaaa");
                    response.sendRedirect(request.getContextPath() + servletPath);
                } catch (FileUploadException ex) {
                    Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, "Controller", ex);
                    session.setAttribute("error", "Erro ao fazer upload do arquivo.");
                    response.sendRedirect(request.getContextPath() + servletPath);
                } catch (ClassNotFoundException | IOException | SQLException ex) {
                    Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, "Controller", ex);
                    session.setAttribute("error", ex.getMessage());
                    response.sendRedirect(request.getContextPath() + servletPath);
                } catch (Exception ex) {
                    Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, "Controller", ex);
                    session.setAttribute("error", "Erro ao gravar arquivo no servidor.");
                    response.sendRedirect(request.getContextPath() + servletPath);
                }
                break;
            }

            case "/user/delete": {
                String[] users = request.getParameterValues("delete");

                try (DAOFactory daoFactory = DAOFactory.getInstance()) {
                    dao = daoFactory.getUserDAO();

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