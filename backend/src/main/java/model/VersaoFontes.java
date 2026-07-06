package model;

public class VersaoFontes {
    private int idDataset;
    private int numVersao;
    private String fonte;

    public VersaoFontes(int idDataset, int numVersao, String fonte) {
        this.idDataset = idDataset;
        this.numVersao = numVersao;
        this.fonte = fonte;
    }

    // Set
    public void setIdDataset(int idDataset){
        this.idDataset = idDataset;
    }
    public void setNumVersao(int numVersao){
        this.numVersao = numVersao;
    }
    public void setFonte(String fonte){
        this.fonte = fonte;
    }

    // Get
    public int getIdDataset(){
        return idDataset;
    }
    public int getNumVersao(){
        return numVersao;
    }
    public String getFonte(){
        return fonte;
    }
    
}