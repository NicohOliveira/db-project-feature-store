/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package dao;

import java.sql.SQLException;
import java.util.Map;

public interface RelatorioDAO {
    Map<String, Object> getEstatisticasGerais(int pageContrib, int pageDatasets) throws SQLException;
    Map<String, Object> getEstatisticasDataset(int idDataset, int pageContrib, int pageVersoes) throws SQLException;
    Map<Integer, Map<String, Integer>> getEstatisticasTodasVersoes(int idDataset) throws SQLException;
}