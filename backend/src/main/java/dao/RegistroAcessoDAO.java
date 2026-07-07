/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package dao;

import java.sql.SQLException;
import java.util.List;
import java.sql.Date;

import model.DadosAcesso;
import model.RegistroAcesso;

public interface RegistroAcessoDAO extends DAO<RegistroAcesso> {

    void registrar(int idDataset, int numVersao, String username, String tipoAcao) throws SQLException;

    List<DadosAcesso> allVersionAcessesBetween(Date startDate, Date endDate, int datasetId, int num_versao) throws SQLException;
    List<DadosAcesso> allDatasetAcessesBetween(Date startDate, Date endDate, int datasetId) throws SQLException;
}