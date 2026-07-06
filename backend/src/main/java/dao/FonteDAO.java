/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package dao;

import java.sql.SQLException;
import java.util.List;

import model.VersaoFontes;

public interface FonteDAO extends DAO<VersaoFontes> {
    public List<VersaoFontes> allDatasetVersionSources(int datasetId, int num_versao) throws SQLException;
}
