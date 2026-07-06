package model;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

public class Versao {
    private int idDataset;
    private int numVersao;
    private String arquivoCsv;
    private List<Feature> features;

    private int nivelMaturidade;
    private Date dataRegistro;
    private Time horaRegistro;
    private String descricaoModificacoes;
    private String usernameAutor;
    private int idDatasetBase;
    private int numVersaoBase;

    public Versao() {}
    public void setIdDataset(int idDataset) { this.idDataset = idDataset; }
    public void setNumVersao(int numVersao) { this.numVersao = numVersao; }
    public void setArquivoCsv(String arquivoCsv) { this.arquivoCsv = arquivoCsv; }
    public void setFeatures(List<Feature> features) {
        this.features = features;
    }

    public void setNivelMaturidade(int nivelMaturidade) { this.nivelMaturidade = nivelMaturidade; }
    public void setDataRegistro(Date dataRegistro) { this.dataRegistro = dataRegistro; }
    public void setHoraRegistro(Time horaRegistro) { this.horaRegistro = horaRegistro; }
    public void setDescricaoModificacoes(String descricaoModificacoes) { this.descricaoModificacoes = descricaoModificacoes; }
    public void setUsernameAutor(String usernameAutor) { this.usernameAutor = usernameAutor; }
    public void setIdDatasetBase(int idDatasetBase) { this.idDatasetBase = idDatasetBase; }
    public void setNumVersaoBase(int numVersaoBase) { this.numVersaoBase = numVersaoBase; }

    // Getters
    public int getIdDataset() { return idDataset; }
    public int getNumVersao() { return numVersao; }
    public String getArquivoCsv() { return arquivoCsv; }

    public List<Feature> getFeatures() {
        return features;
    }

    public int getNivelMaturidade() { return nivelMaturidade; }
    public Date getDataRegistro() { return dataRegistro; }
    public Time getHoraRegistro() { return horaRegistro; }
    public String getDescricaoModificacoes() { return descricaoModificacoes; }
    public String getUsernameAutor() { return usernameAutor; }
    public int getIdDatasetBase() { return idDatasetBase; }
    public int getNumVersaoBase() { return numVersaoBase; }
}