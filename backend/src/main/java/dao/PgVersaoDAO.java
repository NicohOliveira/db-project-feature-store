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

import model.Feature;
import model.Versao;

/**
 *
 * @author dskaster
 */
public class PgVersaoDAO implements VersaoDAO {

    private final Connection connection;

    private static final String CREATE_VERSAO_QUERY =
            "INSERT INTO Versao(id_dataset, num_versao, arquivo_csv, nivel_maturidade, data_registro, hora_registro, descricao_modificacoes, username_autor, id_dataset_base, num_versao_base) " +
                    "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

    private static final String CREATE_FEATURE_QUERY =
            "INSERT INTO Feature(id_dataset, num_versao, nome_coluna, tipo_dado, descricao) " +
                    "VALUES(?, ?, ?, ?, ?);";

    private static final String READ_QUERY =
            "SELECT * FROM Versao WHERE id_dataset = ? AND num_versao = ?;";

    private static final String READASC_QUERY =
            "SELECT * FROM Versao WHERE id_dataset = ? ORDER BY num_versao ASC;";

    private static final String READ_FEATURES_QUERY =
            "SELECT nome_coluna, tipo_dado, descricao FROM Feature WHERE id_dataset = ? AND num_versao = ?;";

    private static final String DELETE_QUERY =
            "DELETE FROM Versao WHERE id_dataset = ? AND num_versao = ? AND username_autor = ?;";

    private static final String READ_HISTORY_FILTER_QUERY =
            "SELECT * FROM Versao " +
                    "WHERE id_dataset = ? " +
                    "AND (?::varchar IS NULL OR UPPER(username_autor) LIKE UPPER(?) OR UPPER(descricao_modificacoes) LIKE UPPER(?)) " +
                    "AND (?::integer IS NULL OR nivel_maturidade = ?) " +
                    "AND (?::integer IS NULL OR num_versao_base = ?) " +
                    "AND (?::date IS NULL OR data_registro >= ?) " +
                    "AND (?::date IS NULL OR data_registro <= ?) " +
                    "ORDER BY num_versao DESC;";

    public PgVersaoDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void create(Versao versao) throws SQLException {
        // auto commit pra atualizar
        boolean originalAutoCommit = connection.getAutoCommit();

        try {
            //transacao pra novo modelo de feature, se falhar nao envia pela metade
            connection.setAutoCommit(false);
            try (PreparedStatement statement = connection.prepareStatement(CREATE_VERSAO_QUERY)) {
                statement.setInt(1, versao.getIdDataset());
                statement.setInt(2, versao.getNumVersao());
                statement.setString(3, versao.getArquivoCsv());
                statement.setInt(4, versao.getNivelMaturidade());
                statement.setDate(5, versao.getDataRegistro());
                statement.setTime(6, versao.getHoraRegistro());
                statement.setString(7, versao.getDescricaoModificacoes());
                statement.setString(8, versao.getUsernameAutor());

                if (versao.getNumVersaoBase() == 0) {
                    statement.setNull(9, java.sql.Types.INTEGER);
                    statement.setNull(10, java.sql.Types.INTEGER);
                } else {
                    statement.setInt(9, versao.getIdDatasetBase());
                    statement.setInt(10, versao.getNumVersaoBase());
                }

                statement.executeUpdate();
            }
            if (versao.getFeatures() != null && !versao.getFeatures().isEmpty()) {
                System.out.println(">>> [DAO] PREPARANDO PARA INSERIR " + versao.getFeatures().size() + " FEATURES");

                try (PreparedStatement stmtFeature = connection.prepareStatement(CREATE_FEATURE_QUERY)) {
                    for (Feature f : versao.getFeatures()) {
                        stmtFeature.setInt(1, versao.getIdDataset());
                        stmtFeature.setInt(2, versao.getNumVersao());
                        stmtFeature.setString(3, f.getNomeColuna());
                        stmtFeature.setString(4, f.getTipoDado());
                        stmtFeature.setString(5, f.getDescricao());

                        stmtFeature.executeUpdate();
                        System.out.println(">>> [DAO] FEATURE '" + f.getNomeColuna() + "' INSERIDA COM SUCESSO!");
                    }
                }
            } else {
                System.out.println(">>> [DAO] NENHUMA FEATURE RECEBIDA PARA SALVAR.");
            }
            connection.commit();

        } catch (SQLException ex) {
            connection.rollback(); // cancela se der erro
            Logger.getLogger(PgVersaoDAO.class.getName()).log(Level.SEVERE, "DAO", ex);
            throw new SQLException("Erro ao criar versão e features: " + ex.getMessage());
        } finally {
            connection.setAutoCommit(originalAutoCommit);
        }
    }

