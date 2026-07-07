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

import model.Dataset;

/**
 *
 * @author dskaster
 */
public class PgDatasetDAO implements DatasetDAO {

    private final Connection connection;

    // private static final String GETALL_QUERY =
    //                             "SELECT nome, username_criador " +
    //                             "FROM dataset " +
    //                             "WHERE username_criador = ?;";
    
    private static final String CREATE_QUERY =
                                "INSERT INTO Dataset(nome, username_criador) " +
                                "VALUES( ?, ?);";
    
    private static final String ALL_QUERY =
                                "SELECT * " +
                                "FROM dataset " +
                                "ORDER BY username_criador;";

    private static final String READ_QUERY =
                                "SELECT * " +
                                "FROM dataset " +
                                "WHERE id_dataset = ?;";

    private static final String UPDATE_QUERY =
                                "UPDATE dataset " +
                                "SET nome = ? " +
                                "WHERE id_dataset = ?;";

    // private static final String DELETE_QUERY =
    //                             "DELETE FROM Usuario " +
    //                             "WHERE username = ?;";
    
    public PgDatasetDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void create(Dataset dataset) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(CREATE_QUERY)) {
            statement.setString(1, dataset.getNome());
            statement.setString(2, dataset.getUsernameCriador());

            statement.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(PgDatasetDAO.class.getName()).log(Level.SEVERE, "DAO", ex);
            throw new SQLException("Erro ao criar repositório: " + ex.getMessage());
        }
    }

    @Override
    public Dataset read(String datasetId) throws SQLException {
        int id = Integer.parseInt(datasetId);
        Dataset dataset = new Dataset(id, null, null);

        try (PreparedStatement statement = connection.prepareStatement(READ_QUERY)) {
            statement.setInt(1, id);

            ResultSet result = statement.executeQuery();

            while (result.next()) {
                dataset.setId(result.getInt("id_dataset"));
                dataset.setNome(result.getString("nome"));
                dataset.setUsernameCriador(result.getString("username_criador"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(PgDatasetDAO.class.getName()).log(Level.SEVERE, "DAO", ex);
            throw new SQLException("Erro ao buscar dataset.");
        }

        return dataset;
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
            Logger.getLogger(PgDatasetDAO.class.getName()).log(Level.SEVERE, "DAO", ex);
            throw new SQLException("Erro ao listar datasets.");
        }

        return datasets;
    }

    @Override
    public void update(Dataset dataset) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_QUERY)) {
            statement.setString(1, dataset.getNome());
            statement.setInt(2, dataset.getId());

            if (statement.executeUpdate() < 1) {
                throw new SQLException("Erro ao editar: dataset não encontrado.");
            }
        } catch (SQLException ex) {
            Logger.getLogger(PgDatasetDAO.class.getName()).log(Level.SEVERE, "DAO", ex);
            throw new SQLException("Erro ao atualizar dataset: " + ex.getMessage());
        }
    }

    @Override
    public void delete(String username) throws SQLException {
        // placeholder
    }
}
