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

import java.sql.Date;

import model.DadosAcesso;
import model.RegistroAcesso;

/**
 *
 * @author dskaster
 */
public class PgRegistroAcessoDAO implements RegistroAcessoDAO {

    private final Connection connection;

    private static final String INSERT_QUERY =
            "INSERT INTO Registro_Acesso (data_acesso, hora_acesso, " +
                    "tipo_acao, username_leitor, " +
                    "id_dataset_acessada, num_versao_acessada) " +
                    "VALUES (CURRENT_DATE, CURRENT_TIME, ?, ?, ?, ?);";

    private static final String CREATE_QUERY =
            "INSERT INTO registro_acesso(data_acesso, hora_acesso, tipo_acao, username_leitor, id_dataset_acessada, num_versao_acessada)\n" + //
                    "VALUES(?, ?, ?, ?, ?, ?);";

    private static final String READ_DATEVERSION_QUERY =
            "SELECT data_acesso AS data, " +
                    "COUNT(*) FILTER (WHERE tipo_acao = 'VISUALIZACAO') AS visualizacoes, " +
                    "COUNT(*) FILTER (WHERE tipo_acao = 'DOWNLOAD') AS downloads " +
                    "FROM registro_acesso " +
                    "WHERE data_acesso BETWEEN ? AND ? " +
                    "AND id_dataset_acessada = ? AND num_versao_acessada = ? " +
                    "GROUP BY data_acesso " +
                    "ORDER BY data_acesso ASC;";

    private static final String READ_DATEDATASET_QUERY =
            "SELECT data_acesso AS data, num_versao_acessada AS versao, " +
                    "COUNT(*) FILTER (WHERE tipo_acao = 'VISUALIZACAO') AS visualizacoes, " +
                    "COUNT(*) FILTER (WHERE tipo_acao = 'DOWNLOAD') AS downloads " +
                    "FROM registro_acesso " +
                    "WHERE data_acesso BETWEEN ? AND ? " +
                    "AND id_dataset_acessada = ? " +
                    "GROUP BY data_acesso, num_versao_acessada " +
                    "ORDER BY data_acesso ASC;";

    private static final String READ_VIEWSVERSION_QUERY =
            "SELECT num_versao_acessada AS versao, " +
                    "COUNT(*) FILTER (WHERE tipo_acao = 'VISUALIZACAO') AS visualizacoes " +
                    "FROM registro_acesso " +
                    "WHERE data_acesso BETWEEN ? AND ? " +
                    "AND id_dataset_acessada = ? " +
                    "GROUP BY num_versao_acessada " +
                    "ORDER BY visualizacoes DESC;";

    private static final String READ_DOWNLOADSVERSION_QUERY =
            "SELECT num_versao_acessada AS versao, " +
                    "COUNT(*) FILTER (WHERE tipo_acao = 'DOWNLOAD') AS downloads " +
                    "FROM registro_acesso " +
                    "WHERE data_acesso BETWEEN ? AND ? " +
                    "AND id_dataset_acessada = ? " +
                    "GROUP BY num_versao_acessada " +
                    "ORDER BY downloads DESC;";

    // private static final String READ_QUERY =
    //         "SELECT * FROM Versao WHERE id_dataset = ? AND num_versao = ?;";

    // private static final String READASC_QUERY =
    //         "SELECT * FROM Versao WHERE id_dataset = ? ORDER BY num_versao ASC;";

    // private static final String READ_FEATURES_QUERY =
    //         "SELECT nome_coluna, tipo_dado, descricao FROM Feature WHERE id_dataset = ? AND num_versao = ?;";

    // private static final String DELETE_QUERY =
    //         "DELETE FROM Versao WHERE id_dataset = ? AND num_versao = ? AND username_autor = ?;";