    private List<Feature> buscarFeatures(int idDataset, int numVersao) throws SQLException {
        List<Feature> features = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(READ_FEATURES_QUERY)) {
            stmt.setInt(1, idDataset);
            stmt.setInt(2, numVersao);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Feature f = new Feature(
                            idDataset,
                            numVersao,
                            rs.getString("nome_coluna"),
                            rs.getString("tipo_dado"),
                            rs.getString("descricao")
                    );
                    features.add(f);
                }
            }
        }
        return features;
    }

    @Override
    public Versao read(String id) throws SQLException {
        String[] partes = id.split("-");

        if (partes.length != 2) {
            throw new IllegalArgumentException("Formato de ID inválido. Utilize 'idDataset-numVersao'.");
        }

        int idDataset;
        int numVersao;

        try {
            idDataset = Integer.parseInt(partes[0]);
            numVersao = Integer.parseInt(partes[1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Os IDs da Versão devem ser numéricos.");
        }

        Versao v = null;

        try (PreparedStatement stmt = connection.prepareStatement(READ_QUERY)) {
            stmt.setInt(1, idDataset);
            stmt.setInt(2, numVersao);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    v = new Versao();
                    v.setIdDataset(rs.getInt("id_dataset"));
                    v.setNumVersao(rs.getInt("num_versao"));
                    v.setArquivoCsv(rs.getString("arquivo_csv"));
                    v.setNivelMaturidade(rs.getInt("nivel_maturidade"));
                    v.setDataRegistro(rs.getDate("data_registro"));
                    v.setHoraRegistro(rs.getTime("hora_registro"));
                    v.setDescricaoModificacoes(rs.getString("descricao_modificacoes"));
                    v.setUsernameAutor(rs.getString("username_autor"));
                    v.setIdDatasetBase(rs.getInt("id_dataset_base"));
                    v.setNumVersaoBase(rs.getInt("num_versao_base"));
                    v.setFeatures(buscarFeatures(v.getIdDataset(), v.getNumVersao()));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(PgVersaoDAO.class.getName()).log(Level.SEVERE, "DAO", ex);
            throw new SQLException("Erro ao buscar a versão: " + ex.getMessage());
        }

        return v;
    }

    @Override
    public List<Versao> listByDataset(int idDataset) throws SQLException {
        List<Versao> historico = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(READASC_QUERY)) {
            stmt.setInt(1, idDataset);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Versao v = new Versao();
                    v.setIdDataset(rs.getInt("id_dataset"));
                    v.setNumVersao(rs.getInt("num_versao"));
                    v.setArquivoCsv(rs.getString("arquivo_csv"));
                    v.setNivelMaturidade(rs.getInt("nivel_maturidade"));
                    v.setDataRegistro(rs.getDate("data_registro"));
                    v.setHoraRegistro(rs.getTime("hora_registro"));
                    v.setDescricaoModificacoes(rs.getString("descricao_modificacoes"));
                    v.setUsernameAutor(rs.getString("username_autor"));
                    v.setIdDatasetBase(rs.getInt("id_dataset_base"));
                    v.setNumVersaoBase(rs.getInt("num_versao_base"));

                    v.setFeatures(buscarFeatures(v.getIdDataset(), v.getNumVersao()));

                    historico.add(v);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(PgVersaoDAO.class.getName()).log(Level.SEVERE, "DAO", ex);
            throw new SQLException("Erro ao listar histórico de versões: " + ex.getMessage());
        }
        return historico;
    }

    @Override
    public void update(Versao t) throws SQLException {
        // placeholder
    }

    @Override
    public void delete(String id) throws SQLException {
        throw new UnsupportedOperationException("Usar o delete(String id, String username) para exclusão segura.");
    }

    public void deleteV(String id, String usernameAutor) throws SQLException {
        String[] partes = id.split("-");
        int idDataset = Integer.parseInt(partes[0]);
        int numVersao = Integer.parseInt(partes[1]);

        try (PreparedStatement statement = connection.prepareStatement(DELETE_QUERY)) {
            statement.setInt(1, idDataset);
            statement.setInt(2, numVersao);
            statement.setString(3, usernameAutor);

            if (statement.executeUpdate() < 1) {
                throw new SQLException("Erro ao excluir: versão não encontrada ou sem permissão.");
            }
        } catch (SQLException ex) {
            Logger.getLogger(PgVersaoDAO.class.getName()).log(Level.SEVERE, "DAO", ex);
            throw new SQLException("Erro ao excluir versão: " + ex.getMessage());
        }
    }

    @Override
    public List<Versao> all() throws SQLException {
        return new ArrayList<>();
    }

    /**
     * Busca versões com filtros dinâmicos de texto, maturidade, versão base e datas.
     */
    public List<Versao> getHistoricoFiltrado(int idDataset, String texto, Integer maturidade, Integer versaoBase, java.sql.Date dataInicio, java.sql.Date dataFim) throws SQLException {
        List<Versao> versoes = new ArrayList<>();


        try (PreparedStatement stmt = connection.prepareStatement(READ_HISTORY_FILTER_QUERY)) {

            stmt.setInt(1, idDataset);
            if (texto != null && !texto.trim().isEmpty()) {
                String termo = "%" + texto + "%";
                stmt.setString(2, termo);
                stmt.setString(3, termo);
                stmt.setString(4, termo);
            } else {
                stmt.setNull(2, java.sql.Types.VARCHAR);
                stmt.setNull(3, java.sql.Types.VARCHAR);
                stmt.setNull(4, java.sql.Types.VARCHAR);
            }

            if (maturidade != null) {
                stmt.setInt(5, maturidade);
                stmt.setInt(6, maturidade);
            } else {
                stmt.setNull(5, java.sql.Types.INTEGER);
                stmt.setNull(6, java.sql.Types.INTEGER);
            }

            if (versaoBase != null) {
                stmt.setInt(7, versaoBase);
                stmt.setInt(8, versaoBase);
            } else {
                stmt.setNull(7, java.sql.Types.INTEGER);
                stmt.setNull(8, java.sql.Types.INTEGER);
            }

            if (dataInicio != null) {
                stmt.setDate(9, dataInicio);
                stmt.setDate(10, dataInicio);
            } else {
                stmt.setNull(9, java.sql.Types.DATE);
                stmt.setNull(10, java.sql.Types.DATE);
            }

            if (dataFim != null) {
                stmt.setDate(11, dataFim);
                stmt.setDate(12, dataFim);
            } else {
                stmt.setNull(11, java.sql.Types.DATE);
                stmt.setNull(12, java.sql.Types.DATE);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Versao v = new Versao();
                    v.setIdDataset(rs.getInt("id_dataset"));
                    v.setNumVersao(rs.getInt("num_versao"));
                    v.setArquivoCsv(rs.getString("arquivo_csv"));
                    v.setNivelMaturidade(rs.getInt("nivel_maturidade"));
                    v.setDataRegistro(rs.getDate("data_registro"));
                    v.setHoraRegistro(rs.getTime("hora_registro"));
                    v.setDescricaoModificacoes(rs.getString("descricao_modificacoes"));
                    v.setUsernameAutor(rs.getString("username_autor"));
                    v.setIdDatasetBase(rs.getInt("id_dataset_base"));
                    v.setNumVersaoBase(rs.getInt("num_versao_base"));
                    v.setFeatures(buscarFeatures(v.getIdDataset(), v.getNumVersao()));

                    versoes.add(v);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(PgVersaoDAO.class.getName()).log(Level.SEVERE, "Erro na busca filtrada", ex);
            throw new SQLException("Erro ao buscar histórico com filtros: " + ex.getMessage());
        }
        return versoes;
    }
}