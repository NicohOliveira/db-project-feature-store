/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dskaster (Adaptado para Relatórios Registro de Acessos)
 */

public class PgRegistroAcessoDAO implements RegistroAcessoDAO {

    private final Connection connection;

    private static final String INSERT_QUERY =
            "INSERT INTO Registro_Acesso (data_acesso, hora_acesso, " +
                    "tipo_acao, username_leitor, " +
                    "id_dataset_acessada, num_versao_acessada) " +
                    "VALUES (CURRENT_DATE, CURRENT_TIME, ?, ?, ?, ?);";

    public PgRegistroAcessoDAO(Connection connection) {
        this.connection = connection;
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
}