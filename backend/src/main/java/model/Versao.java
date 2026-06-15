package model;

import java.sql.Date;
import java.sql.Time;

public class Versao {
    private int idDataset;
    private int numVersao;
    private String arquivoCsv;
    private String detalhesFeature;
    private int nivelMaturidade;
    private Date dataRegistro;
    private Time horaRegistro;
    private String descricaoModificacoes;
    private String usernameAutor;
    private int idDatasetBase;
    private int numVersaoBase;
    public Versao() {}

    public Versao(int idDataset, int numVersao, String arquivoCsv, String detalhesFeature,
                  int nivelMaturidade, Date dataRegistro, Time horaRegistro,
                  String descricaoModificacoes, String usernameAutor,
                  int idDatasetBase, int numVersaoBase) {
        this.idDataset = idDataset;
        this.numVersao = numVersao;
        this.arquivoCsv = arquivoCsv;
        this.detalhesFeature = detalhesFeature;
        this.nivelMaturidade = nivelMaturidade;
        this.dataRegistro = dataRegistro;
        this.horaRegistro = horaRegistro;
        this.descricaoModificacoes = descricaoModificacoes;
        this.usernameAutor = usernameAutor;
        this.idDatasetBase = idDatasetBase;
        this.numVersaoBase = numVersaoBase;
    }

    //get e set

    public int getIdDataset() { return idDataset; }
    public void setIdDataset(int idDataset) { this.idDataset = idDataset; }

    public int getNumVersao() { return numVersao; }
    public void setNumVersao(int numVersao) { this.numVersao = numVersao; }

    public String getArquivoCsv() { return arquivoCsv; }
    public void setArquivoCsv(String arquivoCsv) { this.arquivoCsv = arquivoCsv; }

    public String getDetalhesFeature() { return detalhesFeature; }
    public void setDetalhesFeature(String detalhesFeature) { this.detalhesFeature = detalhesFeature; }

    public int getNivelMaturidade() { return nivelMaturidade; }
    public void setNivelMaturidade(int nivelMaturidade) { this.nivelMaturidade = nivelMaturidade; }

    public Date getDataRegistro() { return dataRegistro; }
    public void setDataRegistro(Date dataRegistro) { this.dataRegistro = dataRegistro; }

    public Time getHoraRegistro() { return horaRegistro; }
    public void setHoraRegistro(Time horaRegistro) { this.horaRegistro = horaRegistro; }

    public String getDescricaoModificacoes() { return descricaoModificacoes; }
    public void setDescricaoModificacoes(String descricaoModificacoes) { this.descricaoModificacoes = descricaoModificacoes; }

    public String getUsernameAutor() { return usernameAutor; }
    public void setUsernameAutor(String usernameAutor) { this.usernameAutor = usernameAutor; }

    public int getIdDatasetBase() { return idDatasetBase; }
    public void setIdDatasetBase(int idDatasetBase) { this.idDatasetBase = idDatasetBase; }

    public int getNumVersaoBase() { return numVersaoBase; }
    public void setNumVersaoBase(int numVersaoBase) { this.numVersaoBase = numVersaoBase; }
}