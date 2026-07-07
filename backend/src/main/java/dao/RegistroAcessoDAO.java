/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package dao;

import java.sql.SQLException;

public interface RegistroAcessoDAO {
    void registrar(int idDataset, int numVersao, String username, String tipoAcao) throws SQLException;
}