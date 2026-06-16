package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dao.DAO;
import dao.DAOFactory;
import dao.PgDatasetDAO;
import dao.VersaoDAO;
import dao.UserDAO;
import dao.PgVersaoDAO;
import jdbc.PgConnectionFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
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
import model.Versao;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

@WebServlet(name = "VersaoController",
        urlPatterns = {
                "/versao",
                "/versao/history",
                "/versao/download",
                "/versao/create",
                "/versao/update",
                "/versao/delete"
        })
public class VersaoController extends HttpServlet {
    private static final int MAX_FILE_SIZE = 1024 * 1024 * 4;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
        response.setHeader("Access-Control-Allow-Credentials", "true");

        HttpSession session = request.getSession();
        switch (request.getServletPath()) {
           /* case "/dataset/create": {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");

                try {
                    //tratamento de erro pra sessão nula
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

                    Dataset novoDataset = new Dataset(0, nome, usuarioLogado.getUsername());
                    PgConnectionFactory factory = new PgConnectionFactory();
                    Connection conn = factory.getConnection();
                    PgDatasetDAO datasetDao = new PgDatasetDAO(conn);
                    datasetDao.create(novoDataset);

                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().write("{\"status\": \"ok\", \"mensagem\": \"Repositório criado com sucesso!\"}");
                } catch (Exception e) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().write("{\"status\": \"erro\", \"mensagem\": \"" + e.getMessage() + "\"}");
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
            }*/

            case "/versao/delete": {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");

                try (DAOFactory daoFactory = DAOFactory.getInstance()) {
                    User usuarioLogado = (User) session.getAttribute("usuario");
                    String idVersao = request.getParameter("id");
                    String senha = request.getParameter("senha");

                    if (usuarioLogado == null) throw new Exception("Usuário não autenticado.");
                    UserDAO userDao = daoFactory.getUserDAO();
                    User credenciais = new User(usuarioLogado.getUsername(), senha);
                    userDao.authenticate(credenciais);
                    PgVersaoDAO versaoDao = (PgVersaoDAO) daoFactory.getVersaoDAO();

                    versaoDao.delete(idVersao, usuarioLogado.getUsername());

                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().write("{\"status\": \"sucesso\"}");

                } catch (Exception e) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
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

                    PgConnectionFactory factory = new PgConnectionFactory();
                    Connection conn = factory.getConnection();
                    dao.PgVersaoDAO versaoDao = new dao.PgVersaoDAO(conn);

                    // Busca a lista completa de versões daquele dataset
                    List<Versao> historico = versaoDao.listByDataset(idDataset);

                    // Converte para JSON (formatando a data bonitinho)
                    Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy").create();
                    String json = gson.toJson(historico);

                    response.getWriter().write(json);

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

                    PgConnectionFactory factory = new PgConnectionFactory();
                    Connection conn = factory.getConnection();
                    dao.PgVersaoDAO versaoDao = new dao.PgVersaoDAO(conn);

                    String idComposto = idDatasetStr + "-" + numVersaoStr;
                    Versao versao = versaoDao.read(idComposto);

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

        /*DAO<User> dao;
        User user = new User(null, null);
        HttpSession session = request.getSession();

        String servletPath = request.getServletPath();

        switch (request.getServletPath()) {
            case "/dataset/create": {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                //tentando consertar o rpoblema do controller nao estar aceitando credenciais do react

                response.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
                response.setHeader("Access-Control-Allow-Credentials", "true");

                try {
                    //tratamento de erro pra sessão nula
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

                    Dataset novoDataset = new Dataset(0, nome, usuarioLogado.getUsername());
                    PgConnectionFactory factory = new PgConnectionFactory();
                    Connection conn = factory.getConnection();
                    PgDatasetDAO datasetDao = new PgDatasetDAO(conn);
                    datasetDao.create(novoDataset);

                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().write("{\"status\": \"ok\", \"mensagem\": \"Repositório criado com sucesso!\"}");
                } catch (Exception e) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().write("{\"status\": \"erro\", \"mensagem\": \"" + e.getMessage() + "\"}");
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

        } */

    }
    @Override
    public String getServletInfo() {
        return "Controller responsável por Histórico e Download de Versões (Parte B)";
    }
}