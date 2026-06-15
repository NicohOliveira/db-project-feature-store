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
import model.Versao;

/**
 *
 * @author dskaster
 */
public class PgVersaoDAO implements VersaoDAO {

    private final Connection connection;

    public PgVersaoDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void create(Versao t) throws SQLException {
        /*     try (PreparedStatement statement = connection.prepareStatement(CREATE_QUERY)) {
            //statement.setString(1, String.valueOf(dataset.getId()));
            statement.setString(1, dataset.getNome());
            statement.setString(2, dataset.getUsernameCriador());

            statement.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(PgDatasetDAO.class.getName()).log(Level.SEVERE, "DAO", ex);

            // fazer os errorMsg

            //so erro de criação pq aceita nome igual kk
            throw new SQLException("Erro ao criar repositório: " + ex.getMessage());
        }COMENTANDO POIS USEI O DO DATASET DE BASE*/
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
        String sql = "SELECT * FROM Versao WHERE id_dataset = ? AND num_versao = ?;";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
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
    public void delete(String id) throws SQLException {
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

    @Override
    public List<Versao> all() throws SQLException {
        //retorno so pro dao nao chiar
        return new ArrayList<>();
    }

    // metodos especificos que irei usar pratentar fazer minha árte

    @Override
    public List<Versao> listByDataset(int idDataset) throws SQLException {
        List<Versao> historico = new ArrayList<>();
        String sql = "SELECT * FROM Versao WHERE id_dataset = ? ORDER BY num_versao ASC;";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
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