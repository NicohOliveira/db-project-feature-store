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

import model.Versao;

/**
 *
 * @author dskaster
 */
public class PgVersaoDAO implements VersaoDAO {

    private final Connection connection;
    
    private static final String CREATE_QUERY =
                                "INSERT INTO Versao(id_dataset, num_versao, arquivo_csv, detalhes_feature, nivel_maturidade, data_registro, hora_registro, descricao_modificacoes, username_autor, id_dataset_base, num_versao_base) " +
                                "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
    
    // private static final String ALL_QUERY =
    //                             "SELECT * " +
    //                             "FROM Versao " +
    //                             "ORDER BY username_criador;";

    private static final String READ_QUERY =
                                "SELECT * " +
                                "FROM Versao " +
                                "WHERE id_dataset = ? " +
                                "AND num_versao = ?;";

    private static final String READASC_QUERY =
                                "SELECT * " +
                                "FROM Versao " +
                                "WHERE id_dataset = ? " +
                                "ORDER BY num_versao ASC;";

    // private static final String UPDATE_QUERY =
    //                             "UPDATE dataset " +
    //                             "SET nome = ? " +
    //                             "WHERE id_dataset = ?;";

    // private static final String DELETE_QUERY =
    //                             "DELETE FROM Usuario " +
    //                             "WHERE username = ?;";

    public PgVersaoDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void create(Versao versao) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(CREATE_QUERY)) {
            statement.setInt(1, versao.getIdDataset());
            statement.setInt(2, versao.getNumVersao());
            statement.setString(3, versao.getArquivoCsv());
            statement.setString(4, versao.getDetalhesFeature());
            statement.setInt(5, versao.getNivelMaturidade());
            statement.setDate(6, versao.getDataRegistro());
            statement.setTime(7, versao.getHoraRegistro());
            statement.setString(8, versao.getDescricaoModificacoes());
            statement.setString(9, versao.getUsernameAutor());
            statement.setInt(10, versao.getIdDatasetBase());
            statement.setInt(11, versao.getNumVersaoBase());

            statement.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(PgDatasetDAO.class.getName()).log(Level.SEVERE, "DAO", ex);
            throw new SQLException("Erro ao criar versão: " + ex.getMessage());
        }
    }

    // vou usar read pra minha parte entao implementei
    @Override
    public Versao read(String id) throws SQLException {
        // vou usar split pq a chave é dupla

        String[] partes = id.split("-");

        if (partes.length != 2) {
            throw new IllegalArgumentException("Formato de ID inválido para Versão. Utilize 'idDataset-numVersao'.");
        }

        int idDataset;
        int numVersao;

        try {
            idDataset = Integer.parseInt(partes[0]);
            numVersao = Integer.parseInt(partes[1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("os IDs da Versão devem ser numéricos.");
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
                    v.setDetalhesFeature(rs.getString("detalhes_feature"));
                    v.setNivelMaturidade(rs.getInt("nivel_maturidade"));
                    v.setDataRegistro(rs.getDate("data_registro"));
                    v.setHoraRegistro(rs.getTime("hora_registro"));
                    v.setDescricaoModificacoes(rs.getString("descricao_modificacoes"));
                    v.setUsernameAutor(rs.getString("username_autor"));
                    v.setIdDatasetBase(rs.getInt("id_dataset_base"));
                    v.setNumVersaoBase(rs.getInt("num_versao_base"));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(PgVersaoDAO.class.getName()).log(Level.SEVERE, "DAO", ex);
            throw new SQLException("Erro ao buscar a versão: " + ex.getMessage());
        }

        return v;
    }

    @Override
    public void update(Versao t) throws SQLException {
        // placeholder
    }

    @Override
    public void delete(String id) throws SQLException {
        // placeholder
    }

    @Override
    public List<Versao> all() throws SQLException {
        // placeholder
        return new ArrayList<>();
    }

    // metodos especificos que irei usar pratentar fazer minha árte

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
                    v.setDetalhesFeature(rs.getString("detalhes_feature"));
                    v.setNivelMaturidade(rs.getInt("nivel_maturidade"));
                    v.setDataRegistro(rs.getDate("data_registro"));
                    v.setHoraRegistro(rs.getTime("hora_registro"));
                    v.setDescricaoModificacoes(rs.getString("descricao_modificacoes"));
                    v.setUsernameAutor(rs.getString("username_autor"));
                    v.setIdDatasetBase(rs.getInt("id_dataset_base"));
                    v.setNumVersaoBase(rs.getInt("num_versao_base"));

                    historico.add(v);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(PgVersaoDAO.class.getName()).log(Level.SEVERE, "DAO", ex);
            throw new SQLException("Erro ao listar histórico de versões: " + ex.getMessage());
        }
        return historico;
    }
}