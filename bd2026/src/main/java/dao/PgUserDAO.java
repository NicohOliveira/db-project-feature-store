/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mindrot.jbcrypt.BCrypt;

import model.User;

/**
 *
 * @author dskaster
 */
public class PgUserDAO implements UserDAO {

    private final Connection connection;

    private static final String AUTHENTICATE_QUERY =
                                "SELECT username " +
                                "FROM usuario " +
                                "WHERE username = ? AND senha = ?;";
    
    private static final String CREATE_QUERY =
                                "INSERT INTO Usuario(username, senha) " +
                                "VALUES(?, ?);";
    
    private static final String ALL_QUERY =
                                "SELECT username " +
                                "FROM Usuario " +
                                "ORDER BY username;";

    private static final String READ_QUERY =
                                "SELECT username " +
                                "FROM Usuario " +
                                "WHERE username = ?;";

    private static final String UPDATE_QUERY =
                                "UPDATE Usuario " +
                                "SET senha = md5(?) " +
                                "WHERE username = ?;";

    private static final String DELETE_QUERY =
                                "DELETE FROM Usuario " +
                                "WHERE username = ?;";
    
    public PgUserDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void authenticate(User user) throws SQLException, SecurityException {
        try (PreparedStatement statement = connection.prepareStatement(AUTHENTICATE_QUERY)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getSenha());

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    user.setUsername(result.getString("username"));
                } else {
                    throw new SecurityException("Login ou senha incorretos.");
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(PgUserDAO.class.getName()).log(Level.SEVERE, "DAO", ex);

            throw new SQLException("Erro ao autenticar usuário.");
        }                
    }
    @Override
    public User getByLogin(String login) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }/* nao sei q fazer com isso por enquanto entao deixarei pro java nao chorar*/

    @Override
    public void create(User user) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(CREATE_QUERY)) {
            statement.setString(1, user.getUsername());

            String senhaHash = BCrypt.hashpw(user.getSenha(), BCrypt.gensalt());
            statement.setString(2, senhaHash);

            statement.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(PgUserDAO.class.getName()).log(Level.SEVERE, "DAO", ex);

            if (ex.getMessage().contains("usuario_pkey")) {
                throw new SQLException("Erro ao inserir usuario: nome de usuário já existe.");
            } else if (ex.getMessage().contains("not-null")) {
                throw new SQLException("Erro ao inserir usuário: pelo menos um campo está em branco.");
            } else {
                throw new SQLException("Erro ao inserir usuário.");
            }
        }        
    }

    @Override
    public User read(String username) throws SQLException {
        User user = new User(null, null);

        try (PreparedStatement statement = connection.prepareStatement(READ_QUERY)) {
            statement.setString(1, username);
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    user.setUsername(username);
                } else {
                    throw new SQLException("Erro ao visualizar: usuário não encontrado.");
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(PgUserDAO.class.getName()).log(Level.SEVERE, "DAO", ex);
            
            if (ex.getMessage().equals("Erro ao visualizar: usuário não encontrado.")) {
                throw ex;
            } else {
                throw new SQLException("Erro ao visualizar usuário.");
            }
        }

        return user;

    }

    @Override
    public void update(User user) throws SQLException {
        //String query;

        // vou tirar os ifs ja que so temos usuario e senha e ver se não capota, por enquanto só comentar


        /*if ((user.getSenha() == null) || (user.getSenha().isBlank())) {
            if ((user.getAvatar() == null) || (user.getAvatar().isBlank()))
                query = UPDATE_QUERY;
            else
                query = UPDATE_WITH_AVATAR_QUERY;
        } else {
            if ((user.getAvatar() == null) || (user.getAvatar().isBlank()))
                query = UPDATE_WITH_PASSWORD_QUERY;
            else
                query = UPDATE_WITH_AVATAR_AND_PASSWORD_QUERY;
        }*/

        try (PreparedStatement statement = connection.prepareStatement(UPDATE_QUERY)) {
            /*statement.setString(1, user.getSenha());
            statement.setString(2, user.getLogin());
            statement.setDate(3, user.getNascimento());

            if ((user.getSenha() == null) || (user.getSenha().isBlank())) {
                if ((user.getAvatar() == null) || (user.getAvatar().isBlank())) {
                    statement.setInt(4, user.getId());
                } else {
                    statement.setString(4, user.getAvatar());
                    statement.setInt(5, user.getId());
                }
            } else {
                if ((user.getAvatar() == null) || (user.getAvatar().isBlank())) {
                    statement.setString(4, user.getSenha());
                    statement.setInt(5, user.getId());
                } else {
                    statement.setString(4, user.getAvatar());
                    statement.setString(5, user.getSenha());
                    statement.setInt(6, user.getId());
                }
            }

            if (statement.executeUpdate() < 1) {
                throw new SQLException("Erro ao editar: usuário não encontrado.");
            }
        } catch (SQLException ex) {
            Logger.getLogger(PgUserDAO.class.getName()).log(Level.SEVERE, "DAO", ex);

            if (ex.getMessage().equals("Erro ao editar: usuário não encontrado.")) {
                throw ex;
            } else if (ex.getMessage().contains("uq_user_login")) {
                throw new SQLException("Erro ao editar usuário: login já existente.");
            } else if (ex.getMessage().contains("not-null")) {
                throw new SQLException("Erro ao editar usuário: pelo menos um campo está em branco.");
            } else {
                throw new SQLException("Erro ao editar usuário.");
            }
            throw new SQLException("Erro ao editar a senha do usuário.");*/
            statement.setString(1, user.getSenha());
            statement.setString(2, user.getUsername());

            if (statement.executeUpdate() < 1) {
                throw new SQLException("Erro ao editar: usuário não encontrado.");
            }
        } catch (SQLException ex) {
            Logger.getLogger(PgUserDAO.class.getName()).log(Level.SEVERE, "DAO", ex);
            throw new SQLException("Erro ao editar a senha do usuário.");
        }        
    }

    @Override
    public void delete(String username) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(DELETE_QUERY)) {
            statement.setString(1, username);

            if (statement.executeUpdate() < 1) {
                throw new SQLException("Erro ao excluir: usuário não encontrado.");
            }
        } catch (SQLException ex) {
            Logger.getLogger(PgUserDAO.class.getName()).log(Level.SEVERE, "DAO", ex);
                throw new SQLException("Erro ao excluir usuário.");
        }
    }

    @Override
    public List<User> all() throws SQLException {
        List<User> userList = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(ALL_QUERY);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                User user = new User(null, null);
                user.setUsername(result.getString("username"));
                userList.add(user);
            }
        } catch (SQLException ex) {
            Logger.getLogger(PgUserDAO.class.getName()).log(Level.SEVERE, "DAO", ex);

            throw new SQLException("Erro ao listar usuários.");
        }

        return userList;        
    }
    
}
