/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package dao;

import java.sql.SQLException;
import java.util.List;
import model.Versao;

/**
 *
 * @author dskaster
 */
public interface VersaoDAO extends DAO<Versao> {
    public List<Versao> listByDataset(int idDataset) throws SQLException;
    public Versao read(String id) throws SQLException;
}