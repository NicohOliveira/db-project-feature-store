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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dskaster
 */

public class PgRelatorioDAO implements RelatorioDAO {

    private final Connection connection;

    private static final String GLOBAL_DATASETS_QUERY = "SELECT COUNT(*) as total FROM Dataset;";
    private static final String GLOBAL_VERSIONS_QUERY = "SELECT COUNT(*) as total FROM Versao;";
    private static final String GLOBAL_ACTIONS_QUERY = "SELECT tipo_acao, COUNT(*) as qtd FROM Registro_Acesso GROUP BY tipo_acao;";

    private static final String GLOBAL_CONTRIBUTORS_QUERY =
            "SELECT username_autor, COUNT(*) as criacoes FROM Versao " +
                    "GROUP BY username_autor ORDER BY criacoes DESC LIMIT 5 OFFSET ?;";

    private static final String DATASET_VERSIONS_QUERY = "SELECT COUNT(*) as total FROM Versao WHERE id_dataset = ?;";
    private static final String DATASET_ACTIONS_QUERY = "SELECT tipo_acao, COUNT(*) as qtd FROM Registro_Acesso WHERE id_dataset_acessada = ? GROUP BY tipo_acao;";

    private static final String DATASET_CONTRIBUTORS_QUERY =
            "SELECT username_autor, COUNT(*) as criacoes FROM Versao " +
                    "WHERE id_dataset = ? GROUP BY username_autor ORDER BY criacoes DESC LIMIT 5 OFFSET ?;";

    public PgRelatorioDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Map<String, Object> getEstatisticasGerais(int pageContrib, int pageDatasets) throws SQLException {
        Map<String, Object> stats = new HashMap<>();
        int offsetContrib = (pageContrib - 1) * 5;

        try {

            try (PreparedStatement stmt = connection.prepareStatement(GLOBAL_DATASETS_QUERY);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) stats.put("totalDatasets", rs.getInt("total"));
            }


            try (PreparedStatement stmt = connection.prepareStatement(GLOBAL_VERSIONS_QUERY);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) stats.put("totalVersoes", rs.getInt("total"));
            }


            int totalVis = 0, totalDown = 0;
            try (PreparedStatement stmt = connection.prepareStatement(GLOBAL_ACTIONS_QUERY);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    if ("VISUALIZACAO".equals(rs.getString("tipo_acao"))) totalVis = rs.getInt("qtd");
                    else if ("DOWNLOAD".equals(rs.getString("tipo_acao"))) totalDown = rs.getInt("qtd");
                }
            }
            stats.put("totalVisualizacoes", totalVis);
            stats.put("totalDownloads", totalDown);

            List<Map<String, Object>> contribuidoresGlobais = new ArrayList<>();
            try (PreparedStatement stmt = connection.prepareStatement(GLOBAL_CONTRIBUTORS_QUERY)) {
                stmt.setInt(1, offsetContrib);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> user = new HashMap<>();
                        user.put("usuario", rs.getString("username_autor"));
                        user.put("criacoes", rs.getInt("criacoes"));
                        contribuidoresGlobais.add(user);
                    }
                }
            }
            stats.put("rankingContribuidoresGlobais", contribuidoresGlobais);

        } catch (SQLException ex) {
            Logger.getLogger(PgRelatorioDAO.class.getName()).log(Level.SEVERE, "DAO", ex);
            throw new SQLException("Erro ao gerar estatísticas globais.");
        }
        return stats;
    }

    @Override
    public Map<String, Object> getEstatisticasDataset(int idDataset, int pageContrib, int pageVersoes) throws SQLException {
        Map<String, Object> stats = new HashMap<>();
        int offsetContrib = (pageContrib - 1) * 5;

        try {

            try (PreparedStatement stmt = connection.prepareStatement(DATASET_VERSIONS_QUERY)) {
                stmt.setInt(1, idDataset);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) stats.put("totalVersoes", rs.getInt("total"));
                }
            }


            int totalVis = 0, totalDown = 0;
            try (PreparedStatement stmt = connection.prepareStatement(DATASET_ACTIONS_QUERY)) {
                stmt.setInt(1, idDataset);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        if ("VISUALIZACAO".equals(rs.getString("tipo_acao"))) totalVis = rs.getInt("qtd");
                        else if ("DOWNLOAD".equals(rs.getString("tipo_acao"))) totalDown = rs.getInt("qtd");
                    }
                }
            }
            stats.put("visualizacoes", totalVis);
            stats.put("downloads", totalDown);

            List<Map<String, Object>> contribuidores = new ArrayList<>();
            try (PreparedStatement stmt = connection.prepareStatement(DATASET_CONTRIBUTORS_QUERY)) {
                stmt.setInt(1, idDataset);
                stmt.setInt(2, offsetContrib);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> user = new HashMap<>();
                        user.put("usuario", rs.getString("username_autor"));
                        user.put("criacoes", rs.getInt("criacoes"));
                        contribuidores.add(user);
                    }
                }
            }
            stats.put("contribuidores", contribuidores);

        } catch (SQLException ex) {
            Logger.getLogger(PgRelatorioDAO.class.getName()).log(Level.SEVERE, "DAO", ex);
            throw new SQLException("Erro ao gerar estatísticas específicas do dataset.");
        }
        return stats;
    }

    @Override
    public Map<Integer, Map<String, Integer>> getEstatisticasTodasVersoes(int idDataset) throws SQLException {
        Map<Integer, Map<String, Integer>> stats = new HashMap<>();
        String query = "SELECT num_versao_acessada, " +
                "SUM(CASE WHEN tipo_acao = 'VISUALIZACAO' THEN 1 ELSE 0 END) as vis, " +
                "SUM(CASE WHEN tipo_acao = 'DOWNLOAD' THEN 1 ELSE 0 END) as down " +
                "FROM Registro_Acesso WHERE id_dataset_acessada = ? GROUP BY num_versao_acessada;";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, idDataset);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Integer> counts = new HashMap<>();
                    counts.put("visualizacoes", rs.getInt("vis"));
                    counts.put("downloads", rs.getInt("down"));
                    stats.put(rs.getInt("num_versao_acessada"), counts);
                }
            }
        } catch (SQLException ex) {
            throw new SQLException("Erro ao buscar estatísticas de todas as versões do dataset.");
        }
        return stats;
    }
}