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

import model.VersaoFontes;

/**
 *
 * @author dskaster
 */
public class PgFonteDAO implements FonteDAO {

    private final Connection connection;

    private static final String CREATE_QUERY =
            "INSERT INTO versao_fontes(id_dataset, num_versao, fonte) " +
                    "VALUES(?, ?, ?);";

    private static final String ALL_DATAVERSION_QUERY =
            "SELECT fonte FROM versao_fontes " +
                    "WHERE id_dataset = ? AND num_versao = ?;";

    private static final String ALL_QUERY = 
            "SELECT * FROM versao_fontes(id_dataset, num_versao, fonte) ";

    // private static final String READ_QUERY =
    //         "SELECT * FROM Versao WHERE id_dataset = ? AND num_versao = ?;";

    // private static final String READASC_QUERY =
    //         "SELECT * FROM Versao WHERE id_dataset = ? ORDER BY num_versao ASC;";

    // private static final String READ_FEATURES_QUERY =
    //         "SELECT nome_coluna, tipo_dado, descricao FROM Feature WHERE id_dataset = ? AND num_versao = ?;";

    // private static final String DELETE_QUERY =
    //         "DELETE FROM Versao WHERE id_dataset = ? AND num_versao = ? AND username_autor = ?;";

    public PgFonteDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void create(VersaoFontes fonte) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(CREATE_QUERY)) {
            statement.setInt(1, fonte.getIdDataset());
            statement.setInt(2, fonte.getNumVersao());
            statement.setString(3, fonte.getFonte());

            statement.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(PgFonteDAO.class.getName()).log(Level.SEVERE, "DAO", ex);

            if (ex.getMessage().contains("versao_fontes_pkey")) {
                throw new SQLException("Erro ao inserir fonte: Fonte duplicada.");
            } else if (ex.getMessage().contains("not-null")) {
                throw new SQLException("Erro ao inserir fonte: pelo menos um campo está em branco.");
            } else {
                throw new SQLException("Erro ao inserir fonte.");
            }
        }        
    }

    @Override
    public VersaoFontes read(String id) throws SQLException {
        // placeholder
        return null;
    }

    @Override
    public void update(VersaoFontes v) throws SQLException {
        // placeholder
    }

    @Override
    public void delete(String fonteStr) throws SQLException {
        // placeholder
        throw new UnsupportedOperationException("Usar o delete(String id, String username) para exclusão segura.");
    }

    @Override
    public List<VersaoFontes> all() throws SQLException {
        List<VersaoFontes> fontes = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(ALL_QUERY)) {
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                VersaoFontes v = new VersaoFontes(0, 0, null);
                
                v.setIdDataset(result.getInt("id_dataset"));
                v.setNumVersao(result.getInt("num_versao"));
                v.setFonte(result.getString("fonte"));

                fontes.add(v);
            }
        } catch (SQLException ex) {
            Logger.getLogger(PgUserDAO.class.getName()).log(Level.SEVERE, "DAO", ex);
            throw new SQLException("Erro ao listar datasets.");
        }

        return fontes;
    }

    @Override
    public List<VersaoFontes> allDatasetVersionSources(int datasetId, int num_versao) throws SQLException {
        List<VersaoFontes> fontes = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(ALL_DATAVERSION_QUERY)) {
            statement.setInt(1, datasetId);
            statement.setInt(2, num_versao);
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                VersaoFontes v = new VersaoFontes(datasetId, num_versao, null);
                
                v.setFonte(result.getString("fonte"));

                fontes.add(v);
            }
        } catch (SQLException ex) {
            Logger.getLogger(PgUserDAO.class.getName()).log(Level.SEVERE, "DAO", ex);
            throw new SQLException("Erro ao listar datasets.");
        }

        return fontes;
    }
}