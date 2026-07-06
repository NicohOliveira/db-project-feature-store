package model;

public class Feature {
    private int idDataset;
    private int numVersao;
    private String nomeColuna;
    private String tipoDado;
    private String descricao;

    public Feature() {}

    public Feature(int idDataset, int numVersao, String nomeColuna, String tipoDado, String descricao) {
        this.idDataset = idDataset;
        this.numVersao = numVersao;
        this.nomeColuna = nomeColuna;
        this.tipoDado = tipoDado;
        this.descricao = descricao;
    }


    public int getIdDataset() { return idDataset; }
    public int getNumVersao() { return numVersao; }
    public String getNomeColuna() { return nomeColuna; }
    public String getTipoDado() { return tipoDado; }
    public String getDescricao() { return descricao; }
    
    public void setIdDataset(int idDataset) { this.idDataset = idDataset; }
    public void setNumVersao(int numVersao) { this.numVersao = numVersao; }
    public void setNomeColuna(String nomeColuna) { this.nomeColuna = nomeColuna; }
    public void setTipoDado(String tipoDado) { this.tipoDado = tipoDado; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
}