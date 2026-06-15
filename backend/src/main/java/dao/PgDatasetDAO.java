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

import javax.xml.crypto.Data;

import org.mindrot.jbcrypt.BCrypt;

import model.Dataset;
import model.User;

/**
 *
 * @author dskaster
 */
public class PgDatasetDAO implements DatasetDAO {

    private final Connection connection;

    private static final String GETALL_QUERY =
                                "SELECT username, senha " +
                                "FROM usuario " +
                                "WHERE username = ?;";
    
    private static final String CREATE_QUERY =
                                "INSERT INTO Dataset(nome, username_criador) " +
                                "VALUES( ?, ?);";
    
    private static final String ALL_QUERY =
                                "SELECT * " +
                                "FROM dataset " +
                                "ORDER BY username_criador;";

    private static final String READ_QUERY =
                                "SELECT username " +
                                "FROM Usuario " +
                                "WHERE username = ?;";

    private static final String UPDATE_QUERY =
                                "UPDATE Usuario " +
                                "SET senha = ? " +
                                "WHERE username = ?;";

    private static final String DELETE_QUERY =
                                "DELETE FROM Usuario " +
                                "WHERE username = ?;";
    
    public PgDatasetDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void create(Dataset dataset) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(CREATE_QUERY)) {
            //statement.setString(1, String.valueOf(dataset.getId()));
            statement.setString(1, dataset.getNome());
            statement.setString(2, dataset.getUsernameCriador());

            statement.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(PgDatasetDAO.class.getName()).log(Level.SEVERE, "DAO", ex);

            // fazer os errorMsg

            //so erro de criação pq aceita nome igual kk
            throw new SQLException("Erro ao criar repositório: " + ex.getMessage());
        }
    }

    @Override
    public Dataset read(String datasetId) throws SQLException {
        // Um return de placeholder pra n acusar falta de implementação da interface DAO
        return new Dataset(0, datasetId, datasetId);
    }

    @Override
    public List<Dataset> all() throws SQLException {
        List<Dataset> datasets = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(ALL_QUERY)) {
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                Dataset d = new Dataset(0, null, null);
                d.setId(result.getInt("id_dataset"));
                d.setNome(result.getString("nome"));
                d.setUsernameCriador(result.getString("username_criador"));
                datasets.add(d);
            }
        } catch (SQLException ex) {
            Logger.getLogger(PgUserDAO.class.getName()).log(Level.SEVERE, "DAO", ex);
            
            throw new SQLException("Erro ao listar datasets.");
        }

        return datasets;

    }

    @Override
    public void update(Dataset dataset) throws SQLException {
        // try (PreparedStatement statement = connection.prepareStatement(UPDATE_QUERY)) {
        //     // update tava feio cheio de comentario, adaptei ja pro bcrypt pra aproveitar e limpar
        //     String senhaHash = BCrypt.hashpw(user.getSenha(), BCrypt.gensalt());

        //     statement.setString(1, senhaHash);
        //     statement.setString(2, user.getUsername());

        //     if (statement.executeUpdate() < 1) {
        //         throw new SQLException("Erro ao editar: usuário não encontrado.");
        //     }
        // } catch (SQLException ex) {
        //     Logger.getLogger(PgUserDAO.class.getName()).log(Level.SEVERE, "DAO", ex);
        //     throw new SQLException("Erro ao editar a senha do usuário.");
        // }
    }

    @Override
    public void delete(String username) throws SQLException {
        // try (PreparedStatement statement = connection.prepareStatement(DELETE_QUERY)) {
        //     statement.setString(1, username);

        //     if (statement.executeUpdate() < 1) {
        //         throw new SQLException("Erro ao excluir: usuário não encontrado.");
        //     }
        // } catch (SQLException ex) {
        //     Logger.getLogger(PgUserDAO.class.getName()).log(Level.SEVERE, "DAO", ex);
        //         throw new SQLException("Erro ao excluir usuário.");
        // }
    }

    /*

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

    */
}
