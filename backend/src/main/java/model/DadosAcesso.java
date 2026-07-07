package model;

import java.util.Date;

public class DadosAcesso {
    private Date data;
    private Integer versao;
    private int visualizacoes;
    private int downloads;

    public DadosAcesso(Date data, Integer versao, int visualizacoes, int downloads) {
        this.data = data;
        this.visualizacoes = visualizacoes;
        this.downloads = downloads;
        this.versao = versao;
    }

    public DadosAcesso(Date data, int visualizacoes, int downloads) {
        this.data = data;
        this.visualizacoes = visualizacoes;
        this.downloads = downloads;
        this.versao = null;
    }

    // Set
    public void setData(Date data) {
        this.data = data;
    }
    public void setVisualizacoes(int visualizacoes) {
        this.visualizacoes = visualizacoes;
    }
    public void setDownloads(int downloads) {
        this.downloads = downloads;
    }
    public void setVersao(Integer num_versao) {
        this.versao = num_versao;
    }

    // Get
    public Date getData() {
        return data;
    }
    public int getVisualizacoes() {
        return visualizacoes;
    }
    public int getDownloads() {
        return downloads;
    }
    public Integer getVersao() {
        return versao;
    }
}