    public PgRegistroAcessoDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void create(RegistroAcesso rg) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(CREATE_QUERY)) {
            statement.setDate(1, rg.getDataAcesso());
            statement.setTime(2, rg.getHoraAcesso());
            statement.setString(3, rg.getTipoAcao());
            statement.setString(4, rg.getUsernameAutor());
            statement.setInt(5, rg.getIdDatasetAcessada());
            statement.setInt(6, rg.getNumVersaoAcessada());

            statement.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(PgRegistroAcessoDAO.class.getName()).log(Level.SEVERE, "DAO", ex);

            if (ex.getMessage().contains("id_acesso_pkey")) {
                throw new SQLException("Erro ao inserir registro: Registro duplicada.");
            } else if (ex.getMessage().contains("not-null")) {
                throw new SQLException("Erro ao inserir registro: pelo menos um campo está em branco.");
            } else {
                throw new SQLException("Erro ao inserir registro.");
            }
        }        
    }

    @Override
    public RegistroAcesso read(String id) throws SQLException {
        // placeholder
        return null;
    }

    @Override
    public void update(RegistroAcesso rg) throws SQLException {
        // placeholder
    }

    @Override
    public void delete(String rgId) throws SQLException {
        // placeholder
        throw new UnsupportedOperationException("Usar o delete(String id, String username) para exclusão segura.");
    }

    @Override
    public List<RegistroAcesso> all() throws SQLException {
        // placeholder
        return new ArrayList<>();
    }

    @Override
    public void registrar(int idDataset, int numVersao, String username, String tipoAcao) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(INSERT_QUERY)) {
            stmt.setString(1, tipoAcao);

            if (username != null && !username.trim().isEmpty()) {
                stmt.setString(2, username);
            } else {
                stmt.setNull(2, java.sql.Types.VARCHAR);
            }

            stmt.setInt(3, idDataset);
            stmt.setInt(4, numVersao);

            stmt.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(PgRegistroAcessoDAO.class.getName()).log(Level.SEVERE, "Erro ao registrar tracking de acesso", ex);
            throw new SQLException("Erro ao registrar acesso no banco de dados.");
        }
    }

    @Override
    public List<DadosAcesso> allVersionAcessesBetween(Date startDate, Date endDate, int datasetId, int num_versao) throws SQLException {
        List<DadosAcesso> registros = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(READ_DATEVERSION_QUERY)) {
            statement.setDate(1, startDate);
            statement.setDate(2, endDate);
            statement.setInt(3, datasetId);
            statement.setInt(4, num_versao);

            ResultSet result = statement.executeQuery();

            while (result.next()) {
                Date date = result.getDate("data");
                int views = result.getInt("visualizacoes");
                int downloads = result.getInt("downloads");

                DadosAcesso d = new DadosAcesso(date, views, downloads);

                registros.add(d);
            }
        } catch (SQLException ex) {
            Logger.getLogger(PgRegistroAcessoDAO.class.getName()).log(Level.SEVERE, "DAO", ex);
            throw new SQLException("Erro ao buscar registros.");
        }

        return registros;
    }

    @Override
    public List<DadosAcesso> allDatasetAcessesBetween(Date startDate, Date endDate, int datasetId) throws SQLException {
        List<DadosAcesso> registros = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(READ_DATEDATASET_QUERY)) {
            statement.setDate(1, startDate);
            statement.setDate(2, endDate);
            statement.setInt(3, datasetId);

            ResultSet result = statement.executeQuery();

            while (result.next()) {
                Date date = result.getDate("data");
                Integer versao = result.getObject("versao", Integer.class);
                int views = result.getInt("visualizacoes");
                int downloads = result.getInt("downloads");

                DadosAcesso d = new DadosAcesso(date, versao, views, downloads);

                registros.add(d);
            }
        } catch (SQLException ex) {
            Logger.getLogger(PgRegistroAcessoDAO.class.getName()).log(Level.SEVERE, "DAO", ex);
            throw new SQLException("Erro ao buscar registros.");
        }

        return registros;
    }

    @Override
    public List<DadosAcesso> topVersionViews(Date startDate, Date endDate, int datasetId) throws SQLException {
        List<DadosAcesso> registros = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(READ_VIEWSVERSION_QUERY)) {
            statement.setDate(1, startDate);
            statement.setDate(2, endDate);
            statement.setInt(3, datasetId);

            ResultSet result = statement.executeQuery();

            while (result.next()) {
                Integer versao = result.getObject("versao", Integer.class);
                int views = result.getInt("visualizacoes");

                DadosAcesso d = new DadosAcesso(null, versao, views, 0);

                registros.add(d);
            }
        } catch (SQLException ex) {
            Logger.getLogger(PgRegistroAcessoDAO.class.getName()).log(Level.SEVERE, "DAO", ex);
            throw new SQLException("Erro ao buscar registros.");
        }

        return registros;
    }

    @Override
    public List<DadosAcesso> topVersionDownloads(Date startDate, Date endDate, int datasetId) throws SQLException {
        List<DadosAcesso> registros = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(READ_DOWNLOADSVERSION_QUERY)) {
            statement.setDate(1, startDate);
            statement.setDate(2, endDate);
            statement.setInt(3, datasetId);

            ResultSet result = statement.executeQuery();

            while (result.next()) {
                Integer versao = result.getObject("versao", Integer.class);
                int downloads = result.getInt("downloads");

                DadosAcesso d = new DadosAcesso(null, versao, 0, downloads);

                registros.add(d);
            }
        } catch (SQLException ex) {
            Logger.getLogger(PgRegistroAcessoDAO.class.getName()).log(Level.SEVERE, "DAO", ex);
            throw new SQLException("Erro ao buscar registros.");
        }

        return registros;
    }
